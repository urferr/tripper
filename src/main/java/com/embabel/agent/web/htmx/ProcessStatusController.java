package com.embabel.agent.web.htmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.embabel.agent.core.AgentPlatform;

@Controller
public class ProcessStatusController {
    private final Logger logger = LoggerFactory.getLogger(ProcessStatusController.class);

    private final AgentPlatform agentPlatform;

    public ProcessStatusController(AgentPlatform theAgentPlatform) {
	agentPlatform = theAgentPlatform;
    }

    /**
     * The HTML page that shows the status of the plan generation.
     */
    @GetMapping("/status/{processId}")
    public String checkPlanStatus(@PathVariable("processId") String theProcessId,
	    @RequestParam("resultModelKey") String theResultModelKey,
	    @RequestParam("successView") String theSuccessView, Model theModel) {
	var agentProcess = agentPlatform.getAgentProcess(theProcessId);

	if (agentProcess == null) {
	    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Process not found");
	}

	return switch (agentProcess.getStatus()) {
	    case COMPLETED -> {
		logger.info("Process {} completed successfully", theProcessId);
		var result = agentProcess.lastResult();
		theModel.addAttribute(theResultModelKey, result);
		theModel.addAttribute("agentProcess", agentProcess);
		yield theSuccessView;
	    }
	    case FAILED -> {
		logger.error("Process {} failed: {}", theProcessId, agentProcess.getFailureInfo());
		theModel.addAttribute("error",
			"Failed to generate travel plan: %s".formatted(agentProcess.getFailureInfo()));
		yield "common/processing-error";
	    }
	    case TERMINATED -> {
		logger.info("Process {} was terminated", theProcessId);
		theModel.addAttribute("error", "Process was terminated before completion");
		yield "common/processing-error";
	    }
	    default -> {
		theModel.addAttribute("processId", theProcessId);
		theModel.addAttribute("pageTitle", "Planning Journey...");
		yield "common/processing"; // Keep showing loading state
	    }
	};
    }
}
