package de.lldgames.ghww;

import de.lldgames.ghww.wsServer.Subscription;
import org.json.JSONObject;

import java.util.ArrayList;

public class GHWW {
    public static final ArrayList<Subscription> subs = new ArrayList<>();

    public static void main(String[] args) {

    }


    public static void broadcastEvent(String type, String repo, JSONObject data){
        for(Subscription sub: subs){
            if(sub.subbedTo(repo)){
                sub.notify(type, repo, data);
            }
        }
    }
}
