package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.TutorApplication;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.DiscussionEntryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class ClarificationController {

    @Autowired
    ClarificationService clarificationService;

    @Autowired
    ClarificationRepository clarificationRepository;

    @GetMapping("/clarification/create")
    public ClarificationDto createClarification(@Valid @RequestBody ClarificationDto clarificationDto, @Valid @RequestBody DiscussionEntryDto discussionEntryDto) {
        return clarificationService.newClarification(discussionEntryDto, clarificationDto);
    }

    @GetMapping("/clarification/{clarificationId}/add")
    public DiscussionEntryDto addDiscussionEntry(@Valid @RequestBody DiscussionEntryDto discussionEntryDto, @PathVariable Integer clarificationId) {
        return clarificationService.addDiscussionEntry(clarificationId, discussionEntryDto);
    }

    @GetMapping("/clarification/{clarificationId}/get")
    public ClarificationDto getClarification(@PathVariable Integer clarificationId) {
        return clarificationService.getClarification(clarificationId);
    }
}
