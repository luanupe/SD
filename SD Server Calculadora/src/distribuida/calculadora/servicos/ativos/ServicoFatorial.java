package distribuida.calculadora.servicos.ativos;

import distribuida.calculadora.core.CalculadoraController;
import distribuida.calculadora.servicos.ServicoAbstrato;
import distribuidos.sistemas.core.InterfaceUsuario;
import net.sf.json.JSONObject;

public class ServicoFatorial extends ServicoAbstrato {

	@Override
	public void run(InterfaceUsuario usuario, JSONObject args) {
		CalculadoraController controller = CalculadoraController.instance();

		// Pega o operador da rede
		int operador = args.getInt("operador");

		// Informações que vão voltar pro cliente
		StringBuilder expressao = new StringBuilder();
		double resultado = 0;

		// Calcula o fatorial e escreve a expressão
		for (int i = operador; i > 0; --i) {
			// Cria a expressão, exemplo: (10*9*8)
			if ((expressao.length() > 0)) {
				expressao.append("*");
			}
			expressao.append(i);

			// Fatorial
			if ((resultado == 0)) {
				resultado = i;
			} else {
				resultado *= i;
			}
		}

		// Prepara a resposta e envia
		JSONObject somatorio = new JSONObject();
		somatorio.put("expressao", expressao.toString());
		somatorio.put("resultado", resultado);
		controller.enviar(usuario, "fatorial", somatorio);
	}

}
