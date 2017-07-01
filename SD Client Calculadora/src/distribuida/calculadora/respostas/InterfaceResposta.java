package distribuida.calculadora.respostas;

import net.sf.json.JSONObject;

/*
 * Cada microserviço tem sua "microresposta" que tem de
 * atender essa interface (CARACTERÍSTICA: ABERTURA)
 */
public interface InterfaceResposta {

	public void run(JSONObject args);

}
