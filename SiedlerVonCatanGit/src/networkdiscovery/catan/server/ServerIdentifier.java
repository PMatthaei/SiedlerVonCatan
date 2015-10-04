package networkdiscovery.catan.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerIdentifier {

	private String sinfo;
	private String version;
	
	private Pattern p = Pattern.compile("##(.*)##(.*)##");

	public ServerIdentifier(String sinfo, String version){
		this.setServerInfo(sinfo);
		this.setVersion(version);
	}

	public String getServerName(){
		Matcher m = p.matcher(sinfo);
		String servername = "";
		if(m.find()){
			servername = m.group(1);
		}
		return servername;
	}
	
	public String getPlayercount(){
		Matcher m = p.matcher(sinfo);
		String playercount = "";
		if(m.find()){
			playercount = m.group(2);
		}
		return playercount;
	}
	/**
	 * @return the servername
	 */
	public String getServerInfo() {
		return sinfo;
	}

	/**
	 * @param servername the servername to set
	 */
	public void setServerInfo(String servername) {
		this.sinfo = servername;
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
