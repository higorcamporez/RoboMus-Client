/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

/**
 *
 * @author Higor
 */
public class Instrument implements Serializable{
    
    protected String name; // nome do instrumento   
    protected int polyphony; // quantidade de notas
    protected String OscAddress; //endereço do OSC do instrumento
    protected String typeFamily; //tipo do instrumento
    protected String specificProtocol; //procolo especifico do robo

    
    public Instrument(){
    }

    public Instrument(String name, int polyphony, String OscAddress, String typeFamily, String specificProtocol) {
        this.name = name;
        this.polyphony = polyphony;
        this.OscAddress = OscAddress;
        this.typeFamily = typeFamily;
        this.specificProtocol = specificProtocol;
    }
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPolyphony() {
        return polyphony;
    }

    public void setPolyphony(int polyphony) {
        this.polyphony = polyphony;
    }

    public String getOscAddress() {
        return OscAddress;
    }

    public void setOscAddress(String OscAddress) {
        this.OscAddress = OscAddress;
    }

    public String getTypeFamily() {
        return typeFamily;
    }

    public void setTypeFamily(String typeFamily) {
        this.typeFamily = typeFamily;
    }

    public String getSpecificProtocol() {
        return specificProtocol;
    }

    public void setSpecificProtocol(String specificProtocol) {
        this.specificProtocol = specificProtocol;
    }

    @Override
    public String toString() {
        return "Instrument{" + "name=" + name + ", polyphony=" + polyphony + ", OscAddress=" + OscAddress + ", typeFamily=" + typeFamily + ", specificProtocol=" + specificProtocol + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instrument other = (Instrument) obj;
        if (!Objects.equals(this.OscAddress, other.OscAddress)) {
            return false;
        }
        return true;
    }

    

    
    
    
    
    
    
}
