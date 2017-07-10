package distribuidos.sistemas.core;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import distribuidos.sistemas.TCP.TCPConexao;
import distribuidos.sistemas.UDP.UDPServer;
import distribuidos.sistemas.requisicoes.Requisicao;
import distribuidos.sistemas.requisicoes.RequisicaoCBInterface;
import net.sf.json.JSONObject;

public class ClientController {

	public static final boolean DEBUG = false;
	public static final Random SEED = new Random(System.currentTimeMillis());

	public static final void debug(String mensagem) {
		if ((ClientController.DEBUG)) {
			System.out.println(mensagem);
		}
	}

	public static final void debugError(String mensagem) {
		if ((ClientController.DEBUG)) {
			System.err.println(mensagem);
		}
	}

	/* Singleton */

	private static ClientController INSTANCE;

	public static ClientController instance() {
		return ClientController.INSTANCE;
	}

	/* Fim Singleton */

	private RequisicaoCBInterface callback; // Interfdace implementada pela própria calculadora
	private int contador;
	private UDPServer udp;
	private Map<String, TCPConexao> tcp;

	public ClientController(RequisicaoCBInterface callback, String grupo, int porta) {
		this.callback = callback;
		this.udp = new UDPServer(grupo, porta);
		this.tcp = new ConcurrentHashMap<String, TCPConexao>();
	}

	public void init() throws IOException {
		ClientController.INSTANCE = this;
		this.callback.init();
		this.udp.init();
	}

	public synchronized void preparar(JSONObject mensagem, boolean tcp) {
		Requisicao requisicao = new Requisicao(++this.contador, mensagem, tcp);
		this.getCallback().preparado(requisicao); // Requisição pronta
	}

	/* CARACTERÍSTICA: Tolerância a falhas
	 * Client só envia UDP se for multicast, então não precisa
	 * saber quem é o destinatário (host/porta)
	 * 
	 * Tenta enviar e confirma se houve falha (Exception)
	 * ou foi enviado com sucesso */
	public void processarUDP(Requisicao requisicao) {
		try {
			this.udp.enviar(requisicao);
			this.getCallback().sucesso(requisicao);
		} catch (IOException e) {
			this.getCallback().falha(requisicao, e.getMessage());
		}
	}

	/* CARACTERÍSTICA: Tolerância a falhas
	 * Se for TCP precisa saber quem é o destinatário, e a camada da
	 * calculadora que é responsável por definir pra onde a mensagem vai.
	 * 
	 * Tenta conectar ao host:porta - Se não conseguir conectar ou der
	 * falha pra enviar (Exception) vai avisar que falhou, se conseguir
	 * vai avisar que teve sucesso */
	public void processarTCP(Requisicao requisicao, String host, int porta) {
		try {
			TCPConexao conexao = this.conectar(host, porta);
			conexao.enviar(requisicao);
			
			// Confirma o sucesso no envio
			this.getCallback().sucesso(requisicao);
		} catch (IOException e) {
			this.getCallback().falha(requisicao, e.getMessage()); // Tolerância a falha
		}
	}

	private TCPConexao conectar(String host, int porta) throws IOException {
		StringBuilder info = new StringBuilder();
		info.append(host).append(":").append(porta);

		// Cria String de Conexão, exemplo: 127.0.0.1:1234
		String server = info.toString();
		ClientController.debug(server + " > Conectando...");

		// Verifica se já existe conexão pra evitar criar novos Sockets
		TCPConexao conexao = this.tcp.get(info.toString());
		if ((conexao == null)) {
			conexao = new TCPConexao(host, porta);
			conexao.init(); // Inicia socket, threads...
			this.tcp.put(conexao.toString(), conexao);
		}

		return conexao;
	}

	public void desconectar(TCPConexao conexao) {
		this.tcp.remove(conexao.toString());
	}

	public RequisicaoCBInterface getCallback() {
		return this.callback;
	}

}
