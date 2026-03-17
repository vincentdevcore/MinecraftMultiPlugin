package fr.epistudio.mmp.upload;

import jakarta.servlet.MultipartConfigElement;
import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

import java.net.URL;

public final class JettyUploadServer {

    private final Server server;

    public JettyUploadServer(int port) {
        this.server = new Server(port);
    }

    public void start() throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        URL webUrl = JettyUploadServer.class.getClassLoader().getResource("web");
        if (webUrl == null) {
            throw new IllegalStateException("Resource folder 'web' not found in classpath.");
        }

        context.setBaseResourceAsString(webUrl.toExternalForm());

        ServletHolder defaultServlet = new ServletHolder("default", DefaultServlet.class);
        defaultServlet.setInitParameter("dirAllowed", "false");
        defaultServlet.setInitParameter("pathInfoOnly", "true");
        context.addServlet(defaultServlet, "/");

        ServletHolder uploadServlet = new ServletHolder(new JarUploadServlet());
        uploadServlet.getRegistration().setMultipartConfig(
                new MultipartConfigElement(
                        "uploads-temp",
                        1024L * 1024L * 200L,
                        1024L * 1024L * 250L,
                        1024 * 1024
                )
        );
        context.addServlet(uploadServlet, "/api/upload");

        server.setHandler(context);

        server.start();
    }

    public void stop() throws Exception {
        if (server.isStarted() || server.isStarting()) {
            server.stop();
        }
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public Server getServer() {
        return server;
    }
}