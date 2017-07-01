package distribuida.calculadora.servicos.ativos;

import distribuida.calculadora.servicos.ServicoAbstrato;
import distribuidos.sistemas.core.InterfaceUsuario;
import net.sf.json.JSONObject;

public class ServicoOffline extends ServicoAbstrato {

	@Override
	public void run(InterfaceUsuario usuario, JSONObject args) {
		/*
		 * Checar TODOS os serviços e migrar se necessário antes
		 * de desligar pra que todos os serviços continuem
		 */
	}

}
