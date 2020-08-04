package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.DiscussionEntry;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ClarificationDto {
    private Integer questionAnswerId;
    private String title;
    private String question;
    private Integer id;
    private ArrayList<DiscussionEntryDto> discussionEntryDtoList;
    private String username;

    public ClarificationDto() {
    }

    public ClarificationDto(Clarification clarification) {
        questionAnswerId = clarification.getQuestionAnswer().getId();
        title = clarification.getTitle();
        id = clarification.getId();
        question = clarification.getQuestionAnswer().getQuizQuestion().getQuestion().getTitle();
        discussionEntryDtoList = (ArrayList<DiscussionEntryDto>) clarification.getDiscussionEntries().stream().map(DiscussionEntryDto::new).collect(Collectors.toList());
        username = clarification.getUser().getUsername();
    }

    public Integer getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Integer questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArrayList<DiscussionEntryDto> getDiscussionEntryDtoList() {
        return discussionEntryDtoList;
    }

    public void setDiscussionEntryDtoList(ArrayList<DiscussionEntryDto> discussionEntryDtoList) {
        this.discussionEntryDtoList = discussionEntryDtoList;
    }

    public void addDiscussionEntryDto(DiscussionEntryDto discussionEntryDto) {
        this.discussionEntryDtoList.add(discussionEntryDto);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}