package viewfx.utilities;

import javafx.beans.property.SimpleStringProperty;

public class ServersTable {
	
    private final SimpleStringProperty servername;
    private final SimpleStringProperty ip;
    private final SimpleStringProperty port;
    private final SimpleStringProperty playercount;

    public ServersTable(String servername, String ip, String port, String playercount) {
        this.servername = new SimpleStringProperty(servername);
        this.ip = new SimpleStringProperty(ip);
        this.port = new SimpleStringProperty(port);
        this.playercount = new SimpleStringProperty(playercount);

    }

    public String getPlayercount() {
        return playercount.get();
    }
    public void setPlayercount(String fname) {
    	playercount.set(fname);
    }
   
    public String getIp() {
        return ip.get();
    }
    public void setIp(String fname) {
    	ip.set(fname);
    }
   
    public String getPort() {
        return port.get();
    }
    public void setPort(String fname) {
        port.set(fname);
    }

	public String getServername() {
		return servername.get();
	}
	
	public void setServername(String fname) {
		servername.set(fname);
	}
}