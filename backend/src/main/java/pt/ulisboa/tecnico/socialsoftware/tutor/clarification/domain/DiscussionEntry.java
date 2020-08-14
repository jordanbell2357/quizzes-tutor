package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.DiscussionEntryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "discussion_entries")
public class DiscussionEntry implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "clarification_id")
    private Clarification clarification;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime localDateTime;

    private String message;

    public DiscussionEntry() {
    }

    public DiscussionEntry(DiscussionEntryDto discussionEntryDto, Clarification clarification, User user) {
        this.clarification = clarification;
        this.user = user;
        this.localDateTime = DateHandler.now();
        this.id = discussionEntryDto.getId();
        if (discussionEntryDto.getMessage() != null) {
            if (!discussionEntryDto.getMessage().isEmpty()) {
                this.message = discussionEntryDto.getMessage();
            } else throw new TutorException(ErrorMessage.DISCUSSION_ENTRY_TITLE_IS_EMPTY);
        } else throw new TutorException(ErrorMessage.DISCUSSION_ENTRY_TITLE_IS_EMPTY);

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

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
