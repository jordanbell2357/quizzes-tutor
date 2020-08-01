package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#clarificationDto.questionAnswerId, 'QA.ACCESS')")
    public ClarificationDto createClarification(@Valid @RequestBody ClarificationDto clarificationDto, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return clarificationService.newClarification(clarificationDto, user.getKey());
    }

    @PostMapping("/clarification/{clarificationId}/add")
    @PreAuthorize("hasRole('ROLE_TEACHER') or (hasRole('ROLE_STUDENT') and hasPermission(#clarificationId, 'CLARIFICATION.ACCESS'))")
    public DiscussionEntryDto addDiscussionEntry(@Valid @RequestBody DiscussionEntryDto discussionEntryDto, @PathVariable Integer clarificationId) {
        return clarificationService.addDiscussionEntry(clarificationId, discussionEntryDto, false, discussionEntryDto.getUserId());
    }

    @GetMapping("/clarification/{clarificationId}/get")
    @PreAuthorize("hasRole('ROLE_TEACHER') or (hasRole('ROLE_STUDENT') and hasPermission(#clarificationId, 'CLARIFICATION.ACCESS'))")
    public ClarificationDto getClarification(@PathVariable Integer clarificationId) {
        return clarificationService.getClarification(clarificationId);
    }

    @GetMapping("/question-answer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<ClarificationDto> getQAClarifications(@PathVariable Integer questionAnswerId) {
        return clarificationService.getClarifications(questionAnswerId);
    }

    @GetMapping("/clarification/get")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<ClarificationDto> getAllClarifications(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return clarificationService.getAllClarifications(user.getKey());
    }

}
