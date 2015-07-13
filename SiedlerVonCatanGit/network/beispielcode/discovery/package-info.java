/**
 * Discover game servers on the <i>local</i> network.
 * 
 * The discovery is done as follows:
 * 
 * <ul>
 * <li>Both clients and servers listen to UDP broadcast messages on port 30303.
 * (This port is unassigned according to IANA.)</li>
 * <li>Messages a triples separated by null bytes, and encoded UTF-8.</li>
 * <li>The first field is a service id. Clients and servers should use different ids.</li>
 * <li>The second field is the port number used for communication, encoded as decimal ascii.</li>
 * <li>Clients advertise port 30303, to receive directed server announcements.</li>
 * <li>Servers advertise the port the client should use to connect.</li>
 * <li>The third field is a version identifier. It is a free form field.</li>
 * <li>Protocol messages are limited in length, therefore it is recommended to keep values
 * below 100 characters. Both clients and servers may silently discard too long messages.</li>
 * </ul>
 * 
 * Notes:
 * <ul>
 * <li>For the discovery to work, you must use the proper service ids.
 * For example "Catan-client" and "Catan-server".
 * Servers will be ignoring messages not coming from their own clients, and
 * Clients will ignore Servers with other ids.</li>
 * <li>The discovery uses stateless messaging via UDP datagrams. For stateful
 * connections to servers, you may want to use TCP sockets instead.</li>
 * </ul>
 * 
 * @author Erich Schubert
 */
package beispielcode.discovery;

