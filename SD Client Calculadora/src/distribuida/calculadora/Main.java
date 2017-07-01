package distribuida.calculadora;

import java.io.IOException;

import distribuida.calculadora.core.Calculadora;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		Calculadora calculadora = new Calculadora("224.1.1.3", 1234);
		calculadora.init();

		/*  */

		// Ping inicial só pra visualizar os servidores
		// calculadora.getOperador().ping();

		// Operações distribuídas
		calculadora.getOperador().somar(1, 2, 20, 13, 198);
		calculadora.getOperador().subtrair(40, 5, 0);
		calculadora.getOperador().somar(2, 3, -8, 0);
		calculadora.getOperador().fatorial(10);
		calculadora.getOperador().subtrair(5, 18);
		calculadora.getOperador().somar(3, 4, 0);
		calculadora.getOperador().subtrair(6, 7);

		for (int i = 0; i < 10; ++i) {
			calculadora.getOperador().somar(0, i);
		}
	}

}
