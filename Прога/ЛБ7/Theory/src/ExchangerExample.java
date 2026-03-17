import java.util.concurrent.Exchanger;

public class ExchangerExample {

    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                String data = exchanger.exchange("Данные от А");
                System.out.println("Поток А получил данные: " + data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                String data = exchanger.exchange("Данные от B");
                System.out.println("Поток B получил данные: " + data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

}
