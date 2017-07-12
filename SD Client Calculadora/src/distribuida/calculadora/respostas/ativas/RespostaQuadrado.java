package distribuida.calculadora.respostas.ativas;

import distribuida.calculadora.respostas.InterfaceResposta;
import net.sf.json.JSONObject;

public class RespostaQuadrado implements InterfaceResposta {

	@Override
	public void run(JSONObject args) {
		String expressao = args.getString("expressao");
		double resultado = args.getDouble("resultado");
		System.out.println(expressao + " = " + resultado);
	}

}
