package es.footleague.app.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;

@Service
public class FileStorageService {

    private final Path rootLocation;
    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp");

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) throws IOException {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.rootLocation);
    }

    public String storeFile(MultipartFile file, String subFolder, String originalFilename) throws IOException {
        String filename = StringUtils.cleanPath(originalFilename);
        if (filename.contains("..")) {
            throw new IOException("Invalid file name: " + filename);
        }

        Path folder = rootLocation.resolve(subFolder).normalize();
        if (!folder.toAbsolutePath().startsWith(rootLocation.toAbsolutePath())) {
            throw new IOException("Access denied: invalid subfolder");
        }
        Files.createDirectories(folder);

        Path targetLocation = folder.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return rootLocation.relativize(targetLocation).toString().replace('\\', '/');
    }

    public Resource loadFileAsResource(String relativePath) throws IOException {
        Path filePath = rootLocation.resolve(relativePath).normalize();
        if (!filePath.toRealPath().startsWith(rootLocation.toRealPath())) {
            throw new IOException("Access denied: path traversal detected");
        }
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new IOException("File not found: " + relativePath);
        }

        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException ex) {
            throw new IOException("Unable to read file: " + relativePath, ex);
        }
    }

    public void cleanUploadsFolder() throws IOException {
        if (Files.exists(rootLocation)) {
            Files.walk(rootLocation)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            Files.createDirectories(rootLocation);
            log.info("Uploads folder cleaned successfully");
        }
    }

    private boolean isValidImageMagicNumber(byte[] fileContent) {
        if (fileContent == null || fileContent.length < 4) {
            return false;
        }

        // JPEG: FF D8 FF
        if (fileContent.length >= 3
                && fileContent[0] == (byte) 0xFF
                && fileContent[1] == (byte) 0xD8
                && fileContent[2] == (byte) 0xFF) {
            return true;
        }

        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if (fileContent.length >= 8
                && fileContent[0] == (byte) 0x89
                && fileContent[1] == (byte) 0x50
                && fileContent[2] == (byte) 0x4E
                && fileContent[3] == (byte) 0x47) {
            return true;
        }

        // GIF: 47 49 46 (GIF8 o GIF9)
        if (fileContent.length >= 3
                && fileContent[0] == (byte) 0x47
                && fileContent[1] == (byte) 0x49
                && fileContent[2] == (byte) 0x46) {
            return true;
        }

        // WebP: RIFF ... WEBP
        if (fileContent.length >= 12
                && fileContent[0] == (byte) 0x52 // 'R'
                && fileContent[1] == (byte) 0x49 // 'I'
                && fileContent[2] == (byte) 0x46 // 'F'
                && fileContent[3] == (byte) 0x46 // 'F'
                && fileContent[8] == (byte) 0x57 // 'W'
                && fileContent[9] == (byte) 0x45 // 'E'
                && fileContent[10] == (byte) 0x50 // 'P'
                && fileContent[11] == (byte) 0x50) { // 'P'
            return true;
        }

        return false;
    }

    public void validateImageFile(MultipartFile file) throws IOException {
        // 1. PRIMERO: Verificar si está vacío
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty");
        }

        // 2. Validar tamaño ANTES de leer todo el contenido
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File too large. Maximum size is 5MB");
        }

        // 3. Validar MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IOException("Invalid file type: " + contentType);
        }

        // 4. Validar extensión
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("Filename is missing");
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(ext)) {
            throw new IOException("Invalid file extension: " + ext);
        }

        // 5. ÚLTIMO: Validar contenido (magic numbers) - DESPUÉS de todo lo anterior
        byte[] fileContent = file.getBytes();
        if (!isValidImageMagicNumber(fileContent)) {
            throw new IOException("File content doesn't match image format");
        }
    }
}
