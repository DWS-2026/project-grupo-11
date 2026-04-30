package es.footleague.app.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path rootLocation;

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
        Files.createDirectories(folder);

        Path targetLocation = folder.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return rootLocation.relativize(targetLocation).toString().replace('\\', '/');
    }

    public Resource loadFileAsResource(String relativePath) throws IOException {
        Path filePath = rootLocation.resolve(relativePath).normalize();
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new IOException("File not found: " + relativePath);
        }

        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException ex) {
            throw new IOException("Unable to read file: " + relativePath, ex);
        }
    }
}
