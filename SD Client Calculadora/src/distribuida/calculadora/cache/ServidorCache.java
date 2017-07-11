package distribuida.calculadora.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import distribuida.calculadora.core.Calculadora;

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
		Map<String, ServidorConhecido> servers = this.cache.get(servico);
		ServidorConhecido servidor = null;

		if ((servers != null)) {
			for (ServidorConhecido server : servers.values()) {
				if ((server.isExpirado() == false)) {
					if ((servidor == null) || (server.getCarga() < servidor.getCarga())) {
						servidor = server;
					}
				} else {
					servers.remove(server.toString());
				}
			}
		}

		return servidor;
	}

	public void adicionar(String servico, ServidorConhecido servidor) {
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
