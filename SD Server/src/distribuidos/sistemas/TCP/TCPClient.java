package distribuidos.sistemas.TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import distribuidos.sistemas.core.ServerController;
import distribuidos.sistemas.core.InterfaceUsuario;
import net.sf.json.JSONObject;

public class TCPClient extends Thread implements InterfaceUsuario {

	private Socket socket;
	private BufferedReader br;

	protected TCPClient(Socket socket) throws IOException {
		this.socket = socket;
		this.br = new BufferedReader(new InputStreamReader(this.getSocket().getInputStream()));
	}

	@Override
	public void run() {
		try {
			for (String entrada; ((entrada = this.br.readLine()) != null); ) {
				JSONObject request = JSONObject.fromObject(entrada);
				String command = request.getString("cmd");
				JSONObject args = request.getJSONObject("args");

				// Executa a requisição
				ServerController.instance().run(this, command, args);
			}
		} catch (Exception e) {
			
		} finally {
			this.shutdown();
		}
	}

	@Override
	public void shutdown() {
		// Diminui a carga do servidor
		ServerController.instance().getTCP().remover(this);

		// Libera corretamente os recursos
		try {
			this.getSocket().shutdownInput();
			this.getSocket().shutdownOutput();
			this.getSocket().close();
		} catch (IOException e) {
			// TODO O que fazer? Tentar shutdown de novo?
		}
	}

	@Override
	public void enviar(String mensagem) {
		mensagem = mensagem + '\n';
		byte[] output = mensagem.getBytes();

		try {
			this.getSocket().getOutputStream().write(output);
		} catch (IOException e) {
			this.shutdown();
		}
	}

	protected Socket getSocket() {
		return this.socket;
	}

}
