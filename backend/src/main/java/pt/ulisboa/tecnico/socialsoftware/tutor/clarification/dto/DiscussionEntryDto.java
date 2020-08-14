package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.DiscussionEntry;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;

import java.util.Date;

public class DiscussionEntryDto {
    private Integer clarificationId;
    private String userName;
    private Integer userId;
    private String message;
    private String dateTime;
    private Integer id;

    public DiscussionEntryDto(DiscussionEntry discussionEntry) {
        clarificationId = discussionEntry.getClarification().getId();
        userName = discussionEntry.getUser().getName();
        userId = discussionEntry.getUser().getId();
        message = discussionEntry.getMessage();
        dateTime = DateHandler.toISOString(discussionEntry.getLocalDateTime());
        id = discussionEntry.getId();
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
