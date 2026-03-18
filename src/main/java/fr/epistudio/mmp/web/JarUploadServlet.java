package fr.epistudio.mmp.web;

import com.google.gson.Gson;
import fr.epistudio.mmp.MinecraftMultiPlugin;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@MultipartConfig
public final class JarUploadServlet extends HttpServlet {

    private static final Path UPLOAD_DIR = Path.of("plugins/mmplugins/uploaded-jars");
    private static final Path FILES_MAP_PATH = UPLOAD_DIR.resolve("files-map.json");
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Files.createDirectories(UPLOAD_DIR);

        Map<String, FileData> files = MinecraftMultiPlugin.getInstance().getUploadedFiles();

        // api/upload get jar files from multipart form data, save them in uploaded-jars directory, add in files map, return json with success and filename

        // apo/upload?prevFile=plugin.jar get key in files map, set value in fileData, return json with success and filename

        Part filePart = req.getPart("file");
        if (filePart == null) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "{\"success\":false,\"message\":\"No file field named 'file' was provided.\"}");
            return;
        }

        String submittedName = filePart.getSubmittedFileName();
        if (submittedName == null || submittedName.isBlank()) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "{\"success\":false,\"message\":\"No file selected.\"}");
            return;
        }

        String safeName = Path.of(submittedName).getFileName().toString();
        if (!safeName.toLowerCase().endsWith(".jar")) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "{\"success\":false,\"message\":\"Only .jar files are allowed.\"}");
            return;
        }

        Path target = UPLOAD_DIR.resolve(safeName);

        try (InputStream in = filePart.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String newFileName = target.getFileName().toString();

        String previousFile = req.getParameter("prevFile");
        if (previousFile != null && !previousFile.isBlank() && files.containsKey(previousFile)) {
            files.get(previousFile).setNewFileName(newFileName);
        } else {
            FileData fileData = new FileData(newFileName);
            fileData.setNewFile(true);
            files.put(newFileName, fileData);
        }

        //save files map to disk

        if (!FILES_MAP_PATH.toFile().exists()) {
            //create empty file
            Files.createDirectories(FILES_MAP_PATH.getParent());
            Files.writeString(FILES_MAP_PATH, gson.toJson(files));
        } else {
            //update file
            Files.writeString(FILES_MAP_PATH, gson.toJson(files));
        }


        sendJson(resp, HttpServletResponse.SC_OK,
                "{\"success\":true,\"message\":\"Upload completed.\",\"file\":\"" + escapeJson(safeName) + "\"}");
    }

    private static void sendJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(json);
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}