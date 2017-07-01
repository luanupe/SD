package distribuidos.sistemas.eventos.nativos;

import java.io.IOException;
import distribuidos.sistemas.core.ServerController;
import distribuidos.sistemas.eventos.EventoInterface;
import net.sf.json.JSONObject;

public class EventoLigar implements EventoInterface {

	@Override
	public void run() {
		ServerController controller = ServerController.instance();

		try {
			controller.getUDP().init();
			controller.getTCP().init();
			controller.getUDP().start(); // Thread
			controller.getTCP().start(); // Thread

			// Avisa todo mundo
			JSONObject online = new JSONObject();
			online.put("cmd", "online");
			online.put("args", controller.serialize());
			controller.getUDP().enviar(online.toString());
		} catch (IOException e) {
			controller.getEventos().run("desligar");
		}
	}

}
