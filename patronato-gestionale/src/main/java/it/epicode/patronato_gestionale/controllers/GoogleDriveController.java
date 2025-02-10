package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.services.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/drive")
public class GoogleDriveController {

    @Autowired
    private GoogleDriveService googleDriveService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data") // 👈 Aggiunto consumes
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileId = googleDriveService.uploadFile(file);
            return ResponseEntity.ok("✅ File caricato con successo! ID: " + fileId);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("❌ Errore nel caricamento del file: " + e.getMessage());
        }
    }
}