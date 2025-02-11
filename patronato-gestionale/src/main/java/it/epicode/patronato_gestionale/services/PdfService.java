package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Fattura;
import it.epicode.patronato_gestionale.entities.Dettaglio;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PdfService {

    public byte[] generateFatturaPdf(Fattura fattura) {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float margin = 50;
            PDRectangle mediaBox = page.getMediaBox();
            float yStart = mediaBox.getUpperRightY() - margin;
            float width = mediaBox.getWidth() - 2 * margin;

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Header: Titolo "Fattura"
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
            contentStream.newLineAtOffset(margin, yStart);
            contentStream.showText("Fattura");
            contentStream.endText();

            float lineY = yStart - 10;
            contentStream.moveTo(margin, lineY);
            contentStream.lineTo(margin + width, lineY);
            contentStream.stroke();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataEmissioneStr = fattura.getDataEmissione().format(formatter);

            // Dettagli della fattura
            float textY = lineY - 30;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(margin, textY);

            String[] lines = {
                    "Numero Fattura: " + fattura.getNumero(),
                    "Data Emissione: " + dataEmissioneStr,
                    "Cliente: " + fattura.getNome() + " " + fattura.getCognome(),
                    "Codice Fiscale: " + fattura.getCodiceFiscale(),
                    "Indirizzo: " + fattura.getIndirizzo(),
                    "Telefono: " + (fattura.getTelefono() != null ? fattura.getTelefono() : "N/A"),
                    "Email: " + (fattura.getEmail() != null ? fattura.getEmail() : "N/A"),
                    "Stato: " + fattura.getStato()
            };

            for (String line : lines) {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -20);
            }
            contentStream.endText();

            // Tabella Dettagli
            float tableY = textY - 40;
            tableY = drawTable(contentStream, tableY, margin, width, fattura.getDettagli());

            // Footer
            drawFooter(contentStream, margin);

            contentStream.close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Errore nella generazione del PDF", e);
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                // Log dell'errore
            }
        }
    }

    private float drawTable(PDPageContentStream contentStream, float yPosition, float margin, float width, List<Dettaglio> dettagli) throws IOException {
        float cellHeight = 20;
        float[] colWidths = {width * 0.4f, width * 0.2f, width * 0.2f, width * 0.2f};
        float xPos = margin;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        String[] headers = {"Descrizione", "Quantità", "Prezzo Unitario", "Totale"};
        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(xPos, yPosition);
            contentStream.showText(headers[i]);
            contentStream.endText();
            xPos += colWidths[i];
        }
        yPosition -= cellHeight;

        contentStream.setFont(PDType1Font.HELVETICA, 12);
        for (Dettaglio dett : dettagli) {
            xPos = margin;
            String[] values = {
                    dett.getDescrizione(),
                    String.valueOf(dett.getQuantita()),
                    "€" + dett.getPrezzoUnitario(),
                    "€" + dett.getTotale()
            };
            for (int i = 0; i < values.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(xPos, yPosition);
                contentStream.showText(values[i]);
                contentStream.endText();
                xPos += colWidths[i];
            }
            yPosition -= cellHeight;
        }

        return yPosition;
    }

    private void drawFooter(PDPageContentStream contentStream, float margin) throws IOException {
        float footerY = 50;
        contentStream.moveTo(margin, footerY);
        contentStream.lineTo(margin + 500, footerY);
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
        contentStream.newLineAtOffset(margin, footerY - 10);
        contentStream.showText("Firma del responsabile");
        contentStream.endText();
    }
}