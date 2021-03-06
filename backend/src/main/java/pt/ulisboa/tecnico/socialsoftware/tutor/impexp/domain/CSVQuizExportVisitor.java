package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVQuizExportVisitor implements Visitor {
    private String[] line;
    private int column;
    private final List<String[]> table = new ArrayList<>();

    public String export(Quiz quiz, List<QuestionAnswerItem> questionAnswerItems) {
        int numberOfQuestions = quiz.getQuizQuestions().size();
        int lineSize = numberOfQuestions + 5;
        Map<Integer, QuizQuestion> quizQuestions = quiz.getQuizQuestions().stream()
                .collect(Collectors.toMap(QuizQuestion::getId, Function.identity()));
        // TODO[is->has]: questionMap XML
        Map<Integer, Option> options = quiz.getQuizQuestions().stream()
                .map(QuizQuestion::getQuestion)
                .map(Question::getQuestionDetails)
                .filter(q -> q instanceof MultipleChoiceQuestion)
                .flatMap(question -> ((MultipleChoiceQuestion) question).getOptions().stream())
                .collect(Collectors.toMap(Option::getId, Function.identity()));

        // add header
        line = new String[lineSize];
        Arrays.fill(line, "");
        line[0] = "Username";
        line[1] = "Name";
        line[2] = "Start";
        line[3] = "Delivered";
        line[4] = "Delay in Seconds";
        column = 4;

        quiz.getQuizQuestions().stream()
                .sorted(Comparator.comparing(QuizQuestion::getSequence))
                .forEach(quizQuestion -> {
                    line[++column] = String.valueOf(quizQuestion.getSequence()+1);
                });

        table.add(line);

        // add student answer
        for (QuizAnswer quizAnswer : quiz.getQuizAnswers()) {
            line = new String[lineSize];
            Arrays.fill(line, "");
            column = 0;
            quizAnswer.getUser().accept(this);

            quizAnswer.accept(this);

            quizAnswer.getQuestionAnswers().stream()
                    .sorted(Comparator.comparing(questionAnswer -> questionAnswer.getQuizQuestion().getSequence()))
                    .collect(Collectors.toList())
                    .forEach(questionAnswer -> questionAnswer.accept(this));

            table.add(line);
        }

        // add key
        line = new String[lineSize];
        Arrays.fill(line, "");
        line[4] = "KEYS";
        column = 5;
        quiz.getQuizQuestions().stream()
                .sorted(Comparator.comparing(QuizQuestion::getSequence))
                .forEach(quizQuestion -> quizQuestion.accept(this));
        table.add(line);

        // add log of question answers
        line = new String[lineSize];
        Arrays.fill(line, "");
        table.add(line);
        line = new String[lineSize];
        Arrays.fill(line, "");
        table.add(line);

        line = new String[lineSize];
        Arrays.fill(line, "");
        line[0] = "Username";
        line[1] = "Question";
        line[2] = "Option";
        line[3] = "Answer Date";
        line[4] = "Time To Submission";
        line[5] = "Time Taken";
        table.add(line);

        Comparator<QuestionAnswerItem> comparator = Comparator.comparing(QuestionAnswerItem::getUsername);
        comparator.thenComparing(Comparator.comparing(QuestionAnswerItem::getAnswerDate));
        questionAnswerItems.sort(comparator);

        for (QuestionAnswerItem questionAnswerItem : questionAnswerItems) {
            line = new String[lineSize];
            Arrays.fill(line, "");
            line[0] = questionAnswerItem.getUsername();
            line[1] = String.valueOf(quizQuestions.get(questionAnswerItem.getQuizQuestionId()).getSequence()+1);
            line[2] = questionAnswerItem.getAnswerRepresentation(options);
            line[3] = DateHandler.toISOString(questionAnswerItem.getAnswerDate());
            line[4] = questionAnswerItem.getTimeToSubmission() != null ? convertMiliseconds(questionAnswerItem.getTimeToSubmission()) : "";
            line[5] = convertMiliseconds(questionAnswerItem.getTimeTaken());
            table.add(line);
        }

        return table.stream().map(this::convertToCSV).collect(Collectors.joining("\n"));
    }

    @Override
    public void visitUser(User user) {
        line[column++] = user.getUsername() != null ? user.getUsername() : "";
        line[column++] = user.getName() != null ? user.getName() : "";
    }

    @Override
    public void visitQuizAnswer(QuizAnswer quizAnswer) {
        line[column++] = quizAnswer.getCreationDate() != null ? DateHandler.toISOString(quizAnswer.getCreationDate()) : "";
        line[column++] = quizAnswer.getAnswerDate() != null ? DateHandler.toISOString(quizAnswer.getAnswerDate()) : "";
        if (quizAnswer.getAnswerDate() != null &&
                quizAnswer.getQuiz().getConclusionDate() != null &&
                quizAnswer.getAnswerDate().isAfter(quizAnswer.getQuiz().getConclusionDate())) {
            Duration duration = Duration.between(quizAnswer.getAnswerDate(), quizAnswer.getQuiz().getConclusionDate());
            line[column++] = String.valueOf(Math.abs(duration.toSeconds()));
        } else
            line[column++] = "";
    }

    @Override
    public void visitQuestionAnswer(QuestionAnswer questionAnswer) {
        if (questionAnswer.getAnswerDetails() != null) {
            questionAnswer.getAnswerDetails().accept(this);
        }
    }

    @Override
    public void visitAnswerDetails(MultipleChoiceAnswer answer) {
        line[column++] = answer.getAnswerRepresentation();
    }

    @Override
    public void visitAnswerDetails(CodeFillInAnswer answer) {
        line[column++] = answer.getAnswerRepresentation();
    }

    @Override
    public void visitQuizQuestion(QuizQuestion quizQuestion) {
        quizQuestion.getQuestion().accept(this);
    }

    @Override
    public void visitQuestion(Question question) {
        question.getQuestionDetails().accept(this);
    }

    @Override
    public void visitQuestionDetails(MultipleChoiceQuestion question) {
        line[column++] = question.getCorrectAnswerRepresentation();
    }

    @Override
    public void visitQuestionDetails(CodeFillInQuestion question) {
        line[column++] = question.getCorrectAnswerRepresentation();
    }

    private String convertToCSV(String[] data) {
        return String.join(",", data);
    }

    private String convertMiliseconds(int millis) {
       return  String.format("%02d:%02d:%02d",
               TimeUnit.MILLISECONDS.toHours(millis),
               TimeUnit.MILLISECONDS.toMinutes(millis)  -
                       TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }


}
