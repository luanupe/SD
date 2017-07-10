package distribuidos.sistemas.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import distribuidos.sistemas.core.ClientController;
import distribuidos.sistemas.core.ConexaoAbstract;
import distribuidos.sistemas.requisicoes.Requisicao;

public class UDPServer extends ConexaoAbstract {

	private InetAddress destino;
	private DatagramSocket server;

	/*  */

	private boolean running;
	private String grupoMulticast;
	private int portaMulticast, portaLocal;

	public UDPServer(String grupo, int porta) {
		this.grupoMulticast = grupo;
		this.portaMulticast = porta;
		this.portaLocal = (porta + (1 + ClientController.SEED.nextInt(999)));
	}

	@Override
	public void init() throws IOException {
		ClientController.debug("Escutando UDP na porta " + this.portaLocal);
		this.destino = InetAddress.getByName(this.grupoMulticast);
		this.server = new DatagramSocket(this.portaLocal);
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
		byte[] output = mensagem.getBytes();
		DatagramPacket packet = new DatagramPacket(output, output.length, this.destino, this.portaMulticast);
		this.server.send(packet);
	}

}
