package action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface ClientAction extends ActionListener {
    @Override
    void actionPerformed(ActionEvent e);
}
