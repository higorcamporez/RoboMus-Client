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
    private String name;
    private String oscAdress;
    private InetAddress ipAdress;
    private int port;

    public Server() {
    }
    

    public Server(String name, String serverOscAdress, InetAddress serverIpAdress, int port) {
        this.name = name;
        this.oscAdress = serverOscAdress;
        this.ipAdress = serverIpAdress;
        this.port = port;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getOscAdress() {
        return oscAdress;
    }

    public void setOscAdress(String oscAdress) {
        this.oscAdress = oscAdress;
    }

    public InetAddress getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(InetAddress ipAdress) {
        this.ipAdress = ipAdress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Server{" + "name=" + name + ", oscAdress=" + oscAdress +
               ", ipAdress=" + ipAdress + ", port=" + port + '}';
    }

}
