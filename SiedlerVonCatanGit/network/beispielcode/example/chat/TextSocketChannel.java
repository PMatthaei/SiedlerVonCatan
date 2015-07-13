package beispielcode.example.chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * Class wrapping everything needed for a text-based connection.
 * 
 * In particular, this class explicitly manages text encoding of the data.
 * 
 * @author Erich Schubert
 */
public class TextSocketChannel {
	/** Buffer size */
	private static final int BUFFER_SIZE = 8192;

	/** Info (name) of the channel */
	private final String info;

	/** Current channel. */
	private final ByteChannel chan;

	/** Charset decoder for protocol */
	private CharsetDecoder decoder;

	/** Charset encoder for protocol */
	private CharsetEncoder encoder;

	/** Buffers for IO */
	private ByteBuffer inbuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

	/** Buffers for IO */
	private ByteBuffer outbuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

	/** Encoded newline character */
	private ByteBuffer newline;

	/**
	 * Constructor.
	 * 
	 * @param chan
	 *            Java NIO channel.
	 * @param charset
	 *            Character set.
	 * @param info
	 *            Channel info.
	 */
	public TextSocketChannel(ByteChannel chan, Charset charset, String info) {
		this.chan = chan;
		this.info = info;

		// Setup text encoding and decoding
		decoder = charset.newDecoder();
		encoder = charset.newEncoder();
		newline = charset.encode("\n");
	}

	/**
	 * Get the channel info (name).
	 * 
	 * @return
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * (Blocking) read from the connection.
	 * 
	 * @return Line read, or {@code null} on disconnection.
	 * @throws IOException
	 *             On IO errors
	 */
	public String read() throws IOException {
		inbuffer.clear();
		int bytes = chan.read(inbuffer);
		if (bytes < 0) {
			return null; // Disconnected
		}
		inbuffer.flip();
		return decoder.decode(inbuffer).toString();
	}

	/**
	 * Sending a message over the channel.
	 * 
	 * @param message
	 *            Message
	 * @throws IOException
	 *             when sending failed
	 */
	public synchronized void send(String message) throws IOException {
		outbuffer.clear();
		outbuffer.put(encoder.encode(CharBuffer.wrap(message)));
		if (!message.endsWith("\n")) {
			outbuffer.put(newline);
		}
		outbuffer.flip();
		chan.write(outbuffer);
	}

	/**
	 * Test if we are connected.
	 * 
	 * @return {@code true} when connected.
	 */
	public boolean isOpen() {
		return chan.isOpen();
	}

	/**
	 * Close the connection.
	 * 
	 * @throws IOException
	 *             When closing failed
	 */
	public void close() throws IOException {
		chan.close();
	}
}
