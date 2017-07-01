package distribuida.calculadora.servicos.ativos;

import java.util.Iterator;
import distribuida.calculadora.core.CalculadoraController;
import distribuida.calculadora.servicos.ServicoAbstrato;
import distribuidos.sistemas.core.InterfaceUsuario;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ServicoSomar extends ServicoAbstrato {

	@Override
	public void run(InterfaceUsuario usuario, JSONObject args) {
		CalculadoraController controller = CalculadoraController.instance();

		// Pega os operandos enviados pela rede
		JSONArray operandos = args.getJSONArray("operandos");
		Iterator valores = operandos.iterator();

		// Informações que vão voltar pro cliente
		StringBuilder expressao = new StringBuilder();
		double resultado = 0;

		// Faz a soma dos valores
		while (valores.hasNext()) {
			String entrada = valores.next().toString();

			// Cria a expressão, exemplo: (10-3-0-8)
			if ((expressao.length() > 0)) {
				expressao.append("+");
			}
			expressao.append(entrada);

			// Calcula o resultado
			if ((resultado == 0)) {
				resultado = Double.parseDouble(entrada);
			} else {
				resultado += Double.parseDouble(entrada);
			}
		}

		// Prepara a resposta e envia
		JSONObject adicao = new JSONObject();
		adicao.put("expressao", expressao.toString());
		adicao.put("resultado", resultado);
		controller.enviar(usuario, "adicao", adicao);
	}

}
