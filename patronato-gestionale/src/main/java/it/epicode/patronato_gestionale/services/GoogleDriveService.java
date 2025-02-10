package it.epicode.patronato_gestionale.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleDriveService {

    private final Drive driveService;

    @Value("${google.drive.folder.id}")
    private String folderId;

    public GoogleDriveService() throws GeneralSecurityException, IOException {
        // Carica le credenziali dal file JSON
        InputStream inputStream = new ClassPathResource("google-drive-service-account.json").getInputStream();
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(inputStream)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/drive"));

        // Converti le credenziali in un HttpRequestInitializer
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // Inizializza il servizio Google Drive
        this.driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer
        ).setApplicationName("Gestionale Patronato").build();
    }

    /**
     * Carica un file su Google Drive
     */
    public String uploadFile(MultipartFile file) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(folderId));

        java.io.File tempFile = java.io.File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);

        FileContent mediaContent = new FileContent("application/pdf", tempFile);
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        // Imposta il file come visibile pubblicamente
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        driveService.permissions().create(uploadedFile.getId(), permission).execute();

        String fileLink = uploadedFile.getWebViewLink();
        System.out.println("✅ File caricato con successo! Link: " + fileLink);

        return fileLink; // Ora restituisce il link pubblico
    }

    /**
     * Scarica un file da Google Drive
     */
    public void downloadFile(String fileId, String outputPath) throws IOException {
        java.io.File outputFile = new java.io.File(outputPath);
        outputFile.getParentFile().mkdirs(); // Crea la directory se non esiste

        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        }

        System.out.println("✅ File scaricato con successo: " + outputPath);
    }
}