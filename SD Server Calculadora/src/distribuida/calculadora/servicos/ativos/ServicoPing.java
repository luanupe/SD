package distribuida.calculadora.servicos.ativos;

import distribuida.calculadora.core.CalculadoraController;
import distribuida.calculadora.servicos.ServicoAbstrato;
import distribuidos.sistemas.core.InterfaceUsuario;
import distribuidos.sistemas.core.ServerController;
import net.sf.json.JSONObject;

public class ServicoPing extends ServicoAbstrato {

	@Override
	public void run(InterfaceUsuario usuario, JSONObject args) {
		CalculadoraController controller = CalculadoraController.instance();

		if ((controller.getListener().isDisponivel(args.getString("servico")))) {
			JSONObject pong = new JSONObject();
			pong.put("servicos", controller.getListener().serialize());
			pong.put("status", ServerController.instance().serialize());
			controller.enviar(usuario, "pong", pong);
		}
	}

}
