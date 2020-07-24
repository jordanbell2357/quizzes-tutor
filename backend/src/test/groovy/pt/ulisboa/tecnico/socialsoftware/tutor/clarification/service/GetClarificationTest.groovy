package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.DiscussionEntry
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetClarificationTest extends SpockTest {
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

        def clarification = new Clarification()
        clarification.setQuestionAnswer(questionAnswer)
        clarification.setTitle(CLARIFICATION_1_TITLE)
        clarification.setId(CLARIFICATION_1_ID)
        clarification.setUser(userRepository.findAll().get(0))
        clarificationRepository.save(clarification)
    }

    def 'Getting an existing clarification, with no discussionEntry'(){
        given: 'a clarification'
        def id = clarificationRepository.findAll().get(0).getId()

        when:
        def clarificationDto = clarificationService.getClarification(id)

        then:
        clarificationDto.id == id
        clarificationDto.title == CLARIFICATION_1_TITLE
        clarificationDto.discussionEntryDtoList.size() == 0
    }

    def 'Getting an existing clarification, with discussionEntry'(){
        given: 'a discussionEntry'
        def clarificationL = clarificationRepository.findAll().get(0)
        def discussionEntry = new DiscussionEntry()
        def user = userRepository.findAll().get(0)
        discussionEntry.setId(DISCUSSION_ENTRY_1_ID)
        discussionEntry.setMessage(DISCUSSION_1_MESSAGE)
        discussionEntry.setUser(user)
        clarificationL.addDiscussionEntry(discussionEntry)
        discussionEntry.setClarification(clarificationL)
        discussionEntryRepository.save(discussionEntry)

        when:
        def clarificationDto = clarificationService.getClarification(clarificationL.getId())

        then:
        clarificationDto.id == clarificationL.getId()
        clarificationDto.title == CLARIFICATION_1_TITLE
        clarificationDto.discussionEntryDtoList.size() == 1
        clarificationDto.discussionEntryDtoList.get(0).getId() == DISCUSSION_ENTRY_1_ID
        clarificationDto.discussionEntryDtoList.get(0).getMessage() == DISCUSSION_1_MESSAGE
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