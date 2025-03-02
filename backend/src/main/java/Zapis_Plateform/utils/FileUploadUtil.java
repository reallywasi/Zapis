package Zapis_Plateform.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class FileUploadUtil {

    // Use dynamic upload directory from properties or default to "uploads/"
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public static String saveFile(MultipartFile file, String username) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Sanitize the file name to avoid special characters
        String originalFileName = file.getOriginalFilename();
        String sanitizedFileName = username + "_" + URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.toString());

        // Ensure the upload directory exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!uploadPath.toFile().exists()) {
            uploadPath.toFile().mkdirs();
        }

        // Save the file to the specified directory
        File destination = new File(uploadPath.toFile(), sanitizedFileName);
        file.transferTo(destination);

        return destination.getAbsolutePath();  // Return the absolute path for storage in the database
    }
}
