package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User


@DataJpaTest
class GetAllClarifications  extends SpockTest {
    def questionAnswer
    def user
    def quiz
    def quizAnswer
    def quizQuestion

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
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

    def 'A user with a clarification'() {
        given: 'a clarification'
        def clarification = new Clarification()
        clarification.setQuestionAnswer(questionAnswerRepository.findAll().get(0))
        clarification.setTitle(CLARIFICATION_1_TITLE)
        def user = userRepository.findAll().get(0)
        clarification.setUser(user)
        user.addClarification(clarification)
        clarificationRepository.save(clarification)

        when:
        ArrayList<ClarificationDto> clarificationList = clarificationService.getAllClarifications(user.getKey())

        then:
        clarificationList.size() == 1
        clarificationList.get(0).getTitle() == CLARIFICATION_1_TITLE
        clarificationList.get(0).getId() == CLARIFICATION_1_ID
    }

    def 'A user without clarifications'() {
        given: 'a user'
        def user = userRepository.findAll().get(0)

        when:
        ArrayList<ClarificationDto> clarificationList = clarificationService.getAllClarifications(user.getKey())

        then:
        clarificationList.size() == 0
    }
    
        @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}
