package de.lldgames.ghww;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

public class GithubEndpointServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getHeader("X-GitHub-Event");
        //System.out.println("got " + type);
        String bodyStr = new String(req.getInputStream().readAllBytes());
        JSONObject body = new JSONObject(bodyStr);

        //get repo
        JSONObject repo = body.getJSONObject("repository");
        String fullName = repo.getString("full_name");
        if(!GHSecret.validate(req.getHeader("X-Hub-Signature-256"), fullName, bodyStr)){
            System.err.println("could not validate authenticity!");
            return;
        }
        System.out.println("got " + type + " on " + fullName);
        GHWW.broadcastEvent(type, fullName, body);
    }
}
