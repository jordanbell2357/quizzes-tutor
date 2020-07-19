package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetClarificationsTest extends SpockTest {
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

    def 'Getting an existing clarification'(){
        given: 'a clarification and a questionAnswer'
        def clarification = new Clarification()
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        clarification.setQuestionAnswer(questionAnswer)
        clarification.setTitle(CLARIFICATION_1_TITLE)
        questionAnswer.addClarification(clarification)
        clarificationRepository.save(clarification)

        when:
        def clarificationDtos = clarificationService.getClarifications(questionAnswer.getId())
        then:
        clarificationDtos.size() == 1
        clarificationDtos.get(0).getTitle() == CLARIFICATION_1_TITLE
        clarificationDtos.get(0).getQuestionAnswerId() == questionAnswer.getId()
    }

    def 'Two clarifications sorted by title'(){
        given: 'two clarifications and a questionAnswer'
        def clarification1 = new Clarification()
        clarification1.setTitle(CLARIFICATION_1_TITLE)

        def clarification2 = new Clarification()
        clarification2.setTitle(CLARIFICATION_2_TITLE)

        def questionAnswer = questionAnswerRepository.findAll().get(0)

        and: 'Saving the clarification on the questionAnswer'
        clarification1.setQuestionAnswer(questionAnswer)
        clarification2.setQuestionAnswer(questionAnswer)
        questionAnswer.addClarification(clarification2)
        questionAnswer.addClarification(clarification1)

        clarificationRepository.save(clarification1)
        clarificationRepository.save(clarification2)

        when:
        def clarificationDtos = clarificationService.getClarifications(questionAnswer.getId())

        then:
        clarificationDtos.size() == 2
        def clarificationX = clarificationDtos.get(0)
        def clarificationY = clarificationDtos.get(1)

        clarificationX.getTitle() == CLARIFICATION_1_TITLE
        clarificationY.getTitle() == CLARIFICATION_2_TITLE
    }

    def 'Getting an non-existing clarification'(){
        given: 'A questionAnswer'
        def questionAnswer = questionAnswerRepository.findAll().get(0)

        when:
        def clarificationDtos = clarificationService.getClarifications(questionAnswer.getId())
        then:
        clarificationDtos.size() == 0
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}