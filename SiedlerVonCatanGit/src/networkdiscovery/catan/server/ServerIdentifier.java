package networkdiscovery.catan.server;

public class ServerIdentifier {

	private String servername;
	private String version;

	public ServerIdentifier(String servername, String version){
		this.setServername(servername);
		this.setVersion(version);
	}

	/**
	 * @return the servername
	 */
	public String getServername() {
		return servername;
	}

	/**
	 * @param servername the servername to set
	 */
	public void setServername(String servername) {
		this.servername = servername;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
}
