package managers;

import network.Request;
import network.Response;
import network.SerializationUtils;

import java.io.IOException;
import java.net.*;

/**
 * Ядро-клиента, выполняющее операции отправки и получения данных
 */
public class UDPClient {
    /**
     * Окно для отправки и приёма данных
     */
    private final DatagramSocket socket;
    /**
     * Адрес сервера, куда отправляются данные
     */
    private final InetSocketAddress serverAddress;

    /**
     * Конструктор класса
     * @param host имя хоста
     * @param port номер порта
     * @throws SocketException если от сервера не последовало ответа, то выбросит данную ошибку
     * @throws UnknownHostException если хост не был найден
     */
    public UDPClient(String host, int port) throws SocketException, UnknownHostException {
        //открываем любое окно, прога сама выберет свободный порт клиента
        this.socket = new DatagramSocket();

        //InetAddress.getByName(host) превращает строку localhost во внутренний формат IP
        this.serverAddress = new InetSocketAddress(InetAddress.getByName(host), port);

        //если ответа нет 3 секунд, то вылетит ошибка
        this.socket.setSoTimeout(3000);
    }

    /**
     * Отправляет объект Request на сервер
     * @param request объект запроса (имя команды, аргумент команды, сложный объект)
     * @throws IOException если произошла ошибка при сериализации или отправке
     */
    public void sendRequest(Request request) throws IOException {
        //впервые юзаем возможности слона
        byte[] sendData = SerializationUtils.serialize(request);

        //создаём объект, который подходит под параметры передачи данных по UDP
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress);

        //отправляем
        socket.send(sendPacket);

    }

    /**
     * Принимает объект Request на клиентской стороне
     * @return объект Response от сервера, либо специально сформированный Response с ошибкой
     * @throws IOException  если произошла ошибка при десериализации или отправке
     */
    public Response receiveResponse() throws IOException {
        //создаём пустой массив, который будет заполняться данными
        byte[] receiveBuffer = new byte[65536];

        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        try {
            socket.receive(receivePacket);

            return (Response) SerializationUtils.deserialize(receivePacket.getData());
        } catch (SocketException e) {
            return new Response("\u001B[31mОшибка:\u001B[0m Сервер не отвечает. Убедитесь, что он запущен.", false);
        } catch (ClassNotFoundException e) {
            return new Response("\u001B[31mОшибка:\u001B[0m Полученные данные невалидны", false);
        }

    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
