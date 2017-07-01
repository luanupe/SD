package distribuida.calculadora.cache;

public class ServidorConhecido {

	private String info, host;
	private int porta, carga;
	private long hora;

	public ServidorConhecido(String info, int carga, long hora) {
		this.info = info;
		this.carga = carga;
		this.hora = hora;
	}

	public void init() {
		String[] infos = this.info.split(":");
		this.host = infos[0];
		this.porta = Integer.parseInt(infos[1]);
	}

	public String getHost() {
		return this.host;
	}

	public int getPorta() {
		return this.porta;
	}

	// Carga do servidor no momento do cache
	public int getCarga() {
		return this.carga;
	}

	// Controle pra expirar a cache
	public boolean isExpirado() {
		long mili = (System.currentTimeMillis() - this.hora);
		return (mili > ServidorCache.EXPIRAR_MILISEGUNDOS);
	}

	public long getHora() {
		return this.hora;
	}

	@Override
	public String toString() {
		return this.info;
	}

}
