/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.client;
    
import java.net.InetAddress;

/**
 *
 * @author Higor
 */
public class Server {
    private String serverOscAdress;
    private InetAddress serverIpAdress;
    private int port;

    public Server() {
    }
    
    public Server(String serverOscAdress, InetAddress serverIpAdress, int port) {
        this.serverOscAdress = serverOscAdress;
        this.serverIpAdress = serverIpAdress;
        this.port = port;
    }

    public String getServerOscAdress() {
        return serverOscAdress;
    }

    public void setServerOscAdress(String serverOscAdress) {
        this.serverOscAdress = serverOscAdress;
    }

    public InetAddress getServerIpAdress() {
        return serverIpAdress;
    }

    public void setServerIpAdress(InetAddress serverIpAdress) {
        this.serverIpAdress = serverIpAdress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Server{" + "serverOscAdress=" + serverOscAdress + ", serverIpAdress=" + serverIpAdress + ", port=" + port + '}';
    }

    
    
    
    
}
