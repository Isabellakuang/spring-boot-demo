package com.scb.ragdemo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class TextSplitter {

    private static final int DEFAULT_CHUNK_SIZE = 1000;
    private static final int DEFAULT_OVERLAP = 200;
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[.!?。！？]+");
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("\n\n+");

    public List<String> splitText(String text) {
        return splitText(text, DEFAULT_CHUNK_SIZE, DEFAULT_OVERLAP);
    }

    public List<String> splitText(String text, int chunkSize, int overlap) {
        if (text == null || text.trim().isEmpty()) {
            log.warn("Input text is empty");
            return new ArrayList<>();
        }

        if (chunkSize <= 0) {
            chunkSize = DEFAULT_CHUNK_SIZE;
        }
        if (overlap < 0 || overlap >= chunkSize) {
            overlap = Math.min(DEFAULT_OVERLAP, chunkSize / 2);
        }

        log.info("Starting text splitting, total length: {}, chunk size: {}, overlap: {}", text.length(), chunkSize, overlap);

        List<String> chunks = new ArrayList<>();
        String[] paragraphs = PARAGRAPH_PATTERN.split(text);

        StringBuilder currentChunk = new StringBuilder();
        
        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) {
                continue;
            }

            if (currentChunk.length() + paragraph.length() <= chunkSize) {
                if (currentChunk.length() > 0) {
                    currentChunk.append("\n\n");
                }
                currentChunk.append(paragraph);
            } else {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString());
                    
                    String overlapText = getOverlapText(currentChunk.toString(), overlap);
                    currentChunk = new StringBuilder(overlapText);
                }

                if (paragraph.length() > chunkSize) {
                    List<String> sentenceChunks = splitBySentences(paragraph, chunkSize, overlap);
                    for (int i = 0; i < sentenceChunks.size(); i++) {
                        if (i == 0 && currentChunk.length() > 0) {
                            currentChunk.append("\n\n").append(sentenceChunks.get(i));
                            chunks.add(currentChunk.toString());
                            currentChunk = new StringBuilder();
                        } else if (i == sentenceChunks.size() - 1) {
                            currentChunk.append(sentenceChunks.get(i));
                        } else {
                            chunks.add(sentenceChunks.get(i));
                        }
                    }
                } else {
                    if (currentChunk.length() > 0) {
                        currentChunk.append("\n\n");
                    }
                    currentChunk.append(paragraph);
                }
            }
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }

        log.info("Text splitting completed, generated {} chunks", chunks.size());
        return chunks;
    }

    private List<String> splitBySentences(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        List<String> sentences = splitIntoSentences(text);

        StringBuilder currentChunk = new StringBuilder();
        
        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() <= chunkSize) {
                currentChunk.append(sentence);
            } else {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString());
                    
                    String overlapText = getOverlapText(currentChunk.toString(), overlap);
                    currentChunk = new StringBuilder(overlapText);
                }
                
                if (sentence.length() > chunkSize) {
                    chunks.addAll(splitByCharacters(sentence, chunkSize, overlap));
                    currentChunk = new StringBuilder();
                } else {
                    currentChunk.append(sentence);
                }
            }
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }

        return chunks;
    }

    private List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        Matcher matcher = SENTENCE_PATTERN.matcher(text);
        
        int lastEnd = 0;
        while (matcher.find()) {
            String sentence = text.substring(lastEnd, matcher.end()).trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
            lastEnd = matcher.end();
        }
        
        if (lastEnd < text.length()) {
            String remaining = text.substring(lastEnd).trim();
            if (!remaining.isEmpty()) {
                sentences.add(remaining);
            }
        }
        
        return sentences;
    }

    private List<String> splitByCharacters(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start = end - overlap;
            
            if (start >= text.length() - overlap) {
                break;
            }
        }
        
        return chunks;
    }

    private String getOverlapText(String text, int overlap) {
        if (text.length() <= overlap) {
            return text;
        }
        return text.substring(text.length() - overlap);
    }
}
