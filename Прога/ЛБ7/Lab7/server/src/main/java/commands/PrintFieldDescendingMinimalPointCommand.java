package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Команда, которая выводит значения поля minimalPoint для всех элементов в порядке убывания.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class PrintFieldDescendingMinimalPointCommand  implements Command {
    /**
     * Менеджер коллекций, из которого извлекается сама коллекция.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекций, из которого извлекается сама коллекция
     */
    public PrintFieldDescendingMinimalPointCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды
     * @param request аргумент команды. Не используется в данной команде.
     */
    @Override
    public Response execute(Request request) {
        String result = collectionManager.getCollection().values().stream()
                .map(LabWork::getMinimalPoint) // Достаем float
                .sorted(Comparator.reverseOrder()) // Сортируем
                .map(String::valueOf) // Превращаем в String (иначе joining не сработает)
                .collect(Collectors.joining("\n")); // Склеиваемт
        if (result.isEmpty()) {
            return new Response("Коллекция пуста, баллов нет", true);
        } else {
            return new Response("Список баллов по убыванию:\n" + result, true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Выводит значения поля minimalPoint для всех элементов в порядке убывания";
    }
}
