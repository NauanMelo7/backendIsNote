package dev.isnote.ai.domain;

public enum AiJobStatus {

    //Especially important for image generation(async)
    PENDING,
    RUNNING,
    SUCCEEDED,
    FAILED
}
