package distribuida.calculadora.respostas;

import net.sf.json.JSONObject;

/*
 * Cada microservi�o tem sua "microresposta" que tem de
 * atender essa interface (CARACTER�STICA: ABERTURA)
 */
public interface InterfaceResposta {

	public void run(JSONObject args);

}
