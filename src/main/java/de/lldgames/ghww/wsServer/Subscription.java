package de.lldgames.ghww.wsServer;

import de.lldgames.ghww.Authentication;
import de.lldgames.ghww.GHWW;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Subscription {
    private String token;
    private Session session;
    private ArrayList<String> subscriptions = new ArrayList<>();

    public Subscription(Session session, String token){
        this.session = session;
        this.token = token;
        GHWW.subs.add(this);
    }

    public void subTo(String repo){
        if(Authentication.hasAccess(this.token, repo)) {
            if(!this.subscriptions.contains(repo)) this.subscriptions.add(repo);
        }
    }

    public boolean subbedTo(String repo){
        return this.subscriptions.contains(repo);
    }

    public void cancel(){
        GHWW.subs.remove(this);
    }

    public void notify(String type, String repo, JSONObject data){
        if(!this.subbedTo(repo)) return;
        JSONObject payload = new JSONObject().put("t", "EVENT")
                .put("type", type)
                .put("repo", repo)
                .put("data", data);
        try{
            session.getRemote().sendString(payload.toString());
        } catch (IOException e) {

        }
    }

    public String getToken(){
        return this.token;
    }
}
