package fr.epistudio.mmp.plugin;

import fr.epistudio.mmp.MinecraftMultiPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class PluginLoader {

    public static final String API_VERSION = "1.0";               // mise à jour à chaque breaking change
    private static final Path PLUGINS_DIR = Paths.get("plugins/mmplugins");

    private final PluginManager manager;

    public PluginLoader(PluginManager manager) { this.manager = manager; }

    /** Charge tous les plug-ins, gère dépendances et cycle de vie. */
    public void loadAll() throws Exception {
        File dir = PLUGINS_DIR.toFile();
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Répertoire de plug-ins créé : " + PLUGINS_DIR);
            } else {
                System.err.println("Erreur : impossible de créer le répertoire " + PLUGINS_DIR);
                return;
            }
        }
        // 1) Découverte brutale des JAR
        List<PluginContainer> discovered = new ArrayList<>();
        if (Files.isDirectory(PLUGINS_DIR)) {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(PLUGINS_DIR, "*.jar")) {
                for (Path jarPath : ds) {
                    PluginContainer pc = inspectJar(jarPath);
                    if (pc != null) discovered.add(pc);
                }
            }
        }

        // 2) Tri topologique par dépendances
        List<PluginContainer> sorted = DependencyResolver.sort(discovered);

        // 3) Cycle de vie
        for (PluginContainer pc : sorted) {
            System.out.println("Chargement du plug-in : " + pc.getDescription().name + " " + pc.getDescription().version);
            manager.register(pc);
        }
    }

    public static void load(PluginContainer pc) throws Exception {
        MMPlugin plugin =
                (MMPlugin) pc.getClassLoader().loadClass(pc.getDescription().main)
                        .getDeclaredConstructor().newInstance();
        pc.setInstance(plugin);
        plugin.attach(MinecraftMultiPlugin.getInstance());
        plugin.onLoad();
    }

    public static void enable(PluginContainer pc, PluginManager manager) throws Exception {
        pc.getInstance().onEnable();
        manager.register(pc.getInstance());
        System.out.println("✔ Plug-in activé : " + pc.getDescription().name + " " + pc.getDescription().version);
    }

    /** Inspecte un JAR, renvoie null si pas de plugin.yml ou api-version incompatible. */
    private PluginContainer inspectJar(Path jarPath) throws IOException {
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            var entry = jar.getJarEntry("model.yml");
            if (entry == null) {
                System.out.println("⚠ " + jarPath.getFileName() + " : pas de model.yml, ignoré");
                return null;
            }

            try (InputStream in = jar.getInputStream(entry)) {
                PluginDescription desc = new PluginDescription(in);

                // Vérif API
                if (!API_VERSION.equals(desc.apiVersion)) {
                    System.err.printf("⚠ %s : api-version \"%s\" incompatible (core=%s)%n",
                            jarPath.getFileName(), desc.apiVersion, API_VERSION);
                    return null;
                }

                // Chargeur isolé
                URL jarUrl = jarPath.toUri().toURL();
                PluginClassLoader cl = new PluginClassLoader(jarUrl, getClass().getClassLoader());

                return new PluginContainer(desc, cl);
            }
        }
    }
}
