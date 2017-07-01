package distribuidos.sistemas.TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import distribuidos.sistemas.core.ClientController;
import distribuidos.sistemas.core.ConexaoAbstract;
import distribuidos.sistemas.requisicoes.Requisicao;

// extends pra garantir a indiferença de UDP e TCP
public class TCPConexao extends ConexaoAbstract {

	private Socket socket;
	private BufferedReader br;

	/*  */

	private String host;
	private int porta;

	public TCPConexao(String host, int porta) {
		this.host = host;
		this.porta = porta;
	}

	@Override
	public void init() throws IOException {
		// Tenta se conectar ao servidor
		if ((this.socket == null) || (this.socket.isConnected() == false)) {
			// this.socket = new Socket(this.host, this.porta);
			this.socket = new Socket();
			this.socket.connect(new InetSocketAddress(this.host, this.porta), 500);
		}

		// Prepara o leitor de entradas
		this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

		// Inicia a Thread
		this.start();
	}

	@Override
	public void shutdown() {
		// Remove da lista
		ClientController.instance().desconectar(this);

		// Para tudo
		try {
			this.socket.shutdownInput();
			this.socket.shutdownOutput();
			this.socket.close();
		} catch (IOException e) {
			// TODO O que fazer aqui?
		}
	}

	@Override
	public void run() {
		try {
			// Lê uma String (entrada) e processa
			for (String mensagem; ((mensagem = this.br.readLine()) != null); ) {
				super.processar(mensagem);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			this.shutdown();
		}
	}

	@Override
	public void enviar(Requisicao requisicao) throws IOException {
		StringBuilder output = new StringBuilder();
		output.append(requisicao.getMensagem().toString());
		output.append('\n'); // Divisor de Strings

		// Envia o pedido pro servidor
		String mensagem = output.toString();
		this.socket.getOutputStream().write(mensagem.getBytes());
	}

	@Override
	public String toString() {
		return this.host + ":" + this.porta;
	}

}
