package distribuida.calculadora.servicos.ativos;

import distribuida.calculadora.core.CalculadoraController;
import distribuida.calculadora.servicos.ServicoAbstrato;
import distribuidos.sistemas.core.InterfaceUsuario;
import net.sf.json.JSONObject;

public class ServicoQuadrado extends ServicoAbstrato {

	@Override
	public void run(InterfaceUsuario usuario, JSONObject args) {
		CalculadoraController controller = CalculadoraController.instance();

		// Pega o operador da rede
		int operador = args.getInt("operador");

		// Informações que vão voltar pro cliente
		StringBuilder expressao = new StringBuilder();
		expressao.append(operador).append("^").append(operador);

		// Calcula o resultado
		double resultado = (operador * operador);

		// Prepara a resposta e envia
		JSONObject quadrado = new JSONObject();
		quadrado.put("expressao", expressao.toString());
		quadrado.put("resultado", resultado);
		controller.enviar(usuario, "quadrado", quadrado);
	}

}
