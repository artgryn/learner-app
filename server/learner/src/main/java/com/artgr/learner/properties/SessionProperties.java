package com.artgr.learner.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// No fixed session size/composition is documented anywhere beyond "6-10 min"
// - these are tunable placeholders until real time-based budgeting exists.
// wordsPerSession is used by ProgressService (estimatedTotal); the other
// three drive SessionScheduler's actual word selection.
@Component
@ConfigurationProperties(prefix = "app.session")
public class SessionProperties {

    private int wordsPerSession = 5;
    private int newWordsPerSession = 3;
    private int exercisesPerWordPerSession = 2;
    private int reviewWordsPerSession = 8;

    public int getWordsPerSession() {
        return wordsPerSession;
    }

    public void setWordsPerSession(int wordsPerSession) {
        this.wordsPerSession = wordsPerSession;
    }

    public int getNewWordsPerSession() {
        return newWordsPerSession;
    }

    public void setNewWordsPerSession(int newWordsPerSession) {
        this.newWordsPerSession = newWordsPerSession;
    }

    public int getExercisesPerWordPerSession() {
        return exercisesPerWordPerSession;
    }

    public void setExercisesPerWordPerSession(int exercisesPerWordPerSession) {
        this.exercisesPerWordPerSession = exercisesPerWordPerSession;
    }

    public int getReviewWordsPerSession() {
        return reviewWordsPerSession;
    }

    public void setReviewWordsPerSession(int reviewWordsPerSession) {
        this.reviewWordsPerSession = reviewWordsPerSession;
    }
}
