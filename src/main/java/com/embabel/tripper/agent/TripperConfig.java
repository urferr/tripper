package com.embabel.tripper.agent;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.embabel.agent.api.common.Actor;
import com.embabel.agent.prompt.element.ToolCallControl;
import com.embabel.agent.prompt.persona.Persona;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;
import com.embabel.common.ai.model.LlmOptions;

@ConfigurationProperties("embabel.tripper")
public class TripperConfig {
    private int wordCount = 700;
    private int imageWidth = 800;
    private Actor<Persona> planner;
    private Actor<RoleGoalBackstory> researcher;
    private ToolCallControl toolCallControl = new ToolCallControl();
    private LlmOptions thinkerLlm;
    private int maxConcurrency = 12;

    public int getWordCount() {
	return wordCount;
    }

    public void setWordCount(int theWordCount) {
	wordCount = theWordCount;
    }

    public int getImageWidth() {
	return imageWidth;
    }

    public void setImageWidth(int theImageWidth) {
	imageWidth = theImageWidth;
    }

    public Actor<Persona> getPlanner() {
	return planner;
    }

    public void setPlanner(Actor<Persona> thePlanner) {
	planner = thePlanner;
    }

    public Actor<RoleGoalBackstory> getResearcher() {
	return researcher;
    }

    public void setResearcher(Actor<RoleGoalBackstory> theResearcher) {
	researcher = theResearcher;
    }

    public ToolCallControl getToolCallControl() {
	return toolCallControl;
    }

    public void setToolCallControl(ToolCallControl theToolCallControl) {
	toolCallControl = theToolCallControl;
    }

    public LlmOptions getThinkerLlm() {
	return thinkerLlm;
    }

    public void setThinkerLlm(LlmOptions theThinkerLlm) {
	thinkerLlm = theThinkerLlm;
    }

    public int getMaxConcurrency() {
	return maxConcurrency;
    }

    public void setMaxConcurrency(int theMaxConcurrency) {
	maxConcurrency = theMaxConcurrency;
    }

}
