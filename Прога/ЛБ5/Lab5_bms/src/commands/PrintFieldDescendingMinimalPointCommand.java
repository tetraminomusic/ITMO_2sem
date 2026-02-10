package commands;

import managers.CollectionManager;
import models.LabWork;

import java.util.Comparator;

public class PrintFieldDescendingMinimalPointCommand  implements Command {
    private final CollectionManager collectionManager;

    public PrintFieldDescendingMinimalPointCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        collectionManager.getCollection().values().stream()
                .map(LabWork::getMinimalPoint) //оставляем только баллы
                .sorted(Comparator.reverseOrder()) // сортируем по убыванию
                .forEach(System.out :: println); // печатаем каждый элемент
    }

    @Override
    public String getDescription() {
        return "Выводит значения поля minimalPoint для всех элементов в порядке убывания";
    }
}
