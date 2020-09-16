package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
@DataJpaTest
class CreateClarificationTest extends SpockTest {
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

    def 'new clarification'(){
        given: 'A questionAnswerId'
        def questionAnswerId = questionAnswerRepository.findAll().get(0).getId()
        and: 'A ClarificationDto'
        def clarificationDto = new ClarificationDto()
        clarificationDto.setTitle(CLARIFICATION_1_TITLE)
        clarificationDto.setQuestionAnswerId(questionAnswerId)
        def user = userRepository.findAll().get(0)

        when:
        clarificationService.createClarification(questionAnswerId, clarificationDto, user)

        then:
        clarificationRepository.findAll().get(0).getTitle() == CLARIFICATION_1_TITLE
        clarificationRepository.findAll().get(0).getQuestionAnswer().getId() == questionAnswerId

    }

    def 'new clarification with no title'() {
        given: 'A questionAnswerId'
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        def questionAnswerId = questionAnswer.getId()
        and: 'A ClarificationDto with no title'
        def clarificationDto = new ClarificationDto()
        clarificationDto.setQuestionAnswerId(questionAnswerId)
        def user = userRepository.findAll().get(0)

        when:
        clarificationService.createClarification(questionAnswerId, clarificationDto, user)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == CLARIFICATION_TITLE_IS_EMPTY
    }

    def 'new clarification with empty title'() {
        given: 'A questionAnswerId'
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        def questionAnswerId = questionAnswer.getId()
        and: 'A ClarificationDto with no title'
        def clarificationDto = new ClarificationDto()
        clarificationDto.setTitle('')
        clarificationDto.setQuestionAnswerId(questionAnswerId)
        def user = userRepository.findAll().get(0)

        when:
        clarificationService.createClarification(questionAnswerId, clarificationDto, user)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == CLARIFICATION_TITLE_IS_EMPTY
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_TITLE_IS_EMPTY

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NO_LONGER_AVAILABLE
