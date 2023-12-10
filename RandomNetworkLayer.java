// =============================================================================
// IMPORTS

import java.util.*;
// =============================================================================



// =============================================================================
/**
 * @file   RandomNetworkLayer.java
 * @author Scott F. Kaplan (sfkaplan@cs.amherst.edu)
 * @date   April 2022
 *
 * A network layer that perform routing via random link selection.
 */
public class RandomNetworkLayer extends NetworkLayer {
// =============================================================================



    // =========================================================================
    // PUBLIC METHODS
    // =========================================================================



    // =========================================================================
    /**
     * Default constructor.  Set up the random number generator.
     */
    public RandomNetworkLayer () {

	random = new Random();

    } // RandomNetworkLayer ()
    // =========================================================================



    // =========================================================================
    /**
     * Create a single packet containing the given data, with header that marks
     * the source and destination hosts.
     *
     * @param destination The address to which this packet is sent.
     * @param data        The data to send.
     * @return the sequence of bytes that comprises the packet.
     */
    protected byte[] createPacket (int destination, byte[] data) {
        // COMPLETE ME

        byte[] lengthArray = intToBytes(data.length);
        byte[] sourceArray = intToBytes(getAddress());
        byte[] destinationArray = intToBytes(destination);

        byte[] packet = new byte[lengthArray.length + sourceArray.length + destinationArray.length + data.length];

        copyInto(packet, lengthOffset, lengthArray);
        copyInto(packet, sourceOffset, sourceArray);
        copyInto(packet, destinationOffset, destinationArray);
        copyInto(packet, bytesPerHeader, data);

        return packet;

    } // createPacket ()
    // =========================================================================



    // =========================================================================
    /**
     * Randomly choose the link through which to send a packet given its
     * destination.
     *
     * @param destination The address to which this packet is being sent.
     */
    protected DataLinkLayer route (int destination) {

        // COMPLETE ME
        LinkedList<Map.Entry<Integer, DataLinkLayer>> mapEntries = new LinkedList<>(dataLinkLayers.entrySet());
        int randomIndex = random.nextInt(mapEntries.size());
        Map.Entry<Integer, DataLinkLayer> randomEntry = mapEntries.get(randomIndex);
        return randomEntry.getValue();
    } // route ()
    // =========================================================================



    // =========================================================================
    /**
     * Examine a buffer to see if it's data can be extracted as a packet; if so,
     * do it, and return the packet whole.
     *
     * @param buffer The receive-buffer to be examined.
     * @return the packet extracted packet if a whole one is present in the
     *         buffer; <code>null</code> otherwise.
     */
    protected byte[] extractPacket (Queue<Byte> buffer) {

        // COMPLETE ME
        // minimum packet size is 13 bytes: 12 bytes for the header + 1 byte for payload.
        if (buffer.size() < bytesPerHeader + 1) return null;

        byte[] lengthArray = new byte[Integer.BYTES];
        Iterator<Byte> iterator = buffer.iterator();

        for (int i = 0; i < lengthArray.length; ++i)  lengthArray[i] = iterator.next();

        int dataLength = bytesToInt(lengthArray);

        // if the length specified plus the 12 bytes of the header is greater than the available stuff
        if (bytesPerHeader + dataLength > buffer.size()) return null;

        //else
        byte[] packet = new byte[bytesPerHeader + dataLength];

       for (int i = 0; i < packet.length; ++i) packet[i] = buffer.remove();

       return packet;

    } // extractPacket ()
    // =========================================================================



    // =========================================================================
    /**
     * Given a received packet, process it.  If the destination for the packet
     * is this host, then deliver its data to the client layer.  If the
     * destination is another host, route and send the packet.
     *
     * @param packet The received packet to process.
     * @see   createPacket
     */
    protected void processPacket (byte[] packet) {

	// COMPLETE ME

        byte[] destinationArray = new byte[Integer.BYTES];
        copyFrom(destinationArray, packet, destinationOffset);
        int destination = bytesToInt(destinationArray);

        if (destination == getAddress()){
            byte[] data = new byte[packet.length - bytesPerHeader];
            copyFrom(data, packet, bytesPerHeader);

            client.receive(data);
        }
        else{
            DataLinkLayer dl = route(destination);
            dl.send(packet);
        }
    } // processPacket ()

    // =========================================================================

    // =========================================================================
    // INSTANCE DATA MEMBERS

    /** The random source for selecting routes. */
    private Random random;
    // =========================================================================



    // =========================================================================
    // CLASS DATA MEMBERS

    /** The offset into the header for the length. */
    public static final int     lengthOffset      = 0;

    /** The offset into the header for the source address. */
    public static final int     sourceOffset      = lengthOffset + Integer.BYTES;

    /** The offset into the header for the destination address. */
    public static final int     destinationOffset = sourceOffset + Integer.BYTES;

    /** How many total bytes per header. */
    public static final int     bytesPerHeader    = destinationOffset + Integer.BYTES;

    /** Whether to emit debugging information. */
    public static final boolean debug             = true;
   // =========================================================================



// =============================================================================
} // class RandomNetworkLayer
// =============================================================================
