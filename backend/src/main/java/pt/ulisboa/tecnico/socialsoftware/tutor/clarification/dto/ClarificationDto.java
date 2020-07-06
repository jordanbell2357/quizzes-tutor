package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;

public class ClarificationDto {
    private Integer questionAnswerId;
    private String title;

    public ClarificationDto() { }

    public ClarificationDto(Clarification clarification) {
        questionAnswerId = clarification.getQuestionAnswer().getId();
        title = clarification.getTitle();
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
}
