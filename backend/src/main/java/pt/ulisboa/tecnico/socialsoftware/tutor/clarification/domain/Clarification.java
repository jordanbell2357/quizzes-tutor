package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "clarifications")
public class Clarification implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "clarification", fetch=FetchType.LAZY, orphanRemoval=true)
    private List<DiscussionEntry> discussionEntries = new ArrayList<>();

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name = "question_answer_id")
    private QuestionAnswer questionAnswer;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "user_id")
    private User user;

    public Clarification() {}

    public Clarification(String title) {
        this.title = title;
    }

    public Clarification(ClarificationDto clarificationDto, QuestionAnswer questionAnswer, User user) {
        if (clarificationDto.getTitle() != null) {
            if (!clarificationDto.getTitle().isEmpty()) {
                this.questionAnswer = questionAnswer;
                this.title = clarificationDto.getTitle();
                this.user = user;
            } else throw new TutorException(ErrorMessage.CLARIFICATION_TITLE_IS_EMPTY);
        } else throw new TutorException(ErrorMessage.CLARIFICATION_TITLE_IS_EMPTY);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitClarification(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DiscussionEntry> getDiscussionEntries() {
        return discussionEntries;
    }

    public void setDiscussionEntries(List<DiscussionEntry> discussionEntries) {
        this.discussionEntries = discussionEntries;
    }

    public void addDiscussionEntry(DiscussionEntry discussionEntry) {
        discussionEntries.add(discussionEntry);
    }

    public QuestionAnswer getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(QuestionAnswer questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
