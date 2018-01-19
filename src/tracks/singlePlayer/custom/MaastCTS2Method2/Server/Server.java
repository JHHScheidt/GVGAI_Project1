package tracks.singlePlayer.custom.MaastCTS2Method2.Server;

import core.game.StateObservation;
import group.datagather.constants.Constants;
import group.datagather.constants.JSONDeicticViewParser;
import ontology.Types;
import org.json.simple.JSONObject;
import tracks.singlePlayer.custom.MaastCTS2Method2.model.MctNode;
import tracks.singlePlayer.custom.Utils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ServerSocket serverSocket;
    private Socket client;
    PrintWriter out;

    private final int port;
    private boolean running;

    public Server(int port) throws IOException {
        this.port = port;
        this.init();
        System.out.println("Server started");
    }

    private void init() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        this.running = true;
        //Runtime r = Runtime.getRuntime();
        //Process p = r.exec("python C:\\Users\\Timo Raff\\Documents\\GitHub\\GVGAI_Project1\\clients\\GVGAI-JavaClient\\src\\group\\learning\\client.py");
        System.out.println("wait for client");
        waitForClient();
    }

    public String receive() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        return in.readLine();
    }

    public void send(String message) throws IOException {
        out.println(message);
    }

    //get the most promising actino through the NN
    public Types.ACTIONS getActionFromNN(MctNode node, ArrayList<Integer> possibleActions) {
        System.out.println("getActino");
        StateObservation so = node.getStateObs();//preprocessdata and sen
        JSONObject object = new JSONObject();
        JSONObject state = Utils.stateObservationToJSON(so);
        object.put("state", JSONDeicticViewParser.parseState(state));
        object.put("possibleActions", possibleActions);

        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;
        try {
            send("E" + object.toJSONString());
            int ret = Integer.parseInt(receive());
            switch (ret) {
                case 0:
                    action = Types.ACTIONS.ACTION_UP;
                    break;
                case 1:
                    action = Types.ACTIONS.ACTION_DOWN;
                    break;
                case 2:
                    action = Types.ACTIONS.ACTION_LEFT;
                    break;
                case 3:
                    action = Types.ACTIONS.ACTION_RIGHT;
                    break;
                case 4:
                    action = Types.ACTIONS.ACTION_USE;
                    break;
                default:
                    break;
            }

        } catch (IOException e) {
            System.out.print("sending/receiving failed");
        }
        return action;

    }

    //one training iteration of the NN for this pair of state and action
    public void trainOn(StateObservation so, Types.ACTIONS action) {
        System.out.println("train model");
        JSONObject object = new JSONObject();
        JSONObject state = Utils.stateObservationToJSON(so);
        object.put("state", state);
        object.put("action", action.toString());
        //now run preprocessing on the object
        JSONObject subResult = new JSONObject();

        double actionPerformed;
        switch ((String) object.get("action")) {
            case "ACTION_UP":
                actionPerformed = Constants.UP;
                break;
            case "ACTION_DOWN":
                actionPerformed = Constants.DOWN;
                break;
            case "ACTION_LEFT":
                actionPerformed = Constants.LEFT;
                break;
            case "ACTION_RIGHT":
                actionPerformed = Constants.RIGHT;
                break;
            case "ACTION_USE":
                actionPerformed = Constants.USE;
                break;
            default:
                actionPerformed= 0;
        }

        subResult.put("state", JSONDeicticViewParser.parseState((JSONObject) object.get("state")));
        subResult.put("action", actionPerformed);

        //send data as string
        try {
            //long time = System.nanoTime();
            send("T" + subResult.toJSONString());
            receive();//TODO dont receive here, it will block maybe receive before the next send. so the .py is done with working
            //String test= receive();
            //time= System.nanoTime()-time;
            //System.out.println(time+" nano sec. :"+ test);

        } catch (IOException e) {
            System.out.println("sending failed");
        }
    }

    public void waitForClient() throws IOException {
        client = this.serverSocket.accept();
        out = new PrintWriter(client.getOutputStream(), true);
    }


    public void stop() {
        this.running = false;

        try {
            this.serverSocket.close();
        } catch (IOException e) {

        }
    }

    public boolean isRunning() {
        return this.running;
    }
}
