package distribuida.calculadora.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorCache {

	public static final int EXPIRAR_MILISEGUNDOS = 5000; // TTL

	/*  */

	private Map<String, Map<String, ServidorConhecido>> cache;

	public ServidorCache() {
		this.cache = new ConcurrentHashMap<String, Map<String, ServidorConhecido>>();
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

}
