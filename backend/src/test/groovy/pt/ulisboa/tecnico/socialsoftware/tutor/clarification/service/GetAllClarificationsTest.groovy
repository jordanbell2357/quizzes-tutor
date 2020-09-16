package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetAllClarificationsTest extends SpockTest {
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

        def quiz1 = new Quiz()
        quiz1.setKey(1)
        quiz1.setTitle("Quiz Title")
        quiz1.setType(Quiz.QuizType.PROPOSED.toString())
        quiz1.setCourseExecution(courseExecution)
        quiz1.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz1)

        def question = new Question()
        question.setKey(2)
        question.setTitle("Question Title 1")
        question.setCourse(course)
        questionRepository.save(question)

        def question1 = new Question()
        question1.setKey(2)
        question1.setTitle("Question Title 2")
        question1.setCourse(course)
        questionRepository.save(question1)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        def quizQuestion1 = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion1)

        quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)

        def quizAnswer1 = new QuizAnswer(user, quiz1)
        quizAnswerRepository.save(quizAnswer1)

        questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)

        def questionAnswer1 = new QuestionAnswer()
        questionAnswer1.setQuizQuestion(quizQuestion1)
        questionAnswer1.setQuizAnswer(quizAnswer1)
        questionAnswerRepository.save(questionAnswer1)
    }

    def 'Getting an existing clarification'(){
        given: 'a clarification and a questionAnswer'
        def clarification = new Clarification()
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        clarification.setQuestionAnswer(questionAnswer)
        clarification.setTitle(CLARIFICATION_1_TITLE)
        questionAnswer.addClarification(clarification)
        clarification.setUser(userRepository.findAll().get(0))
        clarificationRepository.save(clarification)

        and: 'and another one'
        def clarification1 = new Clarification()
        def questionAnswer1 = questionAnswerRepository.findAll().get(1)
        clarification1.setQuestionAnswer(questionAnswer1)
        clarification1.setTitle(CLARIFICATION_2_TITLE)
        questionAnswer1.addClarification(clarification1)
        questionAnswer1.addClarification(clarification1)
        clarification1.setUser(userRepository.findAll().get(0))
        clarificationRepository.save(clarification1)


        when:
        def clarificationDtos = clarificationService.getAllClarifications(courseExecution.getId())

        then:
        clarificationDtos.size() == 2

        clarificationDtos.get(0).getTitle() == CLARIFICATION_1_TITLE
        clarificationDtos.get(0).getQuestionAnswerId() == questionAnswer.getId()

        clarificationDtos.get(1).getTitle() == CLARIFICATION_2_TITLE
        clarificationDtos.get(1).getQuestionAnswerId() == questionAnswer1.getId()
    }

    def 'Two clarifications sorted by title'(){
        given: 'two clarifications and a questionAnswer'
        def clarification1 = new Clarification()
        clarification1.setTitle(CLARIFICATION_1_TITLE)
        clarification1.setUser(userRepository.findAll().get(0))

        def clarification2 = new Clarification()
        clarification2.setTitle(CLARIFICATION_2_TITLE)
        clarification2.setUser(userRepository.findAll().get(0))

        def questionAnswer = questionAnswerRepository.findAll().get(0)

        and: 'Saving the clarification on the questionAnswer'
        clarification1.setQuestionAnswer(questionAnswer)
        clarification2.setQuestionAnswer(questionAnswer)
        questionAnswer.addClarification(clarification2)
        questionAnswer.addClarification(clarification1)

        clarificationRepository.save(clarification1)
        clarificationRepository.save(clarification2)

        when:
        def clarificationDtos = clarificationService.getAllClarifications(courseExecution.getId())

        then:
        clarificationDtos.size() == 2
        def clarificationX = clarificationDtos.get(0)
        def clarificationY = clarificationDtos.get(1)

        clarificationX.getTitle() == CLARIFICATION_1_TITLE
        clarificationY.getTitle() == CLARIFICATION_2_TITLE
    }

    def 'Getting an non-existing clarification'(){
        when:
        def clarificationDtos = clarificationService.getAllClarifications(courseExecution.getId())
        then:
        clarificationDtos.size() == 0
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}