package fr.epistudio.mmp.plugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.epistudio.mmp.MinecraftMultiPlugin;
import fr.epistudio.mmp.web.FileData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

public class PluginLoader {

    public static final String API_VERSION = "1.0";               // mise à jour à chaque breaking change
    private static final Path PLUGINS_DIR = Paths.get("plugins/mmplugins");
    private static final Path UPLOAD_DIR = PLUGINS_DIR.resolve("uploaded-jars");
    private static final Path FILES_MAP_PATH = UPLOAD_DIR.resolve("files-map.json");
    private static final Gson GSON = new Gson();

    private final PluginManager manager;


    public PluginLoader(PluginManager manager) { this.manager = manager; }
    //update plugins files
    public void updateAll() throws Exception {
        Files.createDirectories(UPLOAD_DIR);
        Files.createDirectories(PLUGINS_DIR);

        if (!Files.exists(FILES_MAP_PATH)) {
            return;
        }

        String json = Files.readString(FILES_MAP_PATH);
        Map<String, FileData> files = GSON.fromJson(json, new TypeToken<Map<String, FileData>>() {}.getType());

        if (files == null || files.isEmpty()) {
            return;
        }

        for (Map.Entry<String, FileData> entry : files.entrySet()) {
            String key = entry.getKey();
            FileData data = entry.getValue();

            if (data == null) {
                continue;
            }

            if (data.isDelete()) {
                Path targetPath = PLUGINS_DIR.resolve(key);
                try {
                    Files.deleteIfExists(targetPath);
                    System.out.println("Fichier supprimé : " + targetPath);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la suppression de " + targetPath + " : " + e.getMessage());
                }
                continue;
            }

            String newFileName = data.getNewFileName();

            if (data.isNewFile()) {
                if (newFileName != null && !newFileName.isBlank()) {
                    Path sourcePath = UPLOAD_DIR.resolve(newFileName);
                    Path targetPath = PLUGINS_DIR.resolve(newFileName);

                    try {
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Fichier ajouté : " + targetPath);
                    } catch (IOException e) {
                        System.err.println("Erreur lors de l'ajout de " + targetPath + " : " + e.getMessage());
                    }
                }
                continue;
            }

            if (newFileName != null && !newFileName.isBlank()) {
                Path sourcePath = UPLOAD_DIR.resolve(newFileName);
                Path targetPath = PLUGINS_DIR.resolve(key);

                try {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Fichier mis à jour : " + targetPath);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la mise à jour de " + targetPath + " : " + e.getMessage());
                }
            }
        }
        //clear updateFolder
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(UPLOAD_DIR)) {
            for (Path jarPath : ds) {
                try {
                    Files.deleteIfExists(jarPath);
                    System.out.println("Fichier temporaire supprimé : " + jarPath);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la suppression de " + jarPath + " : " + e.getMessage());
                }
            }
        }

    }

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
                PluginDescription desc = new PluginDescription(in, jarPath.getFileName().toString());

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
