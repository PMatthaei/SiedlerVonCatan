package networkdiscovery.chat;

/**
 * Listener to chat messages.
 * 
 * @author Erich Schubert
 */
public interface ChatListener {
	/**
	 * Connection event.
	 * 
	 * @param text
	 *            Client information
	 * @param conn
	 *            Connection
	 */
	void connected(String text, TextSocketChannel conn);

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
	void received(String text, TextSocketChannel conn);
}