package fr.epistudio.mmp.upload;

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

@MultipartConfig
public final class JarUploadServlet extends HttpServlet {

    private static final Path UPLOAD_DIR = Path.of("uploaded-jars");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Files.createDirectories(UPLOAD_DIR);

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