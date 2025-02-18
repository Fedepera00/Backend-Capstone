package it.epicode.patronato_gestionale.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct; // Usa questo se hai la dipendenza Jakarta
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleDriveService {


    private Drive driveService;

    @Value("${google.drive.folder.id}")
    private String folderId;

    @Value("${google.drive.credentials.json}")
    private String driveCredentialsJson;


    public GoogleDriveService() {
    }

    @PostConstruct
    private void init() throws GeneralSecurityException, IOException {

        InputStream inputStream = new ByteArrayInputStream(
                driveCredentialsJson.getBytes(StandardCharsets.UTF_8)
        );
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(inputStream)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/drive"));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);


        this.driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer
        ).setApplicationName("Gestionale Patronato").build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Il file è vuoto!");
        }

        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(folderId));


        java.io.File tempFile = java.io.File.createTempFile("upload_", ".pdf");
        file.transferTo(tempFile);

        com.google.api.client.http.FileContent mediaContent =
                new com.google.api.client.http.FileContent(file.getContentType(), tempFile);

        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();


        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        driveService.permissions().create(uploadedFile.getId(), permission).execute();


        return "https://drive.google.com/file/d/" + uploadedFile.getId() + "/view?usp=sharing";
    }
}