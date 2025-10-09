package com.scb.ragdemo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class CacheKeyGenerator {

    private static final String QUERY_CACHE_PREFIX = "query:";
    private static final String DOCUMENT_CACHE_PREFIX = "doc:";
    private static final String CHUNK_CACHE_PREFIX = "chunk:";

    public String generateQueryCacheKey(String query, String mode) {
        String combined = query + ":" + mode;
        return QUERY_CACHE_PREFIX + hashString(combined);
    }

    public String generateDocumentCacheKey(Long documentId) {
        return DOCUMENT_CACHE_PREFIX + documentId;
    }

    public String generateChunkCacheKey(Long documentId, int chunkIndex) {
        return CHUNK_CACHE_PREFIX + documentId + ":" + chunkIndex;
    }

    public String generateDocumentListCacheKey() {
        return DOCUMENT_CACHE_PREFIX + "list";
    }

    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            return String.valueOf(input.hashCode());
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public boolean isQueryCacheKey(String key) {
        return key != null && key.startsWith(QUERY_CACHE_PREFIX);
    }

    public boolean isDocumentCacheKey(String key) {
        return key != null && key.startsWith(DOCUMENT_CACHE_PREFIX);
    }

    public boolean isChunkCacheKey(String key) {
        return key != null && key.startsWith(CHUNK_CACHE_PREFIX);
    }
}
