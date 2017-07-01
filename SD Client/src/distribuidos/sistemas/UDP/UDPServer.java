package distribuidos.sistemas.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import distribuidos.sistemas.core.ConexaoAbstract;
import distribuidos.sistemas.requisicoes.Requisicao;
import net.sf.json.JSONObject;

public class UDPServer extends ConexaoAbstract {

	private InetAddress destino;
	private DatagramSocket server;

	/*  */

	private boolean running;
	private String grupo;
	private int porta;

	public UDPServer(String grupo, int porta) {
		this.grupo = grupo;
		this.porta = porta;
	}

	@Override
	public void init() throws IOException {
		this.destino = InetAddress.getByName(this.grupo);
		this.server = new DatagramSocket(this.porta);
		this.running = true;
		this.start();
	}

	@Override
	public void shutdown() {
		this.server.close();
	}

	@Override
	public void run() {
		while (this.running) {
			try {
				/*
				 * Não precisa ser multithread já que é apenas 1 cliente por
				 * vez, mesmo podendo receber resposta de vários servers
				 */

				// Espera por alguma mensagem via UDP
				byte[] dados = new byte[4096];	
				DatagramPacket packet = new DatagramPacket(dados, dados.length);
				this.server.receive(packet);

				// Lê a mensagem e processa (Igual ao TCP)
				String mensagem = new String(packet.getData());
				super.processar(mensagem);
			} catch (IOException e) {
				
			}
		}
	}

	@Override
	public void enviar(Requisicao mensagem) throws IOException {
		// O server precisa saber como a resposta vai voltar...
		mensagem.getMensagem().put("sender", this.serialize());

		// Client só envia UDP pra todo grupo multicast...
		byte[] output = mensagem.getBytes();
		DatagramPacket packet = new DatagramPacket(output, output.length, this.destino, this.porta);
		this.server.send(packet);
	}

	public JSONObject serialize() {
		JSONObject serialize = new JSONObject();
		StringBuilder host = new StringBuilder();

		try {
			// Tenta pegar o IP do adaptador da máquina
			host.append(InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) {
			// Pega o IP do servidor UDP
			host.append(this.server.getInetAddress().getHostAddress());
		}

		host.append(":");
		host.append(this.porta);

		// Adiciona a informação da máquina
		serialize.put("host", host.toString());

		// Retorna a informação da máquina
		return serialize;
	}

	/* GETTERS */

	public String getGrupo() {
		return this.grupo;
	}

}
