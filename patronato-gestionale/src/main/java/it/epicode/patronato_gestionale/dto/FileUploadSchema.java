package it.epicode.patronato_gestionale.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class FileUploadSchema {
    @Schema(type = "string", format = "binary", description = "Il file PDF da caricare")
    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}