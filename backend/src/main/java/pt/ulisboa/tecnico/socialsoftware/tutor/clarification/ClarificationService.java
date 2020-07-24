package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.DiscussionEntry;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.DiscussionEntryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.DiscussionEntryRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class ClarificationService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private ClarificationRepository clarificationRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscussionEntryRepository discussionEntryRepository;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClarificationDto createClarification(Integer questionAnswerId, ClarificationDto clarificationDto, User user) {
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
        Clarification clarification = new Clarification(clarificationDto, questionAnswer, user);
        questionAnswer.addClarification(clarification);
        clarificationRepository.save(clarification);

        return new ClarificationDto(clarification);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ClarificationDto> getClarifications(Integer questionAnswerId) {
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
        return questionAnswer.getClarifications().stream().sorted(Comparator.comparing(Clarification::getTitle)).map(ClarificationDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClarificationDto getClarification(Integer clarification) {
        Clarification clarification1 = clarificationRepository.findById(clarification).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarification));
        return new ClarificationDto(clarification1);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DiscussionEntryDto addDiscussionEntry(Integer clarificationID, DiscussionEntryDto discussionEntryDto) {
        if (clarificationID.equals(discussionEntryDto.getClarificationId())) {
            Clarification clarification = clarificationRepository.findById(clarificationID).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationID));
            User user = userRepository.findById(discussionEntryDto.getUserId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, discussionEntryDto.getUserId()));
            DiscussionEntry discussionEntry = new DiscussionEntry(discussionEntryDto, clarification, user);
            discussionEntryRepository.save(discussionEntry);
            clarification.addDiscussionEntry(discussionEntry);
            return new DiscussionEntryDto(discussionEntry);
        } else throw new TutorException(DISCUSSION_ENTRY_CLARIFICATION_ID);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DiscussionEntryDto> getDiscussionEntries(Integer clarificationId) {
        Clarification clarification = clarificationRepository.findById(clarificationId).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
        return clarification.getDiscussionEntries().stream().sorted(Comparator.comparing(DiscussionEntry::getTimestamp)).map(DiscussionEntryDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClarificationDto newClarification(ClarificationDto clarificationDto, int userKey) {
        if (clarificationDto.getDiscussionEntryDtoList()!= null) {
            if (!clarificationDto.getDiscussionEntryDtoList().isEmpty()) {
                User user = userRepository.findByKey(userKey).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userKey));
                ClarificationDto clarificationDto1 = this.createClarification(clarificationDto.getQuestionAnswerId(), clarificationDto, user);
                DiscussionEntryDto discussionEntryDto1 = this.addDiscussionEntry(clarificationDto1.getId(), clarificationDto.getDiscussionEntryDtoList().get(0));
                clarificationDto1.addDiscussionEntryDto(discussionEntryDto1);
                return clarificationDto1;
            }
        }
        throw new TutorException(CLARIFICATION_EMPTY_DISCUSSION_ENTRY);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean userHasCreated(int clarificationId, int userId) {
        Clarification clarification = clarificationRepository.findById(clarificationId).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
        return clarification.getQuestionAnswer().getQuizAnswer().getUser().getKey() == userId;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ClarificationDto> getAllClarifications(Integer key) {
        User user = userRepository.findByKey(key).orElseThrow(() -> new TutorException(USER_NOT_FOUND, key));
        return user.getClarifications().stream()
                .sorted(Comparator.comparing(Clarification::getTitle))
                .map(ClarificationDto::new)
                .collect(Collectors.toList());
    }
}
