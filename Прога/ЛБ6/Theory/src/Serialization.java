import javax.net.ssl.SNIServerName;
import java.io.*;

public class Serialization  {
    public static void main(String[] args) {
            Sender s = new SmsSender();
            s.deliver();
            s = new EmailSender();
            s.deliver();
        }
    }

interface Msg {
    void send();
}
class Sms implements Msg {
    public void send() {
        System.out.println("СМС, ЁПТА!");
    }
}
class Email implements Msg {
    public void send() {
        System.out.println("МЫЛО, БЛЯТЬ!");
    }
}
abstract class Sender {
    abstract Msg createMsg(); //сам фабричный метод
    void deliver() {
        Msg m = createMsg();
        m.send();
    }
}

class SmsSender extends Sender {
    Msg createMsg() {
        return new Sms();
    }
}

class EmailSender extends Sender {
    Msg createMsg() {
        return new Email();
    }
}