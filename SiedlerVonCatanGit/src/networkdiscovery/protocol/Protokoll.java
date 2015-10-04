package networkdiscovery.protocol;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author Michi
 *
 */
public interface Protokoll {

	
	/**
	 * Ist zuständig für das verwalten von Daten bzw. verwalten von ankommenden
	 * JSONObjekten
	 * 
	 * @param json
	 * @param id - id der PlayerConnection
	 * @throws JSONException
	 */
	public void handleReceivedData(JSONObject json, int id) throws JSONException;
}
