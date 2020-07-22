package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.TutorApplication;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.DiscussionEntryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class ClarificationController {

    @Autowired
    ClarificationService clarificationService;

    @Autowired
    ClarificationRepository clarificationRepository;

    @PostMapping("/clarification/create")
    public ClarificationDto createClarification(@Valid @RequestBody ClarificationDto clarificationDto) {
        return clarificationService.newClarification(clarificationDto);
    }

    @PostMapping("/clarification/{clarificationId}/add")
    public DiscussionEntryDto addDiscussionEntry(@Valid @RequestBody DiscussionEntryDto discussionEntryDto, @PathVariable Integer clarificationId) {
        return clarificationService.addDiscussionEntry(clarificationId, discussionEntryDto);
    }

    @GetMapping("/clarification/{clarificationId}/get")
    public ClarificationDto getClarification(@PathVariable Integer clarificationId) {
        return clarificationService.getClarification(clarificationId);
    }

    @GetMapping("/question-answer/{questionAnswerId}/clarifications")
    public List<ClarificationDto> getQAClarifications(@PathVariable Integer questionAnswerId) {
        return clarificationService.getClarifications(questionAnswerId);
    }



}
