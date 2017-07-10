package distribuida.calculadora;

import java.io.IOException;

import distribuida.calculadora.core.Calculadora;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		Calculadora calculadora = new Calculadora("224.1.1.3", 1234);
		calculadora.init();

		/*  */

		for (int i = 1; i <= 5; ++i) {
			// Somar
			calculadora.getOperador().somar(i, i+1, 1+2);
			Thread.sleep(2000);
			
			// Subtrair
			calculadora.getOperador().subtrair(i+2, i+1, i);
			Thread.sleep(2000);
			
			// Fatorial
			calculadora.getOperador().fatorial(i);
			Thread.sleep(2000);
		}
	}

}
