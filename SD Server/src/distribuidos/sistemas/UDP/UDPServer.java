package distribuidos.sistemas.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import net.sf.json.JSONObject;

public class UDPServer extends Thread {

	private MulticastSocket server;
	private DatagramSocket client;
	private InetAddress grupo;

	/* */

	private boolean running;
	private String endereco;
	private int porta;

	public UDPServer(String grupo, int porta) {
		this.endereco = grupo;
		this.porta = porta;
	}

	public void init() throws IOException {
		System.out.println(" > UDP Server...");
		this.grupo = InetAddress.getByName(this.endereco);
		this.server = new MulticastSocket(this.porta);
		this.server.joinGroup(this.grupo);

		System.out.println(" > UDP Client...");
		this.client = new DatagramSocket();

		System.out.println("   Iniciado!");
		this.running = true;
	}

	@Override
	public void run() {
		System.out.println(" > Ouvindo UDP...");
		while (this.running) {
			try {
				byte[] input = new byte[4096];
				DatagramPacket inPacket = new DatagramPacket(input, input.length);
				this.server.receive(inPacket);

				// Processa em Thread
				UDPMessage packet = new UDPMessage(inPacket);
				packet.init();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void enviar(String mensagem) {
		this.enviar(mensagem, this.endereco, this.porta);
	}

	public void enviar(String mensagem, String address, int port) {
		try {
			InetAddress destino = InetAddress.getByName(address);
			byte[] output = mensagem.getBytes();

			DatagramPacket packet = new DatagramPacket(output, output.length, destino, port);
			this.client.send(packet);
		} catch (IOException e) {
			// TODO Falhou... e ai?
		}
	}

	public JSONObject serialize() {
		JSONObject serialize = new JSONObject();
		StringBuilder host = new StringBuilder();

		try {
			// Tenta pegar o IP do adaptador da máquina
			host.append(InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) {
			// Pega o IP do servidor TCP
			host.append(this.server.getInetAddress().getHostAddress());
		}

		host.append(":");
		host.append(this.porta);

		// Adiciona a informação da máquina
		serialize.put("host", host.toString());

		// Retorna a informação da máquina
		return serialize;
	}

	public void shutdown() throws IOException {
		// Para o servidor
		this.running = false;

		// Sai do grupo
		this.server.leaveGroup(this.grupo);

		// Fecha o socket
		this.server.close();

		// Fecha o client
		this.client.close();
	}

}
