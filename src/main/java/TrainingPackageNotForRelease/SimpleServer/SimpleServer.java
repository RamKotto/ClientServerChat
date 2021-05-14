package TrainingPackageNotForRelease.SimpleServer;


/*
Написать консольный вариант клиент\серверного приложения,
в котором пользователь может писать сообщения, как на клиентской стороне,
так и на серверной. Т.е. если на клиентской стороне написать "Привет",
нажать Enter то сообщение должно передаться на сервер и там отпечататься в консоли.
Если сделать то же самое на серверной стороне, сообщение соответственно передается
клиенту и печатается у него в консоли. Есть одна особенность,
которую нужно учитывать: клиент или сервер может написать несколько сообщений подряд,
такую ситуацию необходимо корректно обработать
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SimpleServer {
    public static void main(String[] args) {
        Socket socket = null;
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            System.out.println("Server is running... Waiting for connection.");
            socket = serverSocket.accept();
            System.out.println("Client has connected well.");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            break;
                        }
                        out.writeUTF("Echo: " + str);
                        System.out.println("Message from client: " + str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    while (true) {
                        Scanner scanner = new Scanner(System.in);
                        System.out.println("Write something for client: ");
                        String messageForClient = scanner.nextLine();
                        out.writeUTF("Message from server: " + messageForClient);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
