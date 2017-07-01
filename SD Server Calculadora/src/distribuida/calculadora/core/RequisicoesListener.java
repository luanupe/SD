package distribuida.calculadora.core;

import java.util.HashMap;
import java.util.Map;
import distribuida.calculadora.servicos.ServicoAbstrato;
import distribuida.calculadora.servicos.ativos.*;
import distribuidos.sistemas.core.InterfaceRequisicoes;
import distribuidos.sistemas.core.InterfaceUsuario;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RequisicoesListener implements InterfaceRequisicoes {

	private Map<String, ServicoAbstrato> requisicoes;

	public RequisicoesListener() {
		this.requisicoes = new HashMap<String, ServicoAbstrato>();
	}

	public boolean isDisponivel(String nome) {
		if ((nome == null)) {
			return true;
		}
		return this.requisicoes.containsKey(nome);
	}

	public JSONArray serialize() {
		JSONArray serialize = new JSONArray();
		for (String nome : this.requisicoes.keySet()) {
			serialize.add(nome);
		}
		return serialize;
	}

	@Override
	public void init() {
		this.requisicoes.put("online", new ServicoOnline());
		this.requisicoes.put("offline", new ServicoOffline());
		this.requisicoes.put("ping", new ServicoPing());
		this.requisicoes.put("somar", new ServicoSomar());
		this.requisicoes.put("subtrair", new ServicoSubtrair());
		this.requisicoes.put("fatorial", new ServicoFatorial());
	}

	@Override
	public void shutdown() {
		// TODO Como desligar corretamente?
	}

	@Override
	public void run(InterfaceUsuario usuario, String nome, JSONObject args) {
		ServicoAbstrato servico = this.requisicoes.get(nome);
		if ((servico != null)) {
			servico.run(usuario, args);
		}
	}

}
