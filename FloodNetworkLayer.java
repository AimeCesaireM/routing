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
public class FloodNetworkLayer extends NetworkLayer {
// =============================================================================



    // =========================================================================
    // PUBLIC METHODS
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
        byte[] idArray = intToBytes(packetCreatedNumber);
        byte[] ttlArray = intToBytes(timeToLive);
        byte[] sourceArray = intToBytes(getAddress());
        byte[] destinationArray = intToBytes(destination);

        byte[] packet = new byte[lengthArray.length + idArray.length + ttlArray.length +
                                sourceArray.length + destinationArray.length + data.length];

        copyInto(packet, lengthOffset, lengthArray);
        copyInto(packet, idOffset, idArray);
        copyInto(packet, ttlOffset, ttlArray);
        copyInto(packet, sourceOffset, sourceArray);
        copyInto(packet, destinationOffset, destinationArray);
        copyInto(packet, bytesPerHeader, data);

        ++ packetCreatedNumber;

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
        System.err.println("[-] Error: Route() called");
        System.exit(-1);
        return null;
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
        // minimum packet size is 20 bytes: 19 bytes for the header + 1 byte for payload.
        if (buffer.size() < bytesPerHeader + 1) return null;

        byte[] lengthArray = new byte[Integer.BYTES];
        Iterator<Byte> iterator = buffer.iterator();

        for (int i = 0; i < lengthArray.length; ++i)  lengthArray[i] = iterator.next();

        int dataLength = bytesToInt(lengthArray);

        // if the length specified plus the 20 bytes of the header is greater than the available stuff
        if (bytesPerHeader + dataLength > buffer.size() || dataLength < 1) return null;

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

        byte[] data = new byte[packet.length - bytesPerHeader];
        copyFrom(data, packet, bytesPerHeader);

        byte[] idArr = new byte[Integer.BYTES];
        copyFrom(idArr, packet, idOffset);
        int id = bytesToInt(idArr);

        // I am the destination and have not received that packet before
        if (destination == getAddress()){
            if (!receivedPacketIDs.contains(id)){
                receivedPacketIDs.add(id);
                client.receive(data);
            }
        }
        else{
            byte[] ttlArray = new byte[Integer.BYTES];
            copyFrom(ttlArray, packet, ttlOffset);
            int ttl = bytesToInt(ttlArray);
            // decrement the time to live and send to everyone.
            if (ttl > 0){
                -- ttl;
                byte[] newTtlArray = intToBytes(ttl);
                copyInto(packet, ttlOffset, newTtlArray);

                for (Map.Entry<Integer, DataLinkLayer> dl: dataLinkLayers.entrySet()){
                    dl.getValue().send(packet);
                }
            }
        }
    } // processPacket ()

    @Override
    public void send(String destination, byte[] data) {
        // Determine the address of the destination.
        int destinationAddress = destination.hashCode();

        // Loop through the data in packet-size chunks.
        int numPackets = ((data.length / MAX_PACKET_SIZE) +
                (data.length % MAX_PACKET_SIZE == 0 ? 0 : 1));
        for (int i = 0; i < numPackets; i += 1) {

            // Grab the next packet-worth of data a make of packet of it.
            int start = i * MAX_PACKET_SIZE;
            int end = Math.min((i + 1) * MAX_PACKET_SIZE,
                    data.length);
            byte[] packetData = Arrays.copyOfRange(data, start, end);
            byte[] packet = createPacket(destinationAddress, packetData);

            // send to all the data links
            for (Map.Entry<Integer, DataLinkLayer> dataLink : dataLinkLayers.entrySet()) {
                dataLink.getValue().send(packet);
                if (debug) {
                    System.err.printf("Address %d sent packet:\n\t%s\n",
                            address,
                            bytesToString(packet));
                }
            }
        }
    }


    // =========================================================================

    // =========================================================================
    // INSTANCE DATA MEMBERS

    // =========================================================================
    // CLASS DATA MEMBERS
    /** The time to live for each packet in the network. **/
    // Note that different senders could also implement different time to live for their packets
    // but we will make it uniform in this network.
    public static final int     timeToLive         = 4;

    /** The offset into the header for the length. */
    public static final int     lengthOffset      = 0;

    /** The offset into the header for the packet id **/
    public static final int     idOffset        = lengthOffset + Integer.BYTES;

    /** The offset into the header for the time to live **/
    public static final int     ttlOffset        = idOffset + Integer.BYTES;

    /** The offset into the header for the source address. */
    public static final int     sourceOffset      = ttlOffset + Integer.BYTES;

    /** The offset into the header for the destination address. */
    public static final int     destinationOffset = sourceOffset + Integer.BYTES;

    /** How many total bytes per header. */
    public static final int     bytesPerHeader    = destinationOffset + Integer.BYTES;

    /** Whether to emit debugging information. */
    public static final boolean debug             = false;

    public static int packetCreatedNumber = 0;

    private final HashSet<Integer> receivedPacketIDs = new HashSet<>();
    // =========================================================================



// =============================================================================
} // class RandomNetworkLayer
// =============================================================================

