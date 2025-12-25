package de.lldgames.ghww.wsServer;

import de.lldgames.ghww.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import java.io.IOException;

@WebSocket
public class ListenWS {
    private boolean greeted = false;
    private Subscription subObj;

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        this.sendGreetings(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws IOException {
        JSONObject m = new JSONObject(msg);
        String type = m.getString("t");
        if(type.equals("GREETINGS")){
            receiveGreetings(m, session);
            return;
        }
        if(type.equals("SUBSCRIBE")){
            receiveSubscribe(m, session);
            return;
        }
    }

    @OnWebSocketClose
    public void onClose(Session session){
        if(subObj !=null) subObj.cancel();
    }

    private void sendGreetings(Session session) throws IOException {
        JSONObject payload = createPayload("GREETINGS");
        session.getRemote().sendString(payload.toString());
    }

    private void receiveSubscribe(JSONObject data, Session session) throws IOException {
        if(!greeted) {
            session.getRemote().sendString(String.valueOf(createPayload("USER_ERROR").put("why", "not greeted. Introduce yourself, rude boy.")));
            return;
        }
        String repo = data.getString("repo");
        if(Authentication.hasAccess(this.subObj.getToken(), repo)){
            this.subObj.subTo(repo);
        }
        session.getRemote().sendString(createPayload("SUBSCRIBE_ACK").toString());
    }

    private void receiveGreetings(JSONObject data, Session session) throws IOException {
        if(greeted) {
            session.getRemote().sendString(
                    createPayload("USER_ERROR").put("why", "already greeted!").toString());
            return;
        }
        greeted = true;
        String token = data.getString("token");
        if(!Authentication.isGoodToken(token)){
            System.out.println("false tokem");
            return;
        }
        //proceed
        this.subObj = new Subscription(session, token);
        session.getRemote().sendString(createPayload("GREETINGS_ACK").toString());
    }

    private JSONObject createPayload(String type){
        return new JSONObject().put("t", type);
    }
}
