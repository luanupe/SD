package distribuidos.sistemas.requisicoes;

import net.sf.json.JSONObject;

public interface RequisicaoCBInterface {

	public void init();

	public void preparado(Requisicao requisicao);

	public void enviado(Requisicao requisicao);

	public void falha(Requisicao requisicao);

	public void recebido(String cmd, JSONObject args);

}
