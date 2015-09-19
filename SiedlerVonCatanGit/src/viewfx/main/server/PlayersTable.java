package viewfx.main.server;

import javafx.beans.property.SimpleStringProperty;

public class PlayersTable {
    private final SimpleStringProperty name;
    private final SimpleStringProperty color;
    private final SimpleStringProperty ready;

    public PlayersTable(String name, String ip, String ready) {
        this.name = new SimpleStringProperty(name);
        this.color = new SimpleStringProperty(ip);
        this.ready = new SimpleStringProperty(ready);
    }

    public String getName() {
        return name.get();
    }
    public void setName(String fName) {
    	name.set(fName);
    }
   
    public String getColor() {
        return color.get();
    }
    public void setColor(String fName) {
    	color.set(fName);
    }
   
    public String getReady() {
        return ready.get();
    }
    public void setReady(String fName) {
        ready.set(fName);
    }
  
}