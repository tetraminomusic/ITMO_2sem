package network;
import java.io.Serializable;

/**
 * Класс-ответ, объект которого используется для отправки результата работы команды на сервере клиенту.
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Текстовое сообщение-ответ (Как было раньше с обычным выводом сразу в консоль. Здесь же это будет передаваться в данный объект-ответ)
     */
    private final String message;

    /**
     * Флаг успеха (Нужно для корректной обработки ситуации с возникшими ошибками)
     */
    private final boolean success;

    /**
     * Конструктор класса
     * @param message сообщение-ответ
     * @param success флаг успешного выполнения команды
     */
    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    /**
     * Геттер сообщения-ответа
     * @return сообщение
     */
    public String getMessage() {
        return message;
    }

    /**
     * Геттер флага успеха
     * @return булево значение флага успеха (true или false)
     */
    public boolean getSuccess() {
        return success;
    }
}
