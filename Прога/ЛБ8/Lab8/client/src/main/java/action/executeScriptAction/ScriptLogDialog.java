package action.executeScriptAction;

import javax.swing.*;
import java.awt.*;

/**
 * Создаёт окно, в котором будет выводиться информация о выполнении скрипта.
 */
public class ScriptLogDialog extends JDialog {
    private final JTextArea logArea;

    public ScriptLogDialog(Frame parent) {
        super(parent, "Выполнение скрипта", false);

        logArea = new JTextArea(20,50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        add(new JScrollPane(logArea));
        pack();
        setLocationRelativeTo(parent);
    }

    public void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

}
