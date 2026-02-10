package commands;

public interface Command {
    void execute(String arg);

    String getDescription();

}
