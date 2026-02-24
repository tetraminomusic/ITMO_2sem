import managers.CollectionManager;
import managers.CommandManager;
import managers.FileManager;
import network.Request;
import network.Response;
import network.UDPServer;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.logging.Logger;

public class ServerMain {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        try {
            CollectionManager collectionManager = new CollectionManager();
            FileManager fileManager = new FileManager("LAB_DATA_PATH");

            collectionManager.getCollection().putAll(fileManager.read());

            CommandManager commandManager = new CommandManager(collectionManager, fileManager);
            UDPServer server = new UDPServer(12345);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Завершение работы... Сохранение коллекции.");
                fileManager.write(collectionManager.getCollection());
            }));

            logger.info("Сервер готов к выполнению команд.");

            byte[] buffer = new byte[65536];
            while (true) {
                Request request = server.receiveRequest(buffer);

                if (request != null) {
                    Response response = commandManager.execute(request);

                    server.sendResponse(response, request.getClientAddress());
                }

                Thread.sleep(10);
            }
        } catch (Exception e) {
            logger.info("Критическая ошибка сервера:" + e.getMessage());
        }
        }
    }
}
