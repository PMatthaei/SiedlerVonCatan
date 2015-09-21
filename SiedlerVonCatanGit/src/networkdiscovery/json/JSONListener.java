package networkdiscovery.json;

import org.json.JSONObject;

/**
 * Listener to chat messages.
 * 
 * @author Erich Schubert
 */
public interface JSONListener {
	/**
	 * Connection event.
	 * 
	 * @param text
	 *            Client information
	 * @param conn
	 *            Connection
	 */
	void connected(String text, JSONSocketChannel conn);

	/**
	 * Disconnection event.
	 * 
	 * @param text
	 *            Client information
	 */
	void disconnected(String text);

	/**
	 * Received a message.
	 * 
	 * @param text
	 *            Decoded message
	 * @param conn
	 *            Connection
	 */
	void received(JSONObject text, JSONSocketChannel conn);
}