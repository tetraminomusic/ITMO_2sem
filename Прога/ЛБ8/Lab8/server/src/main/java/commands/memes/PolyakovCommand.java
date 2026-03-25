package commands.memes;

import commands.Command;
import managers.CollectionManager;
import network.Request;
import network.Response;

import java.util.Random;

public class PolyakovCommand implements Command {
    private final int MAX_ADDITIONS = 7;

    public PolyakovCommand() {
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
    public Response execute(Request request) {
        String arg = request.getArgument();
        if (arg == null || arg.isEmpty()) {
            return new Response(lexicon("Надо что-то написать в аргумент моей команды..."), true, null);
        } else {
            return new Response(lexicon(arg), true, null);
        }
    }

    @Override
    public String getDescription() {
        return lexicon("Работает как команда echo из bash'a");
    }

}
