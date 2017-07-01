package distribuidos.sistemas.eventos.nativos;

import distribuidos.sistemas.eventos.EventoInterface;

public class EventoDesligar implements EventoInterface {

	@Override
	public void run() {
		// TODO Deligar sistema

		/*
		 * Verificar serviços de todos os usuários Verifica quem tem os serviços
		 * dos usuários Redirecionar usuários para o novo servidor Se nenhum
		 * outro nó tiver o serviço ele vai ser migrado antes de desligar.
		 */
	}

}
