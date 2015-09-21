package networkdiscovery.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

/**
 * A primitive text-based chat UI.
 * 
 * Shutdown is not working nicely in this implementation, because
 * {@code reader.nextLine()} isn't easily interruptible. Reading from standard
 * input would need to be rewritten to use non-blocking NIO, if you wanted to
 * make this an ultimative text mode chat application.
 * 
 * @author Erich Schubert
 */
public abstract class TextUI implements JSONListener {
	/**
	 * Constructor.
	 */
	public TextUI() {
		super();
	}

	@Override
	public void connected(String text, JSONSocketChannel conn) {
		System.out.println("Connected: " + text);
	}

	@Override
	public void disconnected(String text) {
		System.out.println("Disconnected: " + text);
	}

	@Override
	public void received(JSONObject text, JSONSocketChannel conn) {
		System.out.print(text);
	}

	/**
	 * Run the text UI until end of input.
	 */
	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				// Blocking read!
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				onUserInput(line);
			}
		} catch (IOException e) {
			System.out.println("Disconnected, or end of user input: " + e.getMessage());
		}
	}

	/**
	 * This method is called whenever the user inputs some data.
	 * 
	 * Implement this to handle user input!
	 * 
	 * @param line
	 *            Line
	 */
	abstract protected void onUserInput(String line);
}