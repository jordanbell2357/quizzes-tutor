package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.DiscussionEntry;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.DiscussionEntryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class ClarificationService {

    @Autowired
    private ClarificationRepository clarificationRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private UserRepository userRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClarificationDto createClarification (Integer questionAnswerId, ClarificationDto clarificationDto) {
        QuestionAnswer questionAnswer =  questionAnswerRepository.findById(questionAnswerId).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
        Clarification clarification = new Clarification(clarificationDto, questionAnswer);
        questionAnswer.addClarification(clarification);
        clarificationRepository.save(clarification);

        return new ClarificationDto(clarification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ClarificationDto> getClarifications (Integer questionAnswerId) {
        QuestionAnswer questionAnswer =  questionAnswerRepository.findById(questionAnswerId).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
        return questionAnswer.getClarifications().stream().sorted(Comparator.comparing(Clarification::getTitle)).map(ClarificationDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addDiscussionEntry(Integer clarificationID, DiscussionEntryDto discussionEntryDto) {
        Clarification clarification = clarificationRepository.findById(clarificationID).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationID));
        User user = userRepository.findByUsername(discussionEntryDto.getUserName()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, discussionEntryDto.getUserName()));
        DiscussionEntry discussionEntry = new DiscussionEntry(discussionEntryDto, clarification, user);
        clarification.addDiscussionEntry(discussionEntry);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DiscussionEntryDto> getDiscussionEntries(Integer clarificationId) {
        Clarification clarification = clarificationRepository.findById(clarificationId).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
        return clarification.getDiscussionEntries().stream().sorted(Comparator.comparing(DiscussionEntry::getTimestamp)).map(DiscussionEntryDto::new).collect(Collectors.toList());
    }


}
