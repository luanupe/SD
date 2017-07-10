package distribuida.calculadora.core;

import java.io.IOException;
import distribuidos.sistemas.core.ClientController;

public class Calculadora extends Thread {

	private static Calculadora INSTANCE;

	public static Calculadora instance() {
		return Calculadora.INSTANCE;
	}

	/*  */

	private ClientController controller;
	private Operador operador;

	/*
	 * Grupo: Endereço multicast
	 * Porta: Porta multicast e porta do servidor UDP local
	 * CARACTERÍSTICA: TRANSPARÊNCIA DE LOCALIZAÇÃO
	 */
	public Calculadora(String grupo, int porta) {
		this.operador = new Operador();
		this.controller = new ClientController(this.operador.getRequisitador(), grupo, porta);
	}

	public void init() throws IOException {
		Calculadora.INSTANCE = this;
		this.controller.init();
	}

	public Operador getOperador() {
		return this.operador;
	}

	public void shutdown(boolean force) {
		
	}

}
