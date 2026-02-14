package commands;

import managers.CollectionManager;
import models.LabWork;

import java.util.Comparator;

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
     * @param arg аргумент команды. Не используется в данной команде.
     */
    @Override
    public void execute(String arg) {
        collectionManager.getCollection().values().stream()
                .map(LabWork::getMinimalPoint) //оставляем только баллы
                .sorted(Comparator.reverseOrder()) // сортируем по убыванию
                .forEach(System.out :: println); // печатаем каждый элемент
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Выводит значения поля minimalPoint для всех элементов в порядке убывания";
    }
}
