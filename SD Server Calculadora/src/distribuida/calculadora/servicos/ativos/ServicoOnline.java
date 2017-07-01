package distribuida.calculadora.servicos.ativos;

import distribuida.calculadora.servicos.ServicoAbstrato;
import distribuidos.sistemas.core.InterfaceUsuario;
import net.sf.json.JSONObject;

public class ServicoOnline extends ServicoAbstrato {

	@Override
	public void run(InterfaceUsuario usuario, JSONObject args) {
		/*
		 * Adiciona um cache de servidores conhecidos pra poder migrar
		 * ou redirecionar conexões caso o servidor esteja muito com
		 * um nível de carga elevado.
		 */
	}

}
