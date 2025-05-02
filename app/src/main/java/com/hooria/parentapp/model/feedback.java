package com.hooria.parentapp.model;

public class feedback {
    private String guardianCNIC;
    private String feedbackText;
    private String sentiment;
    private com.google.firebase.Timestamp timestamp;
    public feedback() {
    }

    public feedback(String guardianCNIC, String feedbackText, String sentiment,com.google.firebase.Timestamp timestamp) {
        this.guardianCNIC = guardianCNIC;
        this.feedbackText = feedbackText;
        this.timestamp = timestamp;
        this.sentiment=sentiment;
    }


    public String getGuardianCNIC() {
        return guardianCNIC;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return timestamp;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }


    public void setGuardianCNIC(String guardianCNIC) {
        this.guardianCNIC = guardianCNIC;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        this.timestamp = timestamp;}
}
