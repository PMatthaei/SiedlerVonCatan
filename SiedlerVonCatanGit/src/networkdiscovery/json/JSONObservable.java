package networkdiscovery.json;

/**
 * API for attaching to a chat component.
 * 
 * @author Erich Schubert
 */
public interface JSONObservable {
	/**
	 * Add a chat listener.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	void addListener(JSONListener listener);

	/**
	 * Remove a chat listener.
	 * 
	 * @param listener
	 *            Listener to remove
	 */
	void removeListener(JSONListener listener);
}