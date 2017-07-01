package distribuida.calculadora.core;

import java.io.IOException;
import distribuidos.sistemas.core.InterfaceUsuario;
import distribuidos.sistemas.core.ServerController;
import net.sf.json.JSONObject;

public class CalculadoraController {

	private static CalculadoraController INSTANCE;

	public static CalculadoraController instance() {
		return CalculadoraController.INSTANCE;
	}

	private RequisicoesListener listener;
	private ServerController controller;
	private String grupo;
	private int udp, tcp;

	public CalculadoraController(String grupoUDP, int portaUDP, int portaTCP) {
		this.listener = new RequisicoesListener();
		this.grupo = grupoUDP;
		this.udp = portaUDP;
		this.tcp = portaTCP;
	}

	public void init() throws IOException {
		CalculadoraController.INSTANCE = this;

		/*  */

		this.controller = new ServerController(this.listener, this.grupo, this.udp, this.tcp);
		this.controller.init();
	}

	public void enviar(InterfaceUsuario usuario, String cmd, JSONObject args) {
		JSONObject saida = new JSONObject();
		saida.put("cmd", cmd);
		saida.put("args", args);

		// TODO Algum callback?
		usuario.enviar(saida.toString());
	}

	public RequisicoesListener getListener() {
		return this.listener;
	}

}
