import managers.CollectionManager;
import managers.CommandManager;
import managers.DatabaseManager;
import network.UDPServer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Точка входа для сервера
 * Класс инициализирует все системы сервера, настраивает безопасное выключение сервера и обеспечивает многопоток сетевых запросов
 */
public class ServerMain {
    /**
     * Логгер для записи логов по потокам в консоль сервера
     */
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    /**
     * Порт для получения UDP пакетов со стороны клиента
     */
    private static final int PORT = 12345;
    /**
     * Пул потоков, которые используются для распараллеливания процесса обработки данных на стороне сервера
     */
    private static final ExecutorService processingPool = Executors.newFixedThreadPool(10);

    /**
     * Точка входа в программу
     * @param arg
     * @throws IOException ошибки при неудачной передачи пакетов по сети
     */
    public static void main(String[] arg) throws IOException {
        //данные от бд вытаскиваются из соответствующих переменных окружения
        String dbHost = System.getenv("DB_HOST"); // хост бд
        String dbName = System.getenv("DB_NAME"); // имя бд
        String dbUser = System.getenv("DB_USER"); // юзер бд
        String dbPass = System.getenv("DB_PASS"); // пароль юзера бд

        if (dbPass == null) {
            logger.error("Переменная окружения DB_PASS не установлена");
            return;
        }

        //подключаемся к бд
        DatabaseManager databaseManager = new DatabaseManager(dbHost, dbName, dbUser, dbPass);
        databaseManager.connect();

        //Загружаем коллекцию из БД в память
        CollectionManager collectionManager = new CollectionManager(databaseManager);
        collectionManager.loadFromDatabase();

        //инициализирует сеть и обработчик
        CommandManager commandManager = new CommandManager(collectionManager, databaseManager);
        UDPServer udpServer = new UDPServer(PORT);

        //при выключении программы мы должны закрыть соединение и закрыть пул потоков
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Завершение работы сервера");
            databaseManager.close();
            processingPool.shutdown();
            logger.info("Ресурсы освобождены");
        }));

        while (true) {
            // читаем запрос, используя многопоток. Таким образом, мы можем отдать десериализацию другому потоку, пока поток чтения считывает новые данные
            try {
                var request = udpServer.receiveRequest();

                if (request != null) {
                    //создаём поток, который будет распаковывать и анализировать запрос
                    new Thread(() -> {

                        //отправляем задачу в пул
                        processingPool.submit(() -> {
                            logger.info("Обработка запроса '{}' от {}", request.getCommandName(), request.getLogin());

                            //выполняем команду и получаем ответ
                            var response = commandManager.handle(request);

                            //для многопоточной отправки использовать новый поток (ТЗ 3 про многопоток)
                            new Thread(() -> {
                                udpServer.sendResponse(response, request.getClientAddress());
                            }).start();
                        });
                    }).start();
                }
            } catch (Exception e) {
                logger.error("Ошибка в главном цикле обработки: {}", e.getMessage());
            }

            //чтобы проц не сдох
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
