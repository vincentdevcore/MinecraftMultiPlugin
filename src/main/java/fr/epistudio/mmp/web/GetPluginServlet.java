package fr.epistudio.mmp.web;

import com.google.gson.Gson;
import fr.epistudio.mmp.MinecraftMultiPlugin;
import fr.epistudio.mmp.plugin.PluginDescription;
import fr.epistudio.mmp.plugin.PluginManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetPluginServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // api/plugins send all name + description of plugins in json format

        List<PluginInfo> plugins = new ArrayList<>();
        PluginManager manager = MinecraftMultiPlugin.getInstance().getManager();
        for (PluginDescription description : manager.getAllDescriptions()) {
            plugins.add(new PluginInfo(description.name, description.description, description.fileName));
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(gson.toJson(plugins));
    }

    private record PluginInfo(String name, String description, String fileName) {

    }
}
