package distribuida.calculadora.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import distribuida.calculadora.core.Calculadora;
import distribuidos.sistemas.core.ClientController;

public class ServidorCache {

	public static final int EXPIRAR_MILISEGUNDOS = 60000; // TTL

	/*  */

	private Map<String, Map<String, ServidorConhecido>> cache;
	private Map<String, Long> pings; // Evitar inundar o multicast

	public ServidorCache() {
		this.cache = new ConcurrentHashMap<String, Map<String, ServidorConhecido>>();
		this.pings = new ConcurrentHashMap<String, Long>();
	}

	public ServidorConhecido getServidor(String servico) {
		if ((this.cache.containsKey(servico) == false)) {
			return null; // Nenhum servidor com esse microserviço
		}

		// Pega na cache
		Collection<ServidorConhecido> servers = this.cache.get(servico).values();

		// Sorteadores
		List<ServidorConhecido> candidatos = new ArrayList<ServidorConhecido>();
		ServidorConhecido candidato = null;

		// Procura os servidores
		for (ServidorConhecido servidor : servers) {
			if ((servidor.isExpirado())) { // Expirou, remove da cache
				servers.remove(servidor.toString());
			} else {
				if ((candidato == null) || (servidor.getCarga() <= candidato.getCarga())) {
					candidato = servidor;
					candidatos.add(candidato);
				}
			}
		}

		ClientController.debug("Cache do serviço '" + servico + "': " + candidatos.size());

		// Sorteia algum da lista
		if ((candidatos.isEmpty() == false)) {
			return candidatos.get(ClientController.SEED.nextInt(candidatos.size()));
		}

		// Retorna o candidado padrão
		return candidato;
	}

	public synchronized void adicionar(String servico, ServidorConhecido servidor) {
		Map<String, ServidorConhecido> servers = this.cache.get(servico);

		// Lista de cache não existe
		if ((servers == null)) {
			servers = new ConcurrentHashMap<String, ServidorConhecido>();
			this.cache.put(servico, servers);
		}

		// Adiciona ou sobreescreve a cache
		servers.put(servidor.toString(), servidor);
	}

	public void remover(String servico, ServidorConhecido servidor) {
		Map<String, ServidorConhecido> servers = this.cache.get(servico);
		if ((servers != null)) {
			servers.remove(servidor.toString());
		}
	}

	/* Evitar inundar a rede com solicitações repetidas de
	 * ping, evitar Denial of Service nos servidores */
	public synchronized void controlePing(String servico) {
		Long cache = this.pings.get(servico);
		long agora = System.currentTimeMillis();

		if ((cache == null) || ((agora - cache) > ServidorCache.EXPIRAR_MILISEGUNDOS)) {
			this.pings.put(servico, agora);
			Calculadora.instance().getOperador().ping(servico);
		}
	}

}
