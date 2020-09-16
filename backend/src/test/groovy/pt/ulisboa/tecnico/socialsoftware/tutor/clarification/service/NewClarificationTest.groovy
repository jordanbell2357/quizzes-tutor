package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.DiscussionEntry
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.DiscussionEntryDto
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_EMPTY_DISCUSSION_ENTRY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DISCUSSION_ENTRY_TITLE_IS_EMPTY

@DataJpaTest
class NewClarificationTest extends SpockTest {
    def questionAnswer
    def user
    def quiz
    def quizAnswer
    def quizQuestion
    def course
    def courseExecution

    def setup() {
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, true, false)
        user.addCourse(courseExecution)
        userRepository.save(user)
        user.setKey(user.getId())

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        def question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setCourse(course)
        questionRepository.save(question)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)

        questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
    }

    def 'Creating a new Clarification with a DiscussionEntry'(){
        given: 'A Clarification'
        def clarification = new ClarificationDto();
        clarification.setTitle(CLARIFICATION_1_TITLE)
        clarification.setId(CLARIFICATION_1_ID)
        clarification.setQuestionAnswerId(questionAnswerRepository.findAll().get(0).getId())

        def discussionEntry = new DiscussionEntryDto()
        discussionEntry.setMessage(DISCUSSION_1_MESSAGE)
        discussionEntry.setClarificationId(clarification.getId())
        discussionEntry.setUserId(userRepository.findAll().get(0).getId())
        ArrayList<DiscussionEntryDto> discussionEntryDtoArrayList = new ArrayList<DiscussionEntryDto>()
        discussionEntryDtoArrayList.add(discussionEntry)
        clarification.setDiscussionEntryDtoList(discussionEntryDtoArrayList)
        def user = userRepository.findAll().get(0)


        when:
        def clarificationDto = clarificationService.newClarification(clarification, user.getKey())

        then:
        clarificationDto.id == clarification.getId()
        clarificationDto.discussionEntryDtoList.size() == 1
        clarificationDto.discussionEntryDtoList.get(0).message == DISCUSSION_1_MESSAGE
    }

    def 'Creating a new Clarification with no DiscussionEntry'(){
        given: 'A clarification'
        def clarification = new ClarificationDto();
        clarification.setTitle(CLARIFICATION_1_TITLE)
        clarification.setId(CLARIFICATION_1_ID)
        clarification.setQuestionAnswerId(questionAnswerRepository.findAll().get(0).getId())
        def user = userRepository.findAll().get(0)



        when:
        clarificationService.newClarification(clarification, user.getKey())

        then:

        clarificationRepository.findAll().size() == 0
        TutorException exception = thrown()
        exception.getErrorMessage() == CLARIFICATION_EMPTY_DISCUSSION_ENTRY


    }




        @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
