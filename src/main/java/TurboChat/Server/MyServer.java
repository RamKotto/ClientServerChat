package TurboChat.Server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private final int PORT = 8189;

    private List<ClientHandler> clients;
    private AuthService authService;


    public AuthService getAuthService() {
        return authService;
    }


    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Server waiting for connection...");
                Socket socket = server.accept();
                System.out.println("Client has connected!");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("Server side error!");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }


    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }


    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }


    public synchronized void sendMsgToClient(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nickTo)) {
                o.sendMsg("From: " + from.getName() + ": " + msg);
                from.sendMsg("To Client " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg(nickTo + " is not in the room! Sorry!");
    }


    public synchronized void broadcastClientsList() {
        StringBuilder sb = new StringBuilder("/clients: ");
        for (ClientHandler o : clients) {
            sb.append(o.getName());
            sb.append(" ");
        }
        broadcastMsg(sb.toString());
    }


    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
        broadcastClientsList();
    }


    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
        broadcastClientsList();
    }
}
