package distribuidos.sistemas.requisicoes;

import net.sf.json.JSONObject;

public interface RequisicaoCBInterface {

	public void init();

	public void preparado(Requisicao requisicao);

	public void sucesso(Requisicao requisicao);

	public void falha(Requisicao requisicao, String motivo);

	public void recebido(String cmd, JSONObject args);

}
