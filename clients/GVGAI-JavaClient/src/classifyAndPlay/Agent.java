/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classifyAndPlay;

import java.util.Random;
import serialization.SerializableStateObservation;
import serialization.Types;
import utils.ElapsedCpuTimer;

/**
 *
 * @author Marciano
 */
public class Agent extends utils.AbstractPlayer {

    boolean firstTime = true;
    Classify c;
    
    public Agent(){
        
    }
    @Override
    public void init(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
        if(firstTime){
            firstTime = false;
            c = new Classify();
        }
    }

    @Override
    public Types.ACTIONS act(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
//        if(c.classified){
//            
//        } else {
            c.setCurrentState(sso);
            c.tryAction();
            return sso.getAvailableActions().get(0);
//        }
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int result(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
