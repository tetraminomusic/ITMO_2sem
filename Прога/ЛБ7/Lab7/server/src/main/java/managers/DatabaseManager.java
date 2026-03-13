package managers;

import models.LabWork;
import network.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Класс, отвечающий за подключение к БД
 */
public class DatabaseManager {
    /**
     * Логгер для проверки подключения к серваку
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    /**
     * Адрес БД
     */
    private final String url;
    /**
     * Имя пользователя
     */
    private final String user;
    /**
     * Пароль пользователя
     */
    private final String password;

    /**
     * Объект подключения к БД
     */
    private Connection connection;

    /**
     * Конструктор менеджера подключения к БД
     * @param host - хост БД (pg)
     * @param dbname имя базы данных (studs)
     * @param user - имя пользователя
     * @param password - пароль пользователя
     */
    public DatabaseManager(String host, String dbname, String user, String password) {
        this.url = "jdbc:postgresql://" + host + ":5433/" + dbname;
        this.user = user;
        this.password = password;
    }

    /**
     * Устанавливает связь с БД
     */

    public void connect() {
        try {
            //говорим джаве, какой драйвер юзать. джава принудительно загружает класс драйвера в память.
            Class.forName("org.postgresql.Driver");
            //открываем соединение
            this.connection = DriverManager.getConnection(url, user, password);
            //делаем отчёт в логгер
            logger.info("Успешно подключение в базе данных: {}", url);
        } catch (ClassNotFoundException e) {
            logger.error("Драйвер Постгреса не был найден!");
        } catch (SQLException e) {
            //ошибка вылетает, если логин или пароль неправильны
            logger.info("Ошибка при подключении к БД: {}", e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Геттер для получения текущего состояния соединения
     * @return текущее состояние соединения
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Закрывает соединение при отключении сервера
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Соединение с БД закрыто.");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при закрытии БД: {}", e.getMessage());
        }
    }

    /**
     * Аутенфикация пользователя к БД
     * @param login логин пользователя
     * @param password пароль пользователя
     * @return булеаново значение об успешной или неуспешной аутенфикации пользователя
     */
    public synchronized boolean authenticate(String login, String password) {
        //хешируем пароль
        String passwordHash = PasswordHasher.hashPassword(password);

        //пробуем найти пользователя
        //юзаем ?, чтобы не было sql инъекций
        String selectQuery = "SELECT password_hash FROM users WHERE login = ?";

        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            //подставляем за место первого знака вопроса
            ps.setString(1, login);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                //нашли пользователя, класс. проверяем на пароль
                String storedHash = rs.getString("password_hash");
                return storedHash.equals(passwordHash);
            } else {
                //регистрация
                return registerUser(login, passwordHash);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при авторизации пользователя: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Вспомогательный метод для создания нового юзера
     * @param login логин юзера
     * @param hash захешированный пароль
     * @return успешность добавления нового юзера в БД
     * @throws SQLException ошибки, связанные с добавлением данных в БД
     */
    private boolean registerUser(String login, String hash) throws SQLException {
        String insertQuery = "INSERT INTO users (login, password_hash) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setString(1, login);
            ps.setString(2, hash);
            //выполняем запись в таблицу
            ps.executeUpdate();
            logger.info("Зарегистрирован новый пользователь: {}", login);
            return true;
        }
    }

    /**
     * Удаляет объект из БД
     * удалять можно только свои объекты
     * @param id номер объекта, который мы хотим удалить
     * @param userLogin - юзер, который хочет удалить объект
     * @return
     */
    public synchronized boolean deleteLabWork(int id, String userLogin) {
        String query = "DELETE FROM labworks WHERE id = ? AND owner_login = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.setString(2, userLogin);

            //возвращает количество удалённых строк
            int rowsDeleted = ps.executeUpdate();

            //если удалено больше 0 строк, значит ID существовал и он наш
            return rowsDeleted > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении объекта из БД: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Метод для вставки новых объектов в БД с возвратом ID объекта
     * @param lab избранный объект
     * @param userLogin юзер, который добавляет новый объект
     * @return id сохранённого объекта. Возвращает -1, если операция произошла неуспешно
     */
    public synchronized int insertLabWork(LabWork lab, String userLogin) {
        //returning id позволяет вернуть id, который присвоила ему бд
        String query = "INSERT INTO labworks (name, x, y, creation_date, minimal_point, " +
                "description, tuned_in_works, difficulty, author_name, author_birthday, " +
                "author_height, author_weight, author_passport_id, owner_login) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            //имя лабы
            ps.setString(1, lab.getName());

            //координаты
            ps.setLong(2, lab.getCoordinates().getX());
            ps.setLong(3, lab.getCoordinates().getY());

            //дата создания с конвертаций в формат sql
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(lab.getCreationDate()));

            //минимальный балл
            ps.setFloat(5, lab.getMinimalPoint());

            //описание
            ps.setString(6, lab.getDescription());

            //вовлечённость в лабу
            if (lab.getTunedInWorks() != null) {
                ps.setInt(7, lab.getTunedInWorks());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }

            //сложность
            ps.setString(8, lab.getDifficulty().toString());

            if (lab.getAuthor() != null) {
                ps.setString(9, lab.getAuthor().getName());
                ps.setTimestamp(10, java.sql.Timestamp.valueOf(lab.getAuthor().getBirthday()));
                ps.setFloat(11, lab.getAuthor().getHeight());

                if (lab.getAuthor().getWeight() != null) ps.setDouble(12, lab.getAuthor().getWeight());
                else ps.setNull(12, java.sql.Types.DOUBLE);

                ps.setString(13, lab.getAuthor().getPassportID());
            } else {
                for (int i = 9; i <= 13; i++) ps.setNull(i, java.sql.Types.NULL);
            }

            ps.setString(14, userLogin);

            ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int generatedID = rs.getInt("id");
                    logger.info("Объект успешно сохранён в БД под ID: {}", generatedID);
                    return generatedID;
                }

        } catch (SQLException e) {
            logger.error("Ошибка при выполнении INSERT в БД: {}", e.getMessage());
        }

        return -1;
    }

    /**
     * Обновляет объект в БД
     * @param id уникальный ключ объекта
     * @param lab объект, который мы хотим вставить как обновлённый
     * @param userLogin юезр, который совершает обновление объекта
     * @return успешно ли выполнялись операция
     */
    public synchronized boolean updateLabWork(int id, LabWork lab, String userLogin) {
        //юзаем WHERE в конце для защиты
        String query = "UPDATE labworks SET " +
                "name = ?, x = ?, y = ?, minimal_point = ?, description = ?, " +
                "tuned_in_works = ?, difficulty = ?, author_name = ?, " +
                "author_birthday = ?, author_height = ?, author_weight = ?, " +
                "author_passport_id = ? " +
                "WHERE id = ? AND owner_login = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, lab.getName());
            ps.setLong(2, lab.getCoordinates().getX());
            ps.setLong(3, lab.getCoordinates().getY());
            ps.setFloat(4, lab.getMinimalPoint());
            ps.setString(5, lab.getDescription());

            //Tuned in work. Может быть null
            if(lab.getTunedInWorks() != null) ps.setInt(6, lab.getTunedInWorks());
            else ps.setNull(6, java.sql.Types.INTEGER);

            //сложность
            ps.setString(7, lab.getDifficulty().toString());

            //поля автора
            if (lab.getAuthor() != null) {
                ps.setString(8, lab.getAuthor().getName());
                ps.setTimestamp(9, java.sql.Timestamp.valueOf(lab.getAuthor().getBirthday()));
                ps.setFloat(10, lab.getAuthor().getHeight());

                if (lab.getAuthor().getWeight() != null) ps.setDouble(11, lab.getAuthor().getWeight());
                else ps.setNull(11, java.sql.Types.DOUBLE);

                ps.setString(12, lab.getAuthor().getPassportID());
            } else {
                for (int i = 8; i <= 12; i++) ps.setNull(i, java.sql.Types.NULL);
            }

            //условия поиска WHERE

            ps.setInt(13, id);
            ps.setString(14, userLogin);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при выполнении UPDATE в БД: {}", e.getMessage());
            return false;
        }
    }

    public synchronized boolean clearUserObjects(String userLogin) {
        String query = "DELETE FROM labworks WHERE owner_login = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userLogin);
            int deleted = ps.executeUpdate();
            logger.info("Пользователь {} очистил свои объекты ({} шт.)", userLogin, deleted);
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка при очистке объектов пользователя {}: {}", userLogin, e.getMessage());
            return false;
        }
    }
}
