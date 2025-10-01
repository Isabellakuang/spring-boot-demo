package com.java.demo.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.faq")
public record FaqProperties(List<FaqSource> sources) {

	public record FaqSource(String name, String path, boolean preload) { }
}
