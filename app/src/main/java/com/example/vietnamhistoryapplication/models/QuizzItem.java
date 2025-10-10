package com.example.vietnamhistoryapplication.models;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class QuizzItem implements Serializable {
    private String quizzslug;
    private String level;
    public String type;
    private Map<String, String> eventId;  // {"link": "...", "title": "..."}
    private Map<String, Long> settings; // {maxPlayers: 1, timeLimit: 60, ...}
    private String description;
    private int questionCount;
    private List<com.example.vietnamhistoryapplication.models.QuestionItem> questions;

    public QuizzItem() {}

    public QuizzItem(String quizzslug, String level, Map<String, String> eventId,
                    Map<String, Long> settings, String description, String type) {
        this.quizzslug = quizzslug;
        this.level = level;
        this.eventId = eventId;
        this.settings = settings;
        this.description = description;
        this.type = type;
    }

    public String getQuizzslug() { return quizzslug; }
    public void setQuizzslug(String quizzslug) { this.quizzslug = quizzslug; }
    public Long getTimeLimit(){
        return settings.get("timeLimit");
    }
    public String getType(){return type;}
    public String getTitle(){return eventId.get("title");}
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Map<String, String> getEventId() { return eventId; }
    public void setEventId(Map<String, String> eventId) { this.eventId = eventId; }

    public Map<String, Long> getSettings() { return settings; }
    public void setSettings(Map<String, Long> settings) { this.settings = settings; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }

    public List<QuestionItem> getQuestions() { return questions; }
    public void setQuestions(List<QuestionItem> questions) { this.questions = questions; }
}
