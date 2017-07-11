package distribuida.calculadora;

import java.io.IOException;
import distribuida.calculadora.core.CalculadoraController;

public class Main {

	public static void main(String[] args) throws IOException {
		CalculadoraController controller = new CalculadoraController(args, "224.1.1.3", 1234, 4321);
		controller.init();
	}

}
