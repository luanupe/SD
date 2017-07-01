package distribuidos.sistemas.requisicoes;

import net.sf.json.JSONObject;

public class Requisicao {

	private int id;
	private boolean tcp;
	private JSONObject mensagem;

	public Requisicao(int id, JSONObject mensagem, boolean tcp) {
		this.id = id;
		this.mensagem = mensagem;
		this.tcp = tcp;
	}

	// ID único que foi gerado
	public int getId() {
		return this.id;
	}

	// Mensagem original
	public JSONObject getMensagem() {
		return this.mensagem;
	}

	public String getServico() {
		return this.getMensagem().getString("cmd");
	}

	public boolean isTCP() {
		return this.tcp;
	}

	// Mensagem em bytes pra ser enviada pela rede
	public byte[] getBytes() {
		return this.getMensagem().toString().getBytes();
	}

}
