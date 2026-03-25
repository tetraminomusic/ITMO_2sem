package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;

import java.util.Comparator;
import java.util.stream.Collectors;


public class PrintFieldDescendingMinimalPointCommand  implements Command {
    private final CollectionManager collectionManager;

    public PrintFieldDescendingMinimalPointCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String result = collectionManager.getCollection().values().stream()
                .map(LabWork::getMinimalPoint) // Достаем float
                .sorted(Comparator.reverseOrder()) // Сортируем
                .map(String::valueOf) // Превращаем в String (иначе joining не сработает)
                .collect(Collectors.joining("\n")); // Склеиваемт
        if (result.isEmpty()) {
            return new Response("Коллекция пуста, баллов нет", true, null);
        } else {
            return new Response("Список баллов по убыванию:\n" + result, true, null);
        }
    }

    @Override
    public String getDescription() {
        return "Выводит значения поля minimalPoint для всех элементов в порядке убывания";
    }
}
