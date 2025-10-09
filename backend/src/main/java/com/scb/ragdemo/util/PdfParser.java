package com.scb.ragdemo.util;

import com.scb.ragdemo.exception.custom.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class PdfParser {

    private static final int MAX_FILE_SIZE = 50 * 1024 * 1024;

    public String extractText(InputStream inputStream, String fileName) {
        log.info("Starting to parse PDF file: {}", fileName);
        
        try (PDDocument document = PDDocument.load(inputStream)) {
            if (document.isEncrypted()) {
                log.error("PDF file is encrypted, cannot parse: {}", fileName);
                throw new InvalidFileException("PDF file is encrypted, please provide an unencrypted file");
            }

            int numberOfPages = document.getNumberOfPages();
            log.info("PDF file page count: {}", numberOfPages);

            if (numberOfPages == 0) {
                throw new InvalidFileException("PDF file is empty");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            
            String text = stripper.getText(document);
            text = cleanText(text);
            
            if (text.trim().isEmpty()) {
                log.warn("PDF file does not contain extractable text: {}", fileName);
                throw new InvalidFileException("PDF file does not contain extractable text content");
            }

            log.info("Successfully parsed PDF file: {}, extracted text length: {} characters", fileName, text.length());
            return text;

        } catch (IOException e) {
            log.error("Failed to parse PDF file: {}", fileName, e);
            throw new InvalidFileException("Failed to parse PDF file: " + e.getMessage());
        }
    }

    private String cleanText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        text = text.replaceAll("\\r\\n", "\n");
        text = text.replaceAll("\\r", "\n");
        text = text.replaceAll("\n{3,}", "\n\n");
        text = text.replaceAll("[ \t]+", " ");
        text = text.replaceAll(" \n", "\n");
        text = text.replaceAll("\n ", "\n");
        
        return text.trim();
    }

    public int getPageCount(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            return document.getNumberOfPages();
        }
    }
}
