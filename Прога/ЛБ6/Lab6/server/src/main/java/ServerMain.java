import managers.CollectionManager;
import managers.CommandManager;
import managers.FileManager;
import network.Request;
import network.Response;
import network.UDPServer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Scanner;

/**
 * Точка входа для сервера
 * Грузит данные из json файла. Получает запросы со стороны клиента. Если запрос пришёл удачно, то запрос передаётся в commandManager и отправляет ответ
 */
public class ServerMain {
    /**
     * Главный логгер сервера
     */
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

    /**
     * Порт, который слушает сервер
     */
    private static final int PORT = 12345;

    /**
     * Точка входа в программу для сервера
     *
     */
    public static void main(String[] arg) {
        try {
            CollectionManager collectionManager = new CollectionManager();
            FileManager fileManager = new FileManager("LAB_DATA_PATH");

            //грузим данные из JSON при старте
            try {
                var loadedData = fileManager.read();
                if (loadedData != null) {
                    collectionManager.getCollection().putAll(loadedData);
                    logger.info("Коллекция загружена. Элементов: {}", loadedData.size());
                }
            } catch (Exception e) {
                logger.error("Ошибка при начальной загрузке файла: {}", e.getMessage());
            }

            //создаём сетевой модуль
            UDPServer server = new UDPServer(PORT);

            CommandManager commandManager = new CommandManager(collectionManager, fileManager);

            //Этот код выполняется, когда сервер останавливается и тд (Чтобы у нас было автоматическое сохранение данных в файл)
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Получен сигнал завершения. Сохранение данных");
                fileManager.write(collectionManager.getCollection());
                server.close();
                logger.info("Сервер успешно остановлен");
            }));

            logger.info("Сервер запушен и готов к работе");

            //бесконечный цикл работы

            Scanner serverScanner = new Scanner(System.in);

            while (true) {
                if (System.in.available() > 0) {
                    String serverCommand = serverScanner.nextLine().trim().toLowerCase();

                    if (serverCommand.equals("save")) {
                        // ТЗ: Сохранение при исполнении специальной команды сервера
                        fileManager.write(collectionManager.getCollection());
                        logger.info("Коллекция принудительно сохранена администратором сервера.");
                    } else if (serverCommand.equals("exit")) {
                        logger.info("Завершение работы через консоль сервера...");
                        fileManager.write(collectionManager.getCollection());
                        System.exit(0);
                    } else {
                        System.out.println("Сервер поддерживает только локальные команды: 'save', 'exit'.");
                    }
                }
                try {
                    //Пробуем получить запрос от клиента
                    Request request = server.receiveRequest();

                    if (request != null) {
                        logger.info("Обработки команды: {}", request.getCommandName());

                        //Выполняем запрос
                        Response response = commandManager.handle(request);
                        //Отправляем ответ
                        server.sendResponse(response, request.getClientAddress());
                    }
                } catch (Exception e) {
                    logger.info("Ошибка в главном цикле обработки: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Критическая ошибка при запуске сервера: {}", e.getMessage());
        }
    }
}
