package networkdiscovery.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerIdentifier {

	private String sinfo;
	private String version;
	
	/**
	 * This Pattern is searching for the corresponding part of the serverinformation
	 *	##<"servername">##<"connected/freeslots">## f.e. "##MyServer##2/4##
	 *	Group:	1	Servername
	 *			2	Currently connected Players of max slots
	 */
	//									.group():	1	  2
	private Pattern sinfo_pat = Pattern.compile("##(.*)##(.*)##");
	private Matcher m;
	
	public ServerIdentifier(String sinfo, String version){
		this.setServerInfo(sinfo);
		this.setVersion(version);
	}

	public String getServerName(){
		m = sinfo_pat.matcher(sinfo);
		String servername = "";
		if(m.find()){
			servername = m.group(1);
		}
		return servername;
	}
	
	public String getPlayercount(){
		m = sinfo_pat.matcher(sinfo);
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
