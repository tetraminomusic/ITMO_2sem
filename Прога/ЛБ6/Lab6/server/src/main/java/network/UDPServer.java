package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Класс-ядро сервера.
 */
public class UDPServer {
    /**
     * Логгер для записи действий сервера
     */
    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);

    /**
     * Поток для передачи данных по сети
     */
    private final DatagramChannel channel;

    /**
     * Конструктор класса
     * @param port порт, по которому будет находиться сервер
     * @throws IOException ошибка, связанная с некорректной передачи данных по сети
     */
    public UDPServer(int port) throws IOException {
        //открываем канал
        this.channel = DatagramChannel.open();

        //отключаем блокирующий режим
        this.channel.configureBlocking(false);

        //говорим ОС, что если пришёл пакет на данный порт, то забираем его себе
        this.channel.bind(new InetSocketAddress(port));

        logger.info("UDP Сервер успешно запущен на порту: {}", port);
    }


    /**
     * Принимает запросы со стороны клиента
     * @return сам запрос, который содержит команду, аргумент команды и, возможно, labwork
     */
    public Request receiveRequest() {
        try {
            //Создаём пустой буфер (как в клиенте при получении response)
            ByteBuffer buffer = ByteBuffer.allocate(65536);

            //пытаемся получить данные из канала
            SocketAddress clientAddress = channel.receive(buffer);

            if (clientAddress == null) {
                return null;
            }

            //закидываем инфу в логгер
            logger.info("Получен пакет данных от клиента: {}", clientAddress);

            //берём байты из потока и превращаем в массив байтов
            buffer.flip(); // Переключаем буфер в режим чтения
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data); // Копируем данные в массив
            //превращаем байты в объект Request
            Request request = (Request) SerializationUtils.deserialize(data);
            //прикрепляем адрес возврата, иначе сервер не поймёт, куда отправлять данные обратно
            request.setClientAddress(clientAddress);

            return request;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Ошибка при чтении запроса: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Отправляет ответ клиенту ответ на выполнение его запроса Response
     * @param response запрос, ответ на который мы и будем отправлять клиенту
     * @param clientAddress адрес клиента. Получаем его как адрес отправителя (отправляем ответ туда же, откуда нам и пришёл запрос)
     */
    public void sendResponse(Response response, SocketAddress clientAddress) {
        try {
            //превращаем объект в массив байтов
            byte[] sendData = SerializationUtils.serialize(response);

            //заворачиваем массив байтов в bytebuffer
            ByteBuffer buffer = ByteBuffer.wrap(sendData);

            channel.send(buffer, clientAddress);

            logger.info("Ответ отправлен клиенту: {}", clientAddress);
        } catch (IOException e) {
            logger.error("Не удалось отправить ответ клиенту {}: {}", clientAddress, e.getMessage());
        }
    }

    /**
     * Закрывает сетевой канал и освобождает сетевой порт.
     */
    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                logger.info("Сетевой канал сервера успешно закрыт.");
            }
        } catch (IOException e) {
            logger.error("Ошибка при закрытии сетевого канала: {}", e.getMessage());
        }
    }
}
