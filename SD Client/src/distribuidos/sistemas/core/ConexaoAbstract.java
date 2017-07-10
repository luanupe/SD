package distribuidos.sistemas.core;

import java.io.IOException;
import distribuidos.sistemas.requisicoes.Requisicao;
import net.sf.json.JSONObject;

/*
 * Essa class abstrata � pra poder tratar qualquer pedido pelo
 * mesmo Request, pra n�o ter "indiferen�a" entre TCP e UDP.
 */
public abstract class ConexaoAbstract extends Thread {

	protected final void processar(String mensagem) {
		// Pega a entrada e converte em JSON
		JSONObject input = JSONObject.fromObject(mensagem);

		// Separa as informa��es importantes
		String cmd = input.getString("cmd");
		JSONObject args = input.getJSONObject("args");

		// Faz o processamento
		ClientController.instance().getCallback().recebido(cmd, args);
	}

	public abstract void init() throws IOException;

	public abstract void shutdown();

	public abstract void enviar(Requisicao mensagem) throws IOException;

}
