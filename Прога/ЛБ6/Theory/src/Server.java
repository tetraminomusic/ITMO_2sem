import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        ServerSocket server = new ServerSocket(1235);
        Socket socket = server.accept(); //ждём запроса клиента
        while (true) {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(scanner.nextLine());
            oos.flush();
            String inputString = (String) ois.readObject();
            System.out.println(inputString);
        }


    }
}
