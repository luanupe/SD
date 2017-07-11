package distribuida.calculadora;

import java.io.IOException;
import distribuida.calculadora.core.Calculadora;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		Calculadora calculadora = new Calculadora("224.1.1.3", 1234);
		calculadora.init();
	}

}
