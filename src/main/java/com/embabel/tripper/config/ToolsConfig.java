package com.embabel.tripper.config;

import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.embabel.agent.core.ToolGroup;
import com.embabel.agent.core.ToolGroupDescription;
import com.embabel.agent.core.ToolGroupPermission;
import com.embabel.agent.tools.mcp.McpToolGroup;

import io.modelcontextprotocol.client.McpSyncClient;

@Configuration
public class ToolsConfig {
    public static final String AIRBNB = "airbnb";

    private final RestClient restClient;
    private final List<McpSyncClient> mcpSyncClients;

    public ToolsConfig(RestClient.Builder theRestClientBuilder, List<McpSyncClient> theMcpSyncClients) {
	restClient = theRestClientBuilder.build();
	mcpSyncClients = theMcpSyncClients;
    }

    @Bean
    public RestClient restClient() {
	return restClient;
    }

    @Bean
    public ToolGroup mcpAirbnbToolsGroup() {
	return new McpToolGroup(ToolGroupDescription.create("Airbnb tools", AIRBNB), "openbnb-airbnb", "Docker",
		Set.of(ToolGroupPermission.INTERNET_ACCESS), mcpSyncClients,
		(theCallback) -> theCallback.getToolDefinition().name().contains(AIRBNB));
    }
}
