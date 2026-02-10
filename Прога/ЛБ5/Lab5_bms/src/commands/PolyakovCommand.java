package commands;

import managers.CollectionManager;

import java.util.Random;

public class PolyakovCommand implements Command {
    private final CollectionManager collectionManager;
    final int MAX_ADDITIONS = 7;

    public PolyakovCommand(CollectionManager manager) {
        this.collectionManager = manager;
    }

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

    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println(lexicon("Надо что-то написать в аргумент моей команды..."));
        } else {
            System.out.println(lexicon(arg));
        }
    }

    @Override
    public String getDescription() {
        return lexicon("Работает как команда echo из bash'a");
    }

}
