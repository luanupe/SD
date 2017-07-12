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

	public static boolean DEBUG = false;
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

	public void shutdown() {
		this.udp.shutdown();
		for (TCPConexao conexao : this.tcp.values()) {
			conexao.shutdown();
		}
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
			TCPConexao conexao = new TCPConexao(host, porta);
			ClientController.debug(conexao.toString() + " > Conectando...");
			conexao.init(); // Inicia socket, threads...
			conexao.enviar(requisicao); // Envia a mensagem
			
			// Confirma o sucesso no envio
			this.getCallback().sucesso(requisicao);
		} catch (IOException e) {
			this.getCallback().falha(requisicao, e.getMessage()); // Tolerância a falha
		}
	}

	public void desconectar(TCPConexao conexao) {
		this.tcp.remove(conexao.toString());
	}

	public RequisicaoCBInterface getCallback() {
		return this.callback;
	}

}
