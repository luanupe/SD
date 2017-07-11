package distribuida.calculadora.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import distribuida.calculadora.servicos.ServicoAbstrato;
import distribuida.calculadora.servicos.ativos.*;
import distribuidos.sistemas.core.InterfaceRequisicoes;
import distribuidos.sistemas.core.InterfaceUsuario;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RequisicoesListener implements InterfaceRequisicoes {

	private Map<String, ServicoAbstrato> requisicoes;
	private List<String> disponiveis;

	public RequisicoesListener(String[] disponiveis) {
		this.requisicoes = new HashMap<String, ServicoAbstrato>();
		this.disponiveis = Arrays.asList(disponiveis);
	}

	public boolean isDisponivel(String nome) {
		if ((nome == null)) {
			return true;
		}
		return this.requisicoes.containsKey(nome);
	}

	public JSONArray serialize() {
		JSONArray serialize = new JSONArray();
		for (String nome : this.disponiveis) {
			serialize.add(nome);
		}
		return serialize;
	}

	@Override
	public void init() {
		// Transparência de localização
		this.requisicoes.put("online", new ServicoOnline());
		this.requisicoes.put("offline", new ServicoOffline());
		this.requisicoes.put("ping", new ServicoPing());

		// Calculadora
		this.requisicoes.put("somar", new ServicoSomar());
		this.requisicoes.put("subtrair", new ServicoSubtrair());
		this.requisicoes.put("fatorial", new ServicoFatorial());
		this.requisicoes.put("quadrado", new ServicoQuadrado());
	}

	@Override
	public void shutdown() {
		// TODO Como desligar corretamente?
	}

	@Override
	public void run(InterfaceUsuario usuario, String nome, JSONObject args) {
		if ((this.disponiveis.contains(nome))) {
			ServicoAbstrato servico = this.requisicoes.get(nome);
			if ((servico != null)) {
				servico.run(usuario, args);
			}
		}
	}

}
