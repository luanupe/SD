package distribuidos.sistemas.UDP;

import java.net.DatagramPacket;
import distribuidos.sistemas.core.ServerController;
import distribuidos.sistemas.core.InterfaceUsuario;
import net.sf.json.JSONObject;

public class UDPMessage extends Thread implements InterfaceUsuario {

	private DatagramPacket packet;
	private JSONObject request;

	public UDPMessage(DatagramPacket packet) {
		this.packet = packet;
	}

	public void init() {
		String mensagem = new String(this.packet.getData(), 0, this.packet.getLength());
		this.request = JSONObject.fromObject(mensagem);
		this.start();
	}

	@Override
	public void shutdown() {
		// TODO Fazer depois
	}

	@Override
	public void run() {
		// Processa a requisição
		String command = this.request.getString("cmd");
		JSONObject args = this.request.getJSONObject("args");

		// Executa a requisição
		ServerController.instance().run(this, command, args);
	}

	@Override
	public void enviar(String mensagem) {
		JSONObject sender = this.request.getJSONObject("sender");
		if ((sender != null) && (sender.isNullObject() == false)) {
			String[] infos = sender.getString("host").split(":");
			ServerController.instance().getUDP().enviar(mensagem, infos[0], Integer.parseInt(infos[1]));
		}
	}

}
