package org.millionaire;

import org.millionaire.entity.Question;

public class QuestionDifficulty{
    private Question question;
    private Difficulty difficulty;

    public QuestionDifficulty(Question question, Difficulty difficulty) {
        this.question = question;
        this.difficulty = difficulty;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "QuestionDifficulty{" +
                "question=" + question +
                ", \ndifficulty=" + difficulty.name() + " " + difficulty.getPrice() + " " + difficulty.isFireproof() +
                '}';
    }
}
