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
	private InetSocketAddress destino;

	public TCPConexao(String host, int porta) {
		this.destino = new InetSocketAddress(host, porta);
	}

	@Override
	public void init() throws IOException {
		// Evita bloquear o Thread por muito tempo
		// 500 millisegundos para criar o Socket
		this.socket = new Socket();
		this.socket.connect(this.destino, 500);

		// Prepara o leitor de entradas
		this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.start(); // Inicia a Thread
	}

	@Override
	public void shutdown() {
		ClientController.debug(this.toString() + " > Fechando conexão.");
		ClientController.instance().desconectar(this); // Remove da lista

		try {
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
			ClientController.debugError(this.toString() + " > Falhou leitura: " + e.getMessage());
		} finally {
			this.shutdown(); // Pra que essa conexão não seja mais usada
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
		return this.destino.toString();
	}

}
