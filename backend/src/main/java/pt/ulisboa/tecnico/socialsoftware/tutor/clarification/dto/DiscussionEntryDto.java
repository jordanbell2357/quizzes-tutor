package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.DiscussionEntry;

import java.sql.Timestamp;

public class DiscussionEntryDto {

    private Integer clarificationId;

    private String userName;

    private String message;

    private Timestamp timestamp;


    public DiscussionEntryDto(DiscussionEntry discussionEntry) {
        clarificationId = discussionEntry.getClarification().getQuestionAnswer().getId();
        userName = discussionEntry.getUser().getName();
        message = discussionEntry.getMessage();
        timestamp = discussionEntry.getTimestamp();
    }

    public DiscussionEntryDto() {
    }

    public Integer getClarificationId() {
        return clarificationId;
    }

    public void setClarificationId(Integer clarification) {
        clarificationId = clarification;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
