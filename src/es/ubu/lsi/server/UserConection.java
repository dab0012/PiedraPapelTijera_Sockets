package es.ubu.lsi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import es.ubu.lsi.common.Util;

public class UserConection {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private int playerId;


    public UserConection(ServerSocket s) {
        try {
            // Se crea el socket y se
            // Acepta la solicitud entrante al socket
            this.clientSocket = s.accept();
            Util.printFormated("Conexion entrante de: " + clientSocket.getRemoteSocketAddress().toString(), "+");
            
            // Creamos un Stream de datos de entrada y otro de salida
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void send(Object o){
        Util.sendTo(out, o);
    }

    public void finalize() {

        try {
            // Cerramos los stream
            this.in.close();
            this.out.close();

            // Cerramos los socket
            this.clientSocket.close();

        } catch (IOException e) {
        }


    }


    public Socket getClientSocket() {
        return clientSocket;
    }


    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


    public PrintWriter getOut() {
        return out;
    }


    public void setOut(PrintWriter out) {
        this.out = out;
    }


    public BufferedReader getIn() {
        return in;
    }


    public void setIn(BufferedReader in) {
        this.in = in;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public int getPlayerId() {
        return playerId;
    }


    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    


    
    
}
