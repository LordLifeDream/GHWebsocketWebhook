package de.lldgames.ghww;

import de.lldgames.ghww.wsServer.ListenWS;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

import java.time.Duration;

public class GHWWServer {
    private static Server server;

    public static void main(String[] args) throws Exception {
        Authentication.loadFromFile();
        GHSecret.load();
        createServer();
        startServer();
    }


    public static void createServer(){
        server = new Server(7715);
        ServletContextHandler h = new ServletContextHandler();
        h.setContextPath("/");

        h.addServlet(HelloServlet.class, "/hello");
        h.addServlet(GithubEndpointServlet.class, "/githubEndpoint");

        JettyWebSocketServletContainerInitializer.configure(
                h,
                (ctx, container)->{
                    container.setIdleTimeout(Duration.ZERO);
                    container.addMapping("/listen", (a, b)->new ListenWS());
                }
        );
        server.setHandler(h);

    }

    public static void startServer () throws Exception {
        server.start();
        server.join();
    }

}
