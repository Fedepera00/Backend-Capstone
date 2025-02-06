package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Fattura;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateFatturaPdf(Fattura fattura) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Titolo
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Fattura");
                contentStream.endText();

                // Dettagli della fattura
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Numero: " + fattura.getNumero());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Nome: " + fattura.getNome() + " " + fattura.getCognome());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Codice Fiscale: " + fattura.getCodiceFiscale());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Indirizzo: " + fattura.getIndirizzo());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Telefono: " + fattura.getTelefono());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Email: " + fattura.getEmail());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Importo: €" + fattura.getImporto());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Descrizione: " + fattura.getDescrizione());
                contentStream.endText();
            }

            document.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Errore nella generazione del PDF", e);
        }
    }
}