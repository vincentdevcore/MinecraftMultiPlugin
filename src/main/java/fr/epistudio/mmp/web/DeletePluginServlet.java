package fr.epistudio.mmp.web;

import com.google.gson.Gson;
import fr.epistudio.mmp.MinecraftMultiPlugin;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class DeletePluginServlet extends HttpServlet {

    private static final Gson gson = new Gson();
    private static final Path UPLOAD_DIR = Path.of("plugins/mmplugins/uploaded-jars");
    private static final Path FILES_MAP_PATH = UPLOAD_DIR.resolve("files-map.json");
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, FileData> files = MinecraftMultiPlugin.getInstance().getUploadedFiles();

        String prevFile = req.getParameter("prevFile");
        if (prevFile == null || prevFile.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\":false,\"message\":\"No 'prevFile' parameter provided.\"}");
            return;
        }

        FileData fileData = files.get(prevFile);
        if (fileData == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"success\":false,\"message\":\"File not found in uploaded files.\"}");
            return;
        }

        fileData.setDelete(true);


        if (!FILES_MAP_PATH.toFile().exists()) {
            //create empty file
            Files.createDirectories(FILES_MAP_PATH.getParent());
            Files.writeString(FILES_MAP_PATH, gson.toJson(files));
        } else {
            //update file
            Files.writeString(FILES_MAP_PATH, gson.toJson(files));
        }


        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("{\"success\":true}");
    }
}
