package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "clarifications")
public class Clarification implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clarification", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<DiscussionEntry> discussionEntries = new HashSet<>();

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name = "question_answer_id")
    private QuestionAnswer questionAnswer;

    public Clarification() {}

    public Clarification(String title) {
        this.title = title;
    }

    public Clarification(ClarificationDto clarificationDto, QuestionAnswer questionAnswer) {
        this.questionAnswer = questionAnswer;
        if (clarificationDto.getTitle() != null) {
            if (!clarificationDto.getTitle().isEmpty())
                this.title = clarificationDto.getTitle();
            else throw new TutorException(ErrorMessage.CLARIFICATION_TITLE_IS_EMPTY);
        }
        else throw new TutorException(ErrorMessage.CLARIFICATION_TITLE_IS_EMPTY);
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

    public Set<DiscussionEntry> getDiscussionEntries() {
        return discussionEntries;
    }

    public void setDiscussionEntries(Set<DiscussionEntry> discussionEntries) {
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

}
