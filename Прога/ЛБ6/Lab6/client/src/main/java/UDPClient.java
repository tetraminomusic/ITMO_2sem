import network.*;

import java.io.IOException;
import java.net.*;

public class UDPClient {
    private final DatagramSocket socket;
    private final InetSocketAddress serverAddress;

    public UDPClient(String host, int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(5000);
        this.serverAddress = new InetSocketAddress(InetAddress.getByName(host), port);
    }

    public Response sendAndReceive(Request request) throws IOException, ClassNotFoundException {
        byte[] data = SerializationUtils.serialize(request);

        DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress);
        socket.send(sendPacket);

        byte[] buffer = new byte[65536];
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(receivePacket);

            return (Response) SerializationUtils.deserialize(receivePacket.getData());
        } catch (SocketTimeoutException e) {
            return new Response("Ошибка: Сервер временно недоступен. Попробуйте позже", false);
        }

    }



}
