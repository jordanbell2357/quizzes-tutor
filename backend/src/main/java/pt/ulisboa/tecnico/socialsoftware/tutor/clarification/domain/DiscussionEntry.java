package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "discussion_entries")
public class DiscussionEntry implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name = "clarification_id")
    private Clarification clarification;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDiscussionEntry(this);
    }


    public Clarification getClarification() {
        return clarification;
    }

    public void setClarification(Clarification clarification) {
        this.clarification = clarification;
    }
}
