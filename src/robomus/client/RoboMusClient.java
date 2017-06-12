/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.client;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import robomus.instrument.Instrument;

/**
 *
 * @author Higor
 */
public class RoboMusClient {

    private List<Instrument> instruments;
    private Server server;
    private OSCPortOut sender;
    private OSCPortIn receiver;
    private String commandHeader;
    private Instrument selectedInstrument = null;
    private String oscAdress;
    
    public RoboMusClient() {
        this.instruments = new ArrayList<>();
        
        try {
            this.receiver = new OSCPortIn(12345);
        } catch (SocketException ex) {
            Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.receiveMessages();
        this.commandHeader = "RoboMusClient>";
        this.oscAdress = "/RoboMusClient";
        getCommands();
        
    }
    public void printInstruments(){
        if(instruments.isEmpty()){
            System.out.println("No instrument");
        }else{
            for (Instrument inst : instruments) {
                System.out.println(inst.toString());

            }
        }
    }
    
    public void printServer(){
        if(this.server != null){
           System.out.println(this.server.toString()); 
        }else{
           System.out.println("Server Not Found");
        }
        
    }
    
    public void printHeader(){
        if(selectedInstrument != null){
            System.out.print(commandHeader+selectedInstrument.getName()+">");
        }else{
            System.out.print(commandHeader);
        }
        
    }
    
    public void commandUse(String[] arrayIn){

        if (arrayIn.length >= 2) {
                boolean aux = false;
                for (Instrument inst : instruments) {
                    if(arrayIn[1].equals(inst.getName())){
                        this.selectedInstrument = inst;
                        aux = true;
                    }              
                }
                if(!aux){
                    System.out.println("Instrument Not Found");
                }
            
        }
    }
    
    public void commandShow(String[] arrayIn){

        if (arrayIn.length >= 2) {
            switch (arrayIn[1]) {
                case "actions":
                case "act":
                    if(selectedInstrument == null){
                        System.out.println("Select one instrument first!");
                    }else{
                        System.out.println(selectedInstrument.getSpecificProtocol());
                    }
                break;
                case "instr":
                case "instruments":
                    printInstruments();
                    break;
                case "serv":
                case "server":
                    printServer();
                    break;
                default:
                    System.out.println("command not found");
                    break;

            }
        }
    }
    public void commandGet(String[] arrayIn){

        if (arrayIn.length >= 2) {
            switch (arrayIn[1]) {
                case "instr":
                case "instruments":
                    sendGetInstruments();
                    break;
                default:
                    System.out.println("command not found");
                    break;

            }
        }
    }

    public List getActionParametersName(String actionName){
        String actions[] = selectedInstrument.getSpecificProtocol().split(">");
        List actionParametersName = new ArrayList<String>();

        for (String action : actions){         
            String sepateAct[] = action.split(";");
            
            sepateAct[0] = sepateAct[0].substring(2, sepateAct[0].length());
            if(actionName.equals(sepateAct[0])){
                for (int i  = 1; i < sepateAct.length; i++) {
                    actionParametersName.add(sepateAct[i]);
                }
                return actionParametersName;
            }
        }
        return null;
    }
    public void sendAction(String oscAdress, List args){
        long l = 100;
        args.add(0, l);
        args.add(1, 123); //message id
        
        OSCMessage msg = new OSCMessage(this.server.getOscAdress()
                                        +selectedInstrument.getOscAddress()
                                        +oscAdress, 
                                        args);
        try {
            this.sender.send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void commandAction(String[] arrayIn){
        List actParamName = null;
        List actParameters = new ArrayList();
        
        if (arrayIn.length >= 2) {
            actParamName = getActionParametersName(arrayIn[1]);
            if(actParamName != null){
                for (Object actParameter : actParamName) {
                    System.out.print(actParameter.toString()+": ");
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    String keyboardIn = null;
                    try {
                        keyboardIn = in.readLine();
                        actParameters.add(keyboardIn);
                    } catch (IOException ex) {
                        Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                 }
                 sendAction("/"+arrayIn[1],actParameters);
                    
               
            }else{
                System.out.println("Action Not Found");
            }
        }
    }
    public void getCommands(){
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String keyboardIn = null;
        while(true){
            printHeader();
            try {
                keyboardIn = in.readLine();
            } catch (IOException ex) {
                Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            String[] arrayIn = keyboardIn.split(" ");
            if (arrayIn.length >= 1) {
                switch (arrayIn[0]) {
                    case "act":
                    case "action":
                        if(selectedInstrument == null){
                            System.out.println("Select one instrument first!");
                        }else{
                            commandAction(arrayIn);
                        }
                        
                        break;
                    case "get":
                        commandGet(arrayIn);
                        break;
                    case "hand":
                    case "handshake":
                        sendHandshake();
                        break;
                    case "disc":
                    case "disconnected":
                        disconnect();
                        break;
                    case "sh":
                    case "show":
                        commandShow(arrayIn);
                        break;
                    case "use":
                        commandUse(arrayIn);
                        break;
                    
                    case "..":
                        selectedInstrument = null;
                        break;
                    case "":

                        break;
                    default:
                        System.out.println("command not found");
                        break;

                }
            }
        }
        
    }
    
    public void sendGetInstruments(){
        if(this.server == null){
            System.out.println("No server connected");
        }else{     
            OSCMessage msg = new OSCMessage(this.server.getOscAdress()+"/getInstruments");
            msg.addArgument("/RoboMusClient");
            try {
                this.sender.send(msg);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            
    }
    
    public void sendHandshake() {
              
        OSCMessage msg = new OSCMessage("/handshake/client");
        msg.addArgument("RoboMusClient");
        msg.addArgument(this.oscAdress);
        try {
            msg.addArgument(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        msg.addArgument(12345);
        OSCPortOut s;
        try {
            s = new OSCPortOut(InetAddress.getByName("192.168.0.255"), 1234);
            s.send(msg);
            System.out.println("handshake sent");
        } catch (SocketException ex) {
            Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
  
            
    }
    
    public void receiveHandshake(OSCMessage msg) {
        List arguments = msg.getArguments();
                
        this.server = new Server();
        server.setName(arguments.get(0).toString());
        server.setOscAdress(arguments.get(1).toString());
        try {
            server.setIpAdress(InetAddress.getByName((String)arguments.get(2)));
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        server.setPort(Integer.parseInt(arguments.get(3).toString()));
        
        try {
            this.sender = new OSCPortOut(this.server.getIpAdress(), this.server.getPort());
        } catch (SocketException ex) {
            Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void receiveGetInstruments(OSCMessage msg){
        
        if(msg.isFirstMessage){
            instruments.clear();
        }     
        Instrument instrument = new Instrument();
        List arguments = msg.getArguments();

        instrument.setName((String)arguments.get(0));
        instrument.setPolyphony((int)arguments.get(1));
        instrument.setTypeFamily((String)arguments.get(2));
        instrument.setSpecificProtocol((String)arguments.get(3));
        instrument.setOscAddress((String)arguments.get(4));
        if(! this.instruments.contains(instrument) ){
           this.instruments.add(instrument);
        }
        System.out.println("received "+instrument.getName());
        
        
    }
    public void disconnect() {
        
        OSCMessage msg = new OSCMessage(this.server.getOscAdress()
                                        +"/disconnect/client"
                                        );
        msg.addArgument(this.oscAdress);
        try {
            this.sender.send(msg);
        
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.server = null;
    }
    
    public void disconnectInstrument(OSCMessage oscMessage){
        Instrument instrument = new Instrument();
        instrument.setOscAddress(oscMessage.getArguments().get(0).toString());
        if ( this.instruments.remove(instrument) ){
            System.out.println("Instrument '"+oscMessage.getArguments().get(0).toString()+"' disconnected");
        }
    }
    
    public String[] divideAddress(String address){
        String aux = address;
        if (aux.startsWith("/")) {
            aux = address.substring(1);
        }

        String[] split = aux.split("/", -1);
        
        return split;
        
    }
    public void receiveMessages(){
        
            OSCListener listener = new OSCListener() {
                @Override
                public void acceptMessage(java.util.Date time, OSCMessage message) {
                    String[] dividedAdress = divideAddress(message.getAddress());
                    if (dividedAdress.length >= 2) {
                        switch (dividedAdress[1]) {
                            case "instrument":
                                receiveGetInstruments(message);
                                break;
                            case "disconnect":
                                disconnectInstrument(message);
                                break;
                            case "handshake":
                                receiveHandshake(message);
                                System.out.println("\nreceive handshake | "+server.toString());
                                System.out.print(commandHeader);
                                break;
                            default:
                                System.out.println("recebeu msg default");
                                break;

                        }
                     
                        
                    }
                }

              
            };
            receiver.addListener("/RoboMusClient"+"/*", listener);
            receiver.addListener("/RoboMusClient"+"/*/*", listener);
            
            receiver.startListening();

    }
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        RoboMusClient roboMusClient = new RoboMusClient();
        roboMusClient.sendGetInstruments();
        
    }

 
}
