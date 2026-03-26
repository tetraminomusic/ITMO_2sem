package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Команда для вывода всех значений minimalPoint в порядке убывания.
 */
public class PrintFieldDescendingMinimalPointCommand implements Command {
    private final CollectionManager collectionManager;

    public PrintFieldDescendingMinimalPointCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String result = collectionManager.getCollection().values().stream()
                .map(LabWork::getMinimalPoint)
                .sorted(Comparator.reverseOrder())
                .map(String::valueOf) // Превращаем числа в строки
                .collect(Collectors.joining("\n")); // Склеиваем через перенос строки

        if (result.isEmpty()) {
            return new Response("server.msg.empty", true, null);
        }

        return new Response("server.msg.print_desc_success", true, null, result);
    }

    @Override
    public String getDescription() {
        return "вывести значения поля minimalPoint всех элементов в порядке убывания";
    }
}