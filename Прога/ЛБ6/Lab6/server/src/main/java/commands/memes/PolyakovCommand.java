package commands.memes;

import commands.Command;
import managers.CollectionManager;

import java.util.Random;

/**
 * Команда, которая выводит аргумент со случайным дублированием букв "к" и "п" (заикание).
 *  @author Малых Кирилл Романович
 *  @version 1.0
 */
public class PolyakovCommand implements Command {
    /**
     * Максимальное количество дублирований "к" и "п".
     */
    private final int MAX_ADDITIONS = 7;

    /**
     * Конструктор команды.
     */
    public PolyakovCommand() {
    }

    /**
     * Дублирование в строке букв "к" и "п".
     * @param str исходная строка (или аргумент команды).
     * @return преобразованная "заиканием" строка.
     */
    private String lexicon(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            result.append(currentChar);
            if (currentChar == 'к' || currentChar == 'К' || currentChar == 'п' || currentChar == 'П') {
                int numAdditions = random.nextInt(MAX_ADDITIONS);
                for (int j = 0; j < numAdditions; j++) result.append(currentChar);
            }
        }
        return result.toString();
    }

    /**
     * Выполнение логики команды
     * @param arg строка, в которой мы хотим сделать дублирование. Может быть null.
     */
    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println(lexicon("Надо что-то написать в аргумент моей команды..."));
        } else {
            System.out.println(lexicon(arg));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return lexicon("Работает как команда echo из bash'a");
    }

}
