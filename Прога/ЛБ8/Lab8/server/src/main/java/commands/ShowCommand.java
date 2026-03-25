package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;


public class ShowCommand implements Command{
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }


    @Override
    public Response execute(Request request) {
        var collection = collectionManager.getCollection();

        if (collection.isEmpty()) {
            return new Response("Коллекция пуста.", true, new ArrayList<>());
        }
        java.util.List<LabWork> sortedList = collectionManager.getCollection().values().stream()
                .sorted(java.util.Comparator.comparing(LabWork::getCoordinates))
                .collect(java.util.stream.Collectors.toList());
        return new Response("ОК", true, sortedList);
    }

    @Override
    public String getDescription() {
        return "Выводит все элементы коллекции в строковом представлении";
    }
}
