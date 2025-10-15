package com.example.vietnamhistoryapplication.models;

import java.util.List;

public class QuestionItem {
    private String question;
    private List<String> options;
    private int correctAnswer;
    private int orderQuestion;
    private String explanation;


    public QuestionItem() {}

    public QuestionItem(String question, List<String> options, int correctAnswer,
                        int orderQuestion, String explanation) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.orderQuestion = orderQuestion;
        this.explanation = explanation;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }

    public int getOrderQuestion() { return orderQuestion; }
    public void setOrderQuestion(int orderQuestion) { this.orderQuestion = orderQuestion; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }


}
