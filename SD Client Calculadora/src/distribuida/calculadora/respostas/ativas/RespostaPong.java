package distribuida.calculadora.respostas.ativas;

import java.util.Iterator;
import distribuida.calculadora.cache.ServidorCache;
import distribuida.calculadora.cache.ServidorConhecido;
import distribuida.calculadora.core.Calculadora;
import distribuida.calculadora.core.RequisitadorCallback;
import distribuida.calculadora.respostas.InterfaceResposta;
import net.sf.json.JSONObject;

/*
 * CARACTERISTICAS:
 *  - ESCALABILIDADE: MICROSERVI�O
 *  - TRANSPAR�NCIA DE LOCALIZA��O: MULTICAST GRUPO UDP
 */
public class RespostaPong implements InterfaceResposta {

	@Override
	public void run(JSONObject args) {
		RequisitadorCallback requisitador = Calculadora.instance().getOperador().getRequisitador();
		JSONObject status = args.getJSONObject("status");
		JSONObject tcp = status.getJSONObject("tcp");

		// Informa��es TCP pra cache
		String host = tcp.getString("host");
		int carga = tcp.getInt("carga");

		// Cria um registro de Cache
		ServidorConhecido servidor = new ServidorConhecido(host, carga, System.currentTimeMillis());
		servidor.init();

		// Pega o servidor cache
		ServidorCache cache = requisitador.getCache();

		// Adiciona servi�os a cache
		Iterator<String> servicos = args.getJSONArray("servicos").iterator();
		while (servicos.hasNext()) {
			cache.adicionar(servicos.next(), servidor);
		}

		// Continua a Thread do Requisitador
		requisitador.continuarThread();
	}

}
