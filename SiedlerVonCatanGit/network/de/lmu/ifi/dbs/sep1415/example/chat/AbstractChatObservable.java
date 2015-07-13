package de.lmu.ifi.dbs.sep1415.example.chat;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Manage listeners for chat events.
 * 
 * @author Erich Schubert
 */
public class AbstractChatObservable implements ChatObservable {
  /** Chat listeners. */
  protected Collection<ChatListener> listeners = new ArrayList<>();

  /** Constructor. */
  public AbstractChatObservable() {
    super();
  }

  @Override
  public void addListener(ChatListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(ChatListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire the event of being connected.
   * 
   * @param remotename Remote name
   * @param conn Connection
   */
  protected void fireConnected(String remotename, TextSocketChannel conn) {
    for(ChatListener listener : listeners) {
      listener.connected(remotename, conn);
    }
  }

  /**
   * Fire the event of having received a message.
   * 
   * @param conn Connection
   * @param message Message
   */
  protected void fireReceived(TextSocketChannel conn, String message) {
    for(ChatListener listener : listeners) {
      listener.received(message, conn);
    }
  }

  /**
   * Fire the event of disconnection.
   * 
   * @param remotename Remote name
   */
  protected void fireDisconnected(String remotename) {
    for(ChatListener listener : listeners) {
      listener.disconnected(remotename);
    }
  }
}