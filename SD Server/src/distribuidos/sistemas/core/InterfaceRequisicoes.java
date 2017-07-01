package distribuidos.sistemas.core;

import net.sf.json.JSONObject;

public interface InterfaceRequisicoes {

	public void init();

	public void shutdown();

	public void run(InterfaceUsuario usuario, String nome, JSONObject args);

}
