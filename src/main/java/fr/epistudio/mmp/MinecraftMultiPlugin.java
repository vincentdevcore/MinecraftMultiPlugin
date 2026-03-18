package fr.epistudio.mmp;

import fr.epistudio.mmp.commands.PluginCommands;
import fr.epistudio.mmp.plugin.PluginLoader;
import fr.epistudio.mmp.plugin.PluginManager;
import fr.epistudio.mmp.web.FileData;
import fr.epistudio.mmp.web.JettyUploadServer;
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.register.RegisterType;
import fr.olympus.hephaestus.resources.HephaestusData;
import fr.olympus.prometheus.Prometheus;
import fr.olympus.prometheus.resources.PrometheusData;
import fr.olymus.heracles.Heracles;
import fr.olymus.heracles.resources.HeraclesData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class MinecraftMultiPlugin extends JavaPlugin implements MinecraftMultiPluginApi {


    private static MinecraftMultiPlugin instance;

    private PluginManager manager;

    private Hephaestus hephaestus;
    private Prometheus prometheus;
    private Heracles heracles;

    private JettyUploadServer jettyUploadServer;
    private Map<String, FileData> uploadedFiles = new java.util.concurrent.ConcurrentHashMap<>();

    public MinecraftMultiPlugin() throws Exception {

    }

    public PluginManager getManager() {
        return manager;

    }

    public Hephaestus getHephaestus() {
        return hephaestus;
    }

    public Prometheus getPrometheus() {
        return prometheus;
    }

    public Heracles getHeracles() {
        return heracles;
    }

    @Override
    public void onLoad() {
        instance = this;

        hephaestus = Hephaestus.init();
        prometheus = Prometheus.init();
        heracles = Heracles.init();

        manager = new PluginManager();

        PluginLoader loader = new PluginLoader(manager);
        try {
            loader.updateAll();
            loader.loadAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        uploadedFiles = manager.getAllFileData();

        manager.load();

    }

    @Override
    public void onEnable() {


        Bukkit.getPluginCommand("mmp").setExecutor(new PluginCommands());

        manager.enable();

        try {
            jettyUploadServer = new JettyUploadServer(8080);
            jettyUploadServer.start();
            getLogger().info("Jetty upload server started on port 8080.");
        } catch (Exception e) {
            getLogger().severe("Failed to start Jetty upload server.");
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {

        if (jettyUploadServer != null) {
            try {
                jettyUploadServer.stop();
                getLogger().info("Jetty upload server stopped.");
            } catch (Exception e) {
                getLogger().severe("Failed to stop Jetty upload server.");
                e.printStackTrace();
            }
        }

        manager.shutdown();
    }

    public static MinecraftMultiPlugin getInstance() {
        return instance;
    }

    @Override
    public HephaestusData getHephaestusData() {
        return Hephaestus.getData();
    }

    @Override
    public PrometheusData getPrometheusData() {
        return Prometheus.getData();
    }

    @Override
    public HeraclesData getHeraclesData() {
        return Heracles.getData();
    }

    @Override
    public void HephaestusAutoRegister(RegisterType type, String... args) {
        Hephaestus.autoRegister(type, args);
    }

    @Override
    public void PrometheusAutoRegister(fr.olympus.prometheus.register.RegisterType type, String... args) {
        Prometheus.autoRegister(type, args);
    }

    @Override
    public void HeraclesAutoRegister(fr.olymus.heracles.register.RegisterType type, String... args) {
        Heracles.autoRegister(type, args);
    }

    public Map<String, FileData> getUploadedFiles() {
        return uploadedFiles;
    }
}