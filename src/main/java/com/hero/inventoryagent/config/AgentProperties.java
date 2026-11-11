package com.hero.inventoryagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "agent")
public record AgentProperties(String companyName, int memoryMaxMessages) {}