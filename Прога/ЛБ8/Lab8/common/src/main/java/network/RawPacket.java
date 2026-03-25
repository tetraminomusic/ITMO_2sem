package network;

import java.net.SocketAddress;

public class RawPacket {
    private byte[] data;
    private SocketAddress address;

    public byte[] getData() {
        return data;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public void setAddress(SocketAddress socketAddress) {
        this.address = socketAddress;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}