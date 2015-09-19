package networkdiscovery.chat;

/**
 * API for attaching to a chat component.
 * 
 * @author Erich Schubert
 */
public interface ChatObservable {
	/**
	 * Add a chat listener.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	void addListener(ChatListener listener);

	/**
	 * Remove a chat listener.
	 * 
	 * @param listener
	 *            Listener to remove
	 */
	void removeListener(ChatListener listener);
}