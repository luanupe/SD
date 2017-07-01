package distribuidos.sistemas.eventos;

import java.util.HashMap;
import java.util.Map;
import distribuidos.sistemas.eventos.nativos.EventoDesligar;
import distribuidos.sistemas.eventos.nativos.EventoLigar;

public class EventoController {

	private Map<String, EventoInterface> eventos;

	public EventoController() {
		this.eventos = new HashMap<String, EventoInterface>();
	}

	public void init() {
		this.eventos.put("ligar", new EventoLigar());
		this.eventos.put("desligar", new EventoDesligar());
	}

	public void run(String nome) {
		EventoInterface evento = this.eventos.get(nome);
		if ((evento != null)) {
			evento.run();
		}
	}

}
