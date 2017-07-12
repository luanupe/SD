package distribuida.calculadora.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import distribuidos.sistemas.core.ClientController;

public class Terminal extends Thread {

	private Scanner scanner;
	private Operador operador;
	private Map<String, String> operacoes, controle;

	public Terminal(Operador operador) {
		this.operador = operador;
		this.scanner = new Scanner(System.in);
		this.operacoes = new HashMap<String, String>();
		this.controle = new HashMap<String, String>();
	}

	public void init() {
		// Inicia o controle
		this.controle.put("?", "Mostra a ajuda");
		this.controle.put("sair", "Fecha a calculadora.");
		this.controle.put("auto", "Exemplo: 'auto 5' ou 'auto 10'");
		this.controle.put("debug", "Exemplo: 'debug true' ou 'debug false'");

		// Inicia as operacoes
		this.operacoes.put("sum", "Exemplo: 'sum operandos' - 'sum 1 2 3 4 5'");
		this.operacoes.put("sub", "Exemplo: 'sub operandos' - 'sub 5 4 3 2 1'");
		this.operacoes.put("fat", "Exemplo: 'fat operandos' - 'fat 1 2 3 4 5'");
		this.operacoes.put("pow", "Exemplo: 'pow operandos' - 'pow 5 4 3 2 1'");
		
		
		this.start(); // Inicia Thread
	}

	@Override
	public void run() {
		String entrada = null;
		System.out.println(" > Digite '?' para ajuda.");

		try {
			while ((entrada = scanner.nextLine()) != null) {
				String[] entradas = entrada.toLowerCase().split(" ");
				if ((entradas.length == 0)) {
					System.out.println("Entradas insuficientes.");
				} else {
					if ((this.controle.keySet().contains(entradas[0]))) {
						this.controlar(entradas);
					} else if ((this.operacoes.keySet().contains(entradas[0]))) {
						this.interpretar(entradas);
					} else {
						System.err.println("Comando '" + entradas[0] + "' indisponível.");
					}
				}
			}
		} catch (Exception e) {
			System.out.println(" > Terminal finalizado.");
		}
	}

	private void controlar(String[] entradas) {
		if ((entradas[0].equals("auto"))) {
			if ((entradas.length <= 2)) {
				System.out.println("Defina a quantidade de testes e o delay para executar.");
			} else {
				this.auto(Integer.parseInt(entradas[1]), Integer.parseInt(entradas[2]));
			}
		} else if ((entradas[0].equals("sair"))) {
			Calculadora.instance().shutdown(true);
			this.scanner.close(); // Para o loop
		} else if ((entradas[0].equals("debug"))) {
			ClientController.DEBUG = Boolean.parseBoolean(entradas[1]);
		} else if ((entradas[0].equals("?"))) {
			this.ajuda(this.controle);
			this.ajuda(this.operacoes);
		}
	}

	private void ajuda(Map<String, String> entradas) {
		for (Map.Entry<String, String> entrada : entradas.entrySet()) {
			System.out.println(entrada.getKey() + " \t " + entrada.getValue());
		}
	}

	private void auto(int quantidade, int delay) {
		try {
			for (int i = 1; i <= quantidade; ++i) {
				ClientController.debug("TESTE AUTOMÁTICO: " + i + " / " + quantidade);
				
				// Somar
				this.operador.somar(i, i+1, 1+2);
				Thread.sleep(delay);
				
				// Subtrair
				this.operador.subtrair(i+2, i+1, i);
				Thread.sleep(delay);
				
				// Fatorial
				this.operador.fatorial(i);
				Thread.sleep(delay);

				// Quadrado
				this.operador.quadrado(i);
				Thread.sleep(delay);
			}
		} catch (InterruptedException e) {
			
		}
	}

	/* CALCULADORA */

	private void interpretar(String[] entradas) {
		try {
			double[] operadores = new double[entradas.length -1];
			for (int i = 1; i < entradas.length; i++) {
				operadores[i - 1] = Double.parseDouble(entradas[i]);
			}

			this.cacular(entradas[0], operadores); // Chamar os operadores
		} catch (NumberFormatException e) {
			System.err.println("Operadores inválidos.");
		}
	}

	private void cacular(String operacao, double[] operandos) {
		if ((operacao.equals("sum"))) {
			this.operador.somar(operandos);
		} else if ((operacao.equals("sub"))) {
			this.operador.subtrair(operandos);
		} else if ((operacao.equals("fat"))) {
			for (double operando : operandos) {
				this.operador.fatorial(operando); 
			}
		} else if ((operacao.equals("pow"))) {
			for (double operando : operandos) {
				this.operador.quadrado(operando); 
			}
		}
	}

}
