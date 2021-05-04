package SuperChat.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Client creating problem...");
        }
    }


    public void authentication() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                String nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        name = nick;
                        myServer.broadcastMsg(name + " has entered to chat.");
                        myServer.subscribe(this);
                        return;
                    } else {
                        sendMsg("This account is already in use!");
                    }
                } else {
                    sendMsg("Login or Password is incorrect!");
                }
            }
        }
    }


    public void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            System.out.println("from " + name + ": " + strFromClient);
            if (strFromClient.equals("/end")) {
                return;
            }
            if (strFromClient.startsWith("/w")) {
                String nick = (strFromClient.split("\\s")[1]);
                System.out.println("from " + name + " to " + nick + " : " + strFromClient);
                myServer.privateMsg(strFromClient, nick);
            } else {
                myServer.broadcastMsg(name + ": " + strFromClient);
            }
        }
    }


    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + " has left the chat.");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
