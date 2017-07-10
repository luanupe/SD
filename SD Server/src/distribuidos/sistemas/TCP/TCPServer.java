package distribuidos.sistemas.TCP;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.sf.json.JSONObject;

public class TCPServer extends Thread {

	private int porta;
	private boolean running;
	private ServerSocket server;
	private List<TCPClient> clients;

	public TCPServer(int porta) {
		this.clients = new CopyOnWriteArrayList<TCPClient>();
		this.porta = porta;
	}

	public void init() throws IOException {
		System.out.println(" > TCP Server...");
		this.server = new ServerSocket(this.porta);

		System.out.println("   Iniciado!");
		this.running = true;
	}

	@Override
	public void run() {
		System.out.println(" > Ouvindo TCP...");
		while (this.running) {
			try {
				Socket socket = this.server.accept();
				if ((socket != null)) {
					TCPClient client = new TCPClient(socket);
					this.clients.add(client);
					client.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void remover(TCPClient cliente) {
		this.clients.remove(cliente);
	}

	public void shutdown() throws IOException {
		// Para o servidor
		this.running = false;

		// Fecha as conexões abertas
		for (TCPClient client : this.clients) {
			client.shutdown();
		}

		// Para de ouvir a porta
		this.server.close();
	}

	public JSONObject serialize() {
		// Informações da máquina
		JSONObject serialize = new JSONObject();
		serialize.put("host", this.toString()); // IP:Porta
		serialize.put("carga", this.clients.size()); // Quantidade de clientes
		return serialize;// Retorna a informação pra enviar na rede
	}

	@Override
	public String toString() {
		StringBuilder host = new StringBuilder();

		try { // Tenta pegar o IP do adaptador da máquina
			host.append(InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) { // Pega o IP do servidor TCP
			host.append(this.server.getInetAddress().getHostAddress());
		}

		host.append(":").append(this.porta);
		return host.toString();
	}

}
