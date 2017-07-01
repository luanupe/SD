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

public class RequisitadorCallback extends Thread implements RequisicaoCBInterface, Runnable {

	public static final long TENTAR_NOVAMENTE = 5000;

	/*  */

	private ServidorCache cache;
	private Map<String, Long> pings;
	private Map<String, InterfaceResposta> respostas;

	private Map<Requisicao, ServidorConhecido> historico;
	private List<Requisicao> pendentes;

	public RequisitadorCallback() {
		this.cache = new ServidorCache();
		this.pings = new ConcurrentHashMap<String, Long>();
		this.historico = new ConcurrentHashMap<Requisicao, ServidorConhecido>();
		this.respostas = new HashMap<String, InterfaceResposta>();
		this.pendentes = new CopyOnWriteArrayList<Requisicao>();
	}

	@Override
	public void init() {
		this.respostas.put("pong", new RespostaPong());
		this.respostas.put("adicao", new RespostaAdicao());
		this.respostas.put("subtracao", new RespostaSubtracao());
		this.respostas.put("fatorial", new RespostaFatorial());

		// Inicia a Thread do Requisitador
		this.start();
	}

	public void run(String servico, JSONObject args, boolean... udp) {
		JSONObject requisicao = new JSONObject();
		requisicao.put("cmd", servico);
		requisicao.put("args", args);

		// Prepara a requisição (transação)
		ClientController.instance().preparar(requisicao, (udp.length == 0));
	}

	@Override
	public void run() {
		ClientController controller = ClientController.instance();
		for (Requisicao pendente : this.pendentes) {
			if ((pendente.isTCP())) {
				String servico = pendente.getServico();
				ServidorConhecido server = this.cache.getServidor(servico);

				if ((server == null)) {
					// Calculadora.instance().getOperador().ping(servico);
					this.controlePing(servico);
				} else {
					this.historico.put(pendente, server);
					this.pendentes.remove(pendente);
					controller.processarTCP(pendente, server.getHost(), server.getPorta());
				}
			} else { // Processa via UDP
				this.pendentes.remove(pendente);
				controller.processarUDP(pendente);
			}
		}

		// Pausa a Thread
		this.pausarThread();

		// Chamada recursiva
		this.run();
	}

	private synchronized void controlePing(String servico) {
		Long cache = this.pings.get(servico);
		long agora = System.currentTimeMillis();

		if ((cache == null) || ((agora - cache) > TENTAR_NOVAMENTE)) {
			this.pings.put(servico, agora);
			Calculadora.instance().getOperador().ping(servico);
		}
	}

	/*
	 * Se não tiver requisições pendentes para a Thread indefinidamente
	 * se tiver requisições espera x segundos e tenta novamente.
	 * CARACTERÍSTICA: TOLERÂNCIA A FALHAS
	 */
	private synchronized void pausarThread() {
		try {
			if ((this.pendentes.isEmpty())) {
				this.wait();
			} else {
				this.wait(RequisitadorCallback.TENTAR_NOVAMENTE);
			}
		} catch (InterruptedException e) {
			
		}
	}

	// Pra poder acordar a Thread de outros objetos
	public synchronized void continuarThread() {
		this.notify();
	}

	@Override
	public void preparado(Requisicao requisicao) {
		if ((this.pendentes.contains(requisicao) == false)) {
			this.pendentes.add(requisicao);
		}
		this.continuarThread();
	}

	@Override
	public void enviado(Requisicao requisicao) {
		// TODO Requisição enviada com sucesso
	}

	@Override
	public void falha(Requisicao requisicao) {
		// Adiciona a requisição de volta na pilha
		// CARACTERÍSTICA: TOLERÂNCIA A FALHAS

		ServidorConhecido server = this.historico.get(requisicao);
		if ((server != null)) {
			this.getCache().remover(requisicao.getServico(), server);
		}
		this.preparado(requisicao);
	}

	/*
	 * Todas as repostas que chegam da rede passam por aqui, então
	 * é aqui que processa a resposta do microserviço.
	 */
	@Override
	public void recebido(String cmd, JSONObject args) {
		InterfaceResposta resposta = this.respostas.get(cmd);
		if ((resposta != null)) {
			resposta.run(args);
		}
	}

	public ServidorCache getCache() {
		return this.cache;
	}

}
