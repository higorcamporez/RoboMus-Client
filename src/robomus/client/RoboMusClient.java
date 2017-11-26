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
import java.util.Random;
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
        System.out.println("RoboMusClient>handshake");
        sendHandshake();
        System.out.println("RoboMusClient>get instr");
        sendGetInstruments();
        getCommands();
        
    }
    public void printCommands(){
        System.out.println("\n___________________COMMANDS___________________");
        System.out.println("- action[act]");
        System.out.println("\t - (instrument name)");            
        System.out.println("- get[g]");
        System.out.println("\t - instruments[instr]");
        System.out.println("- show[sh]");
        System.out.println("\t - actions[act] | instruments[instr] | server[server]");
        System.out.println("- use[u]");
        System.out.println("\t - (instrument name)");
        System.out.println("- handshake[hand]");
        System.out.println("\t -");
        System.out.println("- disconnect[disc]");
        System.out.println("\t -");
        System.out.println("- ..");
        System.out.println("\t -");
        System.out.println("______________________________________________");

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
                        String[] aux = selectedInstrument.getSpecificProtocol().split(">");

                        for (String string : aux) {
                            System.out.println(string+">");
                        }
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
        
        Random random = new Random();
        actParameters.add(0, random.nextInt(1000)); //message id
        
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
                    case "g":
                    case "get":
                        commandGet(arrayIn);
                        break;
                    case "hand":
                    case "hs":
                    case "handshake":
                        sendHandshake();
                        break;
                    case "disc":
                    case "disconnect":
                        disconnect();
                        break;
                    case "sh":
                    case "show":
                        commandShow(arrayIn);
                        break;
                    case "u":
                    case "use":
                        commandUse(arrayIn);
                        break;
                    case "bday":
                        playHappyBirthday();
                        break;
                    case "..":
                        selectedInstrument = null;
                        break;
                    case "h":
                    case "help":    
                        printCommands();
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
        
         String[] ip = {"255","255","255","255"};
        try {
            ip = InetAddress.getLocalHost().getHostAddress().split("\\.");
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            s = new OSCPortOut(InetAddress.getByName(ip[0]+"."+ip[1]+"."+ip[2]+".255"), 1234);
            s.send(msg);
            System.out.println("handshake sent. Wait max 5 sec");
            Long ti = System.currentTimeMillis();
            Boolean flag = false;
            float count = 1;
            while( (System.currentTimeMillis() - ti ) < 5001 ){
               if(this.server != null && this.server.getIpAdress() != null){
                   flag = true;
                   System.out.println("\nreceive handshake | "+server.toString());
                   try {
                       Thread.sleep(500);
                   } catch (InterruptedException ex) {
                       Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
                   }
                   break;
               }else{
                   if( (float)((System.currentTimeMillis() - ti )/1000) == count){
                       System.out.println(count);
                       count++;
                   }
               }
            }
            if(flag == false){
                System.out.println("Server no connected");
            }
            
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
        System.out.print("\n received "+instrument.getName());
        
        
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
    public void playHappyBirthday(){
        Instrument laplap, laplap2;
        int index;
        index = this.instruments.indexOf(new Instrument("/laplap"));
        if(index != -1){
            laplap = this.instruments.get(index);
        }else{
            System.out.println("There is no laplap");
            return;
        }
           
        index = this.instruments.indexOf(new Instrument("/laplap2"));
        if(index != -1){
            laplap2 = this.instruments.get(index);
        }else{
            System.out.println("There is no laplap2");
            return;
        }
        
        //E|----0-0-2-0-5-4--0-0-2-0-7-5-5--9-9-12-9-5-4-2--10-10-9-5-7-5-5--------|
        //music notes E4-E4-F#4-E4-A4-G#4--
        //            E4-E4-F#4-E4-B4-A4-A4--
        //            C#5-C#5-E5-C#5-A4-G#4-F#4--
        //            D5-D5-C#5-A4-B4-A4-A4
        int time = 800;
        String[] notes = {  "E4","E4","F#4","E4","A4","G#4",
                            "E4","E4","F#4","E4","B4","A4","A4"};
        List l1 = new ArrayList();
        l1.add("E4");
        l1.add("E4");
        l1.add("F#4");
        l1.add("E4");
        int[] duration = {  time,time,time,time,time,time,
                            time,time,time,time,time,time,time};
        List l = new ArrayList();

        for (int i = 0; i < notes.length; i++) {
            l.clear();
            l.add(l1);
            if( i%2 == 0){
                this.selectedInstrument = laplap;
            }else{
                this.selectedInstrument = laplap2;
            }
            sendAction("/playNote", l);
            try {
                Thread.sleep(duration[i]);
            } catch (InterruptedException ex) {
                Logger.getLogger(RoboMusClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Note: "+notes[i]);
        }
        System.out.println("");
    }
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        RoboMusClient roboMusClient = new RoboMusClient();
        
        
    }

 
}
