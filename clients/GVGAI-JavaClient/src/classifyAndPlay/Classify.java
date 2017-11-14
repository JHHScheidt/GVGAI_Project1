/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classifyAndPlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import serialization.Observation;
import serialization.SerializableStateObservation;
import serialization.Types;

/**
 *
 * @author Marciano
 */
public class Classify {
    
    boolean classified;
    int category; //Integer consisting of which category, category labeleld from 0 to 7.
    //Shooter = 0, Puzzle = 1, Platformer = 2, Strategy = 3 
    boolean[] possCat; //Everything is possible, so if a game is excluded make false
    ArrayList prevAction = new ArrayList<Types.ACTIONS>(); //PreviousAction
    SerializableStateObservation currentState;
    ArrayList availableActions = new ArrayList<Types.ACTIONS>();
    //Variables required to identify gametype
    double speed; //Speed calculated in blocksize per game tick
    ArrayList enemies; //This can be shootable enemies or avoidable enemies
    ArrayList friendlies; //Friendlies that have to be protected or collected
    Observation[][] prevNPC; //PreviousState of the NPC's
    
    public Classify(){
        this.classified = false;
        this.possCat = new boolean[4];
        Arrays.fill(this.possCat, true);
        this.enemies = new ArrayList<>();
        this.friendlies = new ArrayList<>();
    }
    
    public Types.ACTIONS tryAction(){
        //This should try certain actions that might match a genre, result of these
        //actions should exclude or infer that the game is played or not.
        //The order of these actions will always be checked in the same order as put forward in the category Integer

        //It should try to hit an enemy if possible.
        if(this.possCat[0]){
            Observation[][][] all = this.currentState.getObservationGrid();
            System.out.println(all[0][0][0].itype);
//            Observation[][] NPC = this.currentState.getNPCPositions();
//            System.out.println(NPC.length);
//            System.out.println(NPC[0].length);
//            System.out.println(NPC[0][0].itype);
        
        } else if(this.possCat[1]){
            
        } else if(this.possCat[2]){
            
        } else if(this.possCat[3]){
            
        } else {
            //Cannot be qualified
        }
        return null;
    }
    
    public boolean isClassified() {
        return classified;
    }

    public void setClassified(boolean classified) {
        this.classified = classified;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean[] getPossCat() {
        return possCat;
    }

    public void setPossCat(boolean[] possCat) {
        this.possCat = possCat;
    }

    public ArrayList<Types.ACTIONS> getPrevAction() {
        return prevAction;
    }

    public SerializableStateObservation getCurrentState() {
        return currentState;
    }

    public void setCurrentState(SerializableStateObservation currentState) {
        this.currentState = currentState;
    }
    
    
    
}
