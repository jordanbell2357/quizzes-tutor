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
class GetDiscussionEntryTest extends SpockTest {
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
        clarification.setUser(userRepository.findAll().get(0))
        clarificationRepository.save(clarification)
    }

    def 'get a DiscussionEntry' () {
        given: 'a DiscussionEntry'
        DiscussionEntry discussionEntry = new DiscussionEntry()
        discussionEntry.setId(DISCUSSION_ENTRY_1_ID)
        discussionEntry.setUser(userRepository.findAll().get(0))
        def clarification = clarificationRepository.findAll().get(0)
        discussionEntry.setClarification(clarification)
        discussionEntry.setMessage(DISCUSSION_1_MESSAGE)
        clarification.addDiscussionEntry(discussionEntry)
        discussionEntry.setClarification(clarification)

        when:
        def discussionEntries = clarificationService.getDiscussionEntries(clarification.getId())

        then:
        discussionEntries.size() == 1
        def disc = discussionEntries.get(0)
        disc.getId() == DISCUSSION_ENTRY_1_ID
        disc.getMessage() == DISCUSSION_1_MESSAGE
    }

    def 'A Clarification with no discussionEntry'() {
        given: 'A clarification'
        def clarification = clarificationRepository.findAll().get(0)

        when:
        def discussionEntries = clarificationService.getDiscussionEntries(clarification.getId())

        then: 'it is empty'
        discussionEntries.size() == 0
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
