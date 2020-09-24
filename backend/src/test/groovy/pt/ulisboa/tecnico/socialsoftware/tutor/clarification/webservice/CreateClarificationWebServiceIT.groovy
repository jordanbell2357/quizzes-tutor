package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateClarificationWebServiceIT extends SpockTest{
    @LocalServerPort
    private int port

    def questionAnswer
    def quiz
    def quizAnswer
    def quizQuestion
    def question
    def course
    def courseExecution
    def student

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        courseExecution = courseExecutionRepository.findB().get(0)
        course = courseRepository.findByNameType('Demo ').get()
        def user = userRepository.findByUsername('Demo-Student').get()

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        question = new Question()
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

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
    }

    def "creating a clarification"(){
        given: 'A questionAnswerId'
        def questionAnswerId = questionAnswer.getId()
        and: 'A ClarificationDto'
        def clarificationDto = new ClarificationDto()
        clarificationDto.setTitle(CLARIFICATION_1_TITLE)
        clarificationDto.setQuestionAnswerId(questionAnswerId)

        when:
        def response = restClient.post(
                path: '/clarification/create',
                body: clarificationDto,
                requestContentType: 'application/json'
        )
        then: "check the response status"
        response != null
        response.status == 200
        and: "if it responds with the correct questionSubmission"
        def clarification = response.data
        clarification.id != null
        clarificationRepository.findAll().get(0).getTitle() == clarification.title

    }

    def cleanup() {
        persistentCourseCleanup()
        questionAnswerRepository.deleteById(questionAnswer.getId())
        quizAnswerRepository.deleteById(quizAnswer.getId())
        questionRepository.deleteById(question.getId())
        quizRepository.deleteById(quiz.getId())
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
