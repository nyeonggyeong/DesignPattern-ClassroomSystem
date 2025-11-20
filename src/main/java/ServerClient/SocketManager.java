/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ServerClient;

/**
 *
 * @author adsd3
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class SocketManager {
    private static Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    
    public static synchronized void setSocket(Socket s) {
        socket = s;
    }

    public static synchronized Socket getSocket() {
        return socket;
    }

    public static synchronized boolean isAlive() {
        return socket != null && !socket.isClosed();
    }

    public static synchronized void close() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                System.out.println("[SocketManager] 소켓 닫힘");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void setReader(BufferedReader in) { this.in = in; }
    
    public void setWriter(BufferedWriter out) { this.out = out; }
    
    public BufferedReader getReader() { return in; }
    
    public BufferedWriter getWriter() { return out; }
}