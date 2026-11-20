package com.hero.inventoryagent.config;

import com.hero.inventoryagent.tools.InventoryQueryTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AgentConfig {

    @Bean
    ChatMemory chatMemory(ChatMemoryRepository repository, AgentProperties props) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(props.memoryMaxMessages())
                .build();
    }

    @Bean
    ChatClient agentChatClient(ChatClient.Builder builder,
                          ChatMemory chatMemory,
                          AgentProperties props,
                          InventoryQueryTools inventoryQueryTools,
                          @Value("classpath:prompts/system-inventory-agent.st") Resource systemPrompt) {
        return builder
                .defaultSystem(s -> s.text(systemPrompt)
                        .param("company_name", props.companyName()))
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(inventoryQueryTools)
                .build();
    }

    @Bean
    ChatClient extractionChatClient(ChatClient.Builder builder,
                                    AgentProperties props,
                                    @Value("classpath:prompts/system-movement-extractor.st") Resource systemPrompt) {
        return builder
                .defaultSystem(s -> s.text(systemPrompt).param("company_name", props.companyName()))
                .build();
    }
}