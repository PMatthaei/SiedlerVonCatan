package networkdiscovery.json;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONObject;

/**
 * Manage listeners for chat events.
 * 
 * @author Erich Schubert
 */
public class AbstractJSONObservable implements JSONObservable {
	/** Chat listeners. */
	protected Collection<JSONListener> listeners = new ArrayList<>();

	/** Constructor. */
	public AbstractJSONObservable() {
		super();
	}

	@Override
	public void addListener(JSONListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(JSONListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Fire the event of being connected.
	 * 
	 * @param remotename
	 *            Remote name
	 * @param conn
	 *            Connection
	 */
	protected void fireConnected(String remotename, JSONSocketChannel conn) {
		for (JSONListener listener : listeners) {
			listener.connected(remotename, conn);
		}
	}

	/**
	 * Fire the event of having received a message.
	 * 
	 * @param conn
	 *            Connection
	 * @param message
	 *            Message
	 */
	protected void fireReceived(JSONSocketChannel conn, JSONObject message) {
		for (JSONListener listener : listeners) {
			listener.received(message, conn);
		}
	}

	/**
	 * Fire the event of disconnection.
	 * 
	 * @param remotename
	 *            Remote name
	 */
	protected void fireDisconnected(String remotename) {
		for (JSONListener listener : listeners) {
			listener.disconnected(remotename);
		}
	}
}