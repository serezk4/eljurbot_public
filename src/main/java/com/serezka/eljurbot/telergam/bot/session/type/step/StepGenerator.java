package com.serezka.eljurbot.telergam.bot.session.type.step;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public  class StepGenerator {
    private Step defaultStep;

    public StepGenerator(Step defaultStep) {
        this.defaultStep = defaultStep;
    }

    public Step get(String chatId, List<String> data) {
        return defaultStep;
    }
}
