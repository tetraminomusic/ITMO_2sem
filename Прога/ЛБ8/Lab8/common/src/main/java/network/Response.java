package network;
import models.LabWork;

import java.io.Serializable;
import java.util.List;

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

    private final List<LabWork> collection;

    /**
     * Конструктор класса
     * @param message сообщение-ответ
     * @param success флаг успешного выполнения команды
     */
    public Response(String message, boolean success, List<LabWork> collection) {
        this.message = message;
        this.success = success;
        this.collection = collection;
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

    public List<LabWork> getCollection() {
        return collection;
    }
}
