package com.example.mlautogradecodetool.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PredictionRequest {
    private String instruction;
    private String output;

    private float code_style;

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

	public float getCode_style() {
		return code_style;
	}

	public void setCode_style(float code_style) {
		this.code_style = code_style;
	}

}
