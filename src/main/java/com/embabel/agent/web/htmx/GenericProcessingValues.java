package com.embabel.agent.web.htmx;

import org.springframework.ui.Model;

import com.embabel.agent.core.AgentProcess;

public record GenericProcessingValues(AgentProcess agentProcess, String pageTitle, String detail, String resultModelKey,
	String successView) {
    public void addToModel(Model theModel) {
	theModel.addAttribute("processId", agentProcess.getId());
	theModel.addAttribute("pageTitle", pageTitle);
	theModel.addAttribute("detail", detail);
	theModel.addAttribute("resultModelKey", resultModelKey);
	theModel.addAttribute("successView", successView);
    }

}
