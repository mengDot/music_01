import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(9999);
            Socket socket = s.accept();
            System.out.println("客户端"+socket.getInetAddress()+"连接了.....");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
