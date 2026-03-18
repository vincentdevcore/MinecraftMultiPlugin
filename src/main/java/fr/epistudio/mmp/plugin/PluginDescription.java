package fr.epistudio.mmp.plugin;


import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class PluginDescription {
    public final String name;
    public final String main;
    public final String version;
    public final String apiVersion;
    public final List<String> depends;
    public final String description;
    public final List<String> authors;
    public final String fileName; // Set by PluginManager when loaded, not from YAML

    private static final Pattern DEP_PATTERN =
            Pattern.compile("^\\s*([\\w-]+)(?:\\s*>=\\s*([\\d.]+))?\\s*$");

    @SuppressWarnings("unchecked")
    public PluginDescription(InputStream yamlStream, String fileName) {
        this.fileName = fileName;
        Map<String, Object> map = new Yaml().load(yamlStream);
        name       = (String) map.get("name");
        main = (String) map.get("main");
        version    =  map.getOrDefault("version", "0.0.0") instanceof Double ? String.valueOf(map.get("version")) : (String) map.getOrDefault("version", "0.0.0");
        apiVersion = map.getOrDefault("api-version", PluginLoader.API_VERSION) instanceof Double ? String.valueOf(map.get("api-version")) : (String) map.getOrDefault("api-version", PluginLoader.API_VERSION);
        depends    = (List<String>) map.getOrDefault("depends", List.of());
        description = (String) map.getOrDefault("description", "Aucune description fournie");
        authors     = (List<String>) map.getOrDefault("authors", List.of());
    }

    /** Extrait simplement le nom d’un élément depends ("logger >=1.1" -> "logger"). */
    public static String depName(String raw) {
        var m = DEP_PATTERN.matcher(raw);
        return m.matches() ? m.group(1) : raw.trim();
    }
}
