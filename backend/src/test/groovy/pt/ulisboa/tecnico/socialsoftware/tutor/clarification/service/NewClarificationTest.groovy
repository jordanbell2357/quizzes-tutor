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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class NewClarificationTest extends SpockTest {
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

        when:
        def clarificationDto = clarificationService.newClarification(discussionEntry, clarification)

        then:
        clarificationDto.id == clarification.getId()
        clarificationDto.discussionEntryDtoList.size() == 1
        clarificationDto.discussionEntryDtoList.get(0).message == DISCUSSION_1_MESSAGE
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
