package distribuida.calculadora.core;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Operador {

	private RequisitadorCallback requisitador;

	public Operador() {
		this.requisitador = new RequisitadorCallback();
	}

	public void ping(String... cmds) {
		JSONObject servico = new JSONObject();
		if ((cmds != null) && (cmds.length > 0)) {
			servico.put("servico", cmds[0]);
		}
		this.requisitador.run("ping", servico, true);
	}

	/* CALCULADORA */

	private JSONArray getOperandos(double...operandos) {
		JSONArray args = new JSONArray();
		for (double operando : operandos) {
			args.add(operando);
		}
		return args;
	}

	// Operações

	public void somar(double... operandos) {
		JSONObject args = new JSONObject();
		args.put("operandos", this.getOperandos(operandos));
		this.getRequisitador().run("somar", args);
	}

	public void subtrair(double... operandos) {
		JSONObject args = new JSONObject();
		args.put("operandos", this.getOperandos(operandos));
		this.getRequisitador().run("subtrair", args);
	}

	public void multiplicar(double... operandos) {
		// TODO
	}

	public void dividir(double a, double b) {
		// TODO
	}

	public void fatorial(int a) {
		JSONObject operador = new JSONObject();
		operador.put("operador", a);
		this.getRequisitador().run("fatorial", operador);
	}

	/* FIM CALCULADORA */

	public RequisitadorCallback getRequisitador() {
		return this.requisitador;
	}

}
