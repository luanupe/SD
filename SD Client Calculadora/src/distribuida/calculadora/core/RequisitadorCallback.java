package distribuida.calculadora.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import distribuida.calculadora.cache.ServidorCache;
import distribuida.calculadora.cache.ServidorConhecido;
import distribuida.calculadora.respostas.InterfaceResposta;
import distribuida.calculadora.respostas.ativas.*;
import distribuidos.sistemas.core.ClientController;
import distribuidos.sistemas.requisicoes.Requisicao;
import distribuidos.sistemas.requisicoes.RequisicaoCBInterface;
import net.sf.json.JSONObject;

public class RequisitadorCallback extends Thread implements RequisicaoCBInterface {

	private ServidorCache cache; // Cache de microservi�os
	private List<Requisicao> pendentes;
	private Map<String, InterfaceResposta> respostas; // Respostas dos servi�os
	private Map<Requisicao, ServidorConhecido> historico;

	public RequisitadorCallback() {
		this.cache = new ServidorCache();
		this.historico = new ConcurrentHashMap<Requisicao, ServidorConhecido>();
		this.respostas = new HashMap<String, InterfaceResposta>();
		this.pendentes = new CopyOnWriteArrayList<Requisicao>();
	}

	@Override
	public void init() {
		// Microrespostas dos microservi�os
		this.respostas.put("pong", new RespostaPong());
		this.respostas.put("adicao", new RespostaAdicao());
		this.respostas.put("subtracao", new RespostaSubtracao());
		this.respostas.put("fatorial", new RespostaFatorial());
		this.respostas.put("quadrado", new RespostaQuadrado());

		this.start(); // Inicia a Thread do Requisitador
	}

	public void run(String servico, JSONObject args, boolean... udp) {
		JSONObject requisicao = new JSONObject();
		requisicao.put("cmd", servico);
		requisicao.put("args", args);

		// Prepara a requisi��o (transa��o)
		ClientController.instance().preparar(requisicao, (udp.length == 0));
	}

	public synchronized void acordar() {
		this.notifyAll();
	}

	/* CARACTER�STICA: TRANSPAR�NCIA DE LOCALIZA��O (MICROSERVI�OS)
	 * CARACTER�STICA: TOLER�NCIA A FALHAS
	 * 
	 * Aqui � onde a m�gica realmente acontece: todos as requisi��es
	 * pendentes v�o ficar sendo iteradas, se algum servi�o n�o for
	 * conhecido vai ser realizado o multicast UDP para encontrar
	 * algum n� dispon�vel, se der falhar no envio, a requisi��o vai
	 * ser readicionada na fila de pendentes at� que algum microservi�o
	 * esteja dispon�vel. */
	public void run() {
		try {
			ClientController controller = ClientController.instance();
			while (true) {
				for (Requisicao pendente : this.pendentes) {
					if ((pendente.isTCP())) { // Processamento TCP
						ServidorConhecido server = this.cache.getServidor(pendente.getServico());
						if ((server == null)) { // Nenhum servidor pro microservi�o
							this.falha(pendente, "Nenhum microservi�o localizado.");
						} else {
							this.pendentes.remove(pendente);
							this.historico.put(pendente, server);
							controller.processarTCP(pendente, server.getHost(), server.getPorta());
						}
					} else { // Processamento UDP
						this.pendentes.remove(pendente);
						controller.processarUDP(pendente);
					}
				}

				synchronized (this) {
					this.wait(2000);
				}
			}
		} catch (InterruptedException e) {
			// TODO Interrompeu... o que fazer?
		}
	}

	public ServidorCache getCache() {
		return this.cache;
	}

	public int getQuantidadePendentes() {
		return this.pendentes.size();
	}

	private void adicionarPendente(Requisicao requisicao) {
		ClientController.debug("Requisi��o #" + requisicao.getId() + " adicionada a fila.");
		if ((this.pendentes.contains(requisicao) == false)) {
			this.pendentes.add(requisicao);
		}
	}

	private void removerCache(Requisicao requisicao) {
		ServidorConhecido server = this.historico.get(requisicao);
		if ((server != null)) {
			this.getCache().remover(requisicao.getServico(), server);
		}
	}

	/* M�TODOS DA INTERFACE CALLBACK */

	// Adiciona na pilha, requisi��o pronta pra ser processada
	public void preparado(Requisicao requisicao) {
		this.adicionarPendente(requisicao);
		this.acordar(); // Para o wait da Thread
	}

	/* Enviado com sucesso, nao precisa fazer nada coloquei
	 * somente uma mensagem no debug pra n�o ficar em branco */
	public void sucesso(Requisicao requisicao) {
		ClientController.debug("Requisi��o #" + requisicao.getId() + " enviada sem falhas.");
		this.pendentes.remove(requisicao);
	}

	/* CARACTER�STICA: TOLER�NCIA A FALHAS
	 * Deu falha, pega o servidor e remove da cache pra
	 * evitar novas requisi��es com falha e faz ping novamente
	 * pra encontrar algum servidor com microservi�o */
	public void falha(Requisicao requisicao, String motivo) {
		ClientController.debug("Requisi��o #" + requisicao.getId() + " falhou: " + motivo);
		this.removerCache(requisicao);
		this.adicionarPendente(requisicao); // Coloca de volta na pilha
		Calculadora.instance().getOperador().ping(requisicao.getServico());
	}

	/* Todas as repostas que chegam da rede passam por aqui, ent�o
	 * � aqui que processa a resposta do microservi�o. */
	public void recebido(String cmd, JSONObject args) {
		InterfaceResposta resposta = this.respostas.get(cmd);
		if ((resposta != null)) {
			resposta.run(args);
		}
	}

}
