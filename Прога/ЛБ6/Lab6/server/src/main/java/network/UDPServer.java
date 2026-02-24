package network;

import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactoryFriend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

public class UDPServer {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(UDPServer.class);
    private final DatagramChannel channel;
    private final int bufferSize = 65536;

    public UDPServer(int port) throws IOException {
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.channel.bind(new InetSocketAddress(port));
        logger.info("Сервер запущен на порту {" + port + "}");
    }


    public void sendResponse(Response response, SocketAddress clientAddress) throws IOException {
        byte[] data = SerializationUtils.serialize(response);
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        channel.send(byteBuffer, clientAddress);
        logger.info("Отправлен ответ клиенту {" + clientAddress + "}");
    }

    public Request receiveRequest(byte[] buffer) throws IOException, ClassNotFoundException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        SocketAddress clientAddress = channel.receive(byteBuffer);

        if (clientAddress == null) return null;

        logger.info("Получен запрос от {" + clientAddress + "}");
        Request request = (Request) SerializationUtils.deserialize(buffer);
        request.setClientAddress(clientAddress);
        return request;
    }
}
