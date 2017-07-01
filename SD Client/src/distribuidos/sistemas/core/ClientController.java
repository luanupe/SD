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

	public static final Random SEED = new Random(System.currentTimeMillis());

	/*  */

	private static ClientController INSTANCE;

	/*
	 * Acesso global sem precisar passar por parâmento, quase
	 * uma classe padrão "sigleton"
	 */
	public static ClientController instance() {
		return ClientController.INSTANCE;
	}

	/*  */

	// O callback é enviado pela própria calculadora
	private RequisicaoCBInterface callback;
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
		this.getCallback().preparado(requisicao);
	}

	/*
	 * Client só envia UDP se for multicast, então não precisa
	 * saber quem é o destinatário (host/porta).
	 */
	public void processarUDP(Requisicao requisicao) {
		try {
			this.udp.enviar(requisicao);
			this.getCallback().enviado(requisicao);
		} catch (IOException e) {
			this.getCallback().falha(requisicao);
		}
	}

	/*
	 * Se for TCP precisa saber quem é o destinatário, e a camada da
	 * calculadora que é responsável por definir pra onde a mensagem vai.
	 */
	public void processarTCP(Requisicao requisicao, String host, int porta) {
		try {
			TCPConexao conexao = this.conectar(host, porta);
			conexao.enviar(requisicao);
			this.getCallback().enviado(requisicao);
		} catch (IOException e) {
			this.getCallback().falha(requisicao);
		}
	}

	private TCPConexao conectar(String host, int porta) throws IOException {
		StringBuilder info = new StringBuilder();
		info.append(host).append(":").append(porta);

		String server = info.toString();
		TCPConexao conexao = this.tcp.get(server);

		if ((conexao == null)) {
			conexao = new TCPConexao(host, porta);
			this.tcp.put(conexao.toString(), conexao);

			// Inicia Socket, Streams e Thread
			conexao.init();
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
