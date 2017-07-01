package distribuida.calculadora.servicos;

import distribuidos.sistemas.core.InterfaceUsuario;
import net.sf.json.JSONObject;

public abstract class ServicoAbstrato {

	public abstract void run(InterfaceUsuario usuario, JSONObject args);

}
