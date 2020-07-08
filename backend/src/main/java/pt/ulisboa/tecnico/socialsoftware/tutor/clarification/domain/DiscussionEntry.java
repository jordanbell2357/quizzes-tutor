package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.DiscussionEntryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.sql.Timestamp;

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

    private Timestamp timestamp;

    private String message;

    public DiscussionEntry() {
    }

    public DiscussionEntry(DiscussionEntryDto discussionEntryDto, Clarification clarification, User user) {
        this.clarification = clarification;
        this.user = user;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.id = discussionEntryDto.getId();
        if (discussionEntryDto.getMessage() != null) {
            if (!discussionEntryDto.getMessage().isEmpty()) {
                this.message = discussionEntryDto.getMessage();
            }
            else throw new TutorException(ErrorMessage.DISCUSSION_ENTRY_TITLE_IS_EMPTY);
        }
        else throw new TutorException(ErrorMessage.DISCUSSION_ENTRY_TITLE_IS_EMPTY);

    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDiscussionEntry(this);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Clarification getClarification() {
        return clarification;
    }

    public void setClarification(Clarification clarification) {
        this.clarification = clarification;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
