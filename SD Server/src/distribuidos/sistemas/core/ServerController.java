package distribuidos.sistemas.core;

import java.io.IOException;
import distribuidos.sistemas.TCP.TCPServer;
import distribuidos.sistemas.UDP.UDPServer;
import distribuidos.sistemas.eventos.EventoController;
import net.sf.json.JSONObject;

public class ServerController {

	public static final boolean DEBUG = true;
	private static ServerController INSTANCE;

	public static ServerController instance() {
		return ServerController.INSTANCE;
	}

	/*  */

	private InterfaceRequisicoes requisicoes;
	private EventoController eventos;
	private UDPServer udp;
	private TCPServer tcp;

	public ServerController(InterfaceRequisicoes requisicoes, String grupoUDP, int portaUDP, int portaTCP) {
		this.eventos = new EventoController();
		this.requisicoes = requisicoes;

		/*  */

		this.udp = new UDPServer(grupoUDP, portaUDP);
		this.tcp = new TCPServer(portaTCP);
	}

	public void init() throws IOException {
		System.out.println("Iniciando:");
		ServerController.INSTANCE = this;

		// Serviços

		this.getRequisicoes().init();
		this.getEventos().init();
		this.getEventos().run("ligar");
	}

	public void run(InterfaceUsuario usuario, String nome, JSONObject args) {
		if ((ServerController.DEBUG)) {
			System.out.println("RECEBENDO: " + nome + ": " + args);
		}

		this.getRequisicoes().run(usuario, nome, args);
	}

	public void shutdown() {
		try {
			this.getRequisicoes().shutdown();
			this.getTCP().shutdown();
			this.getUDP().shutdown();
		} catch (IOException e) {
			System.out.println("Server não foi desligado corretamente: " + e.getMessage());
		}
	}

	public InterfaceRequisicoes getRequisicoes() {
		return this.requisicoes;
	}

	public EventoController getEventos() {
		return this.eventos;
	}

	public TCPServer getTCP() {
		return this.tcp;
	}

	public UDPServer getUDP() {
		return this.udp;
	}

	public JSONObject serialize() {
		JSONObject serialize = new JSONObject();
		serialize.put("tcp", this.getTCP().serialize());
		serialize.put("udp", this.getUDP().serialize());
		serialize.put("hora", System.currentTimeMillis());

		// TODO % de memória e de CPU que está em uso

		return serialize;
	}

}
