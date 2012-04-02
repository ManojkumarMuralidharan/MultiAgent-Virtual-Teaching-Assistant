package edu.ncsu.csc.mvta.service;

import java.util.ArrayList;

import edu.ncsu.csc.mvta.data.Question;
import edu.ncsu.csc.mvta.data.Question.ContentArea;
import edu.ncsu.csc.mvta.data.Question.Difficulty;
import edu.ncsu.csc.mvta.data.Question.Grade;

public class QuestionService {
    
    private final static String URL_PREFIX = "file:///android_asset/question_";
    
    ArrayList<Question> questions = new ArrayList<Question>();
    
    public QuestionService() {
        questions.add(new Question( 1, URL_PREFIX + "01.html", "B", 0.8387, Grade.GRADE_08, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question( 2, URL_PREFIX + "02.html", "D", 0.7679, Grade.GRADE_08, ContentArea.ANALYSIS_AND_PROBABILITY));
        questions.add(new Question( 3, URL_PREFIX + "03.html", "E", 0.6837, Grade.GRADE_08, ContentArea.ALGEBRA));
        questions.add(new Question( 4, URL_PREFIX + "04.html", "B", 0.7250, Grade.GRADE_08, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question( 5, URL_PREFIX + "05.html", "B", 0.6708, Grade.GRADE_08, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question( 6, URL_PREFIX + "06.html", "C", 0.4006, Grade.GRADE_08, ContentArea.GEOMETRY));
        questions.add(new Question( 7, URL_PREFIX + "07.html", "E", 0.5316, Grade.GRADE_08, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question( 8, URL_PREFIX + "08.html", "D", 0.5053, Grade.GRADE_08, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question( 9, URL_PREFIX + "09.html", "D", 0.4656, Grade.GRADE_08, ContentArea.ALGEBRA));
        questions.add(new Question(10, URL_PREFIX + "10.html", "D", 0.4015, Grade.GRADE_08, ContentArea.ANALYSIS_AND_PROBABILITY));
        questions.add(new Question(11, URL_PREFIX + "11.html", "A", 0.3646, Grade.GRADE_08, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question(12, URL_PREFIX + "12.html", "C", 0.1813, Grade.GRADE_08, ContentArea.ANALYSIS_AND_PROBABILITY));
        questions.add(new Question(13, URL_PREFIX + "13.html", "E", 0.3665, Grade.GRADE_08, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question(14, URL_PREFIX + "14.html", "E", 0.2486, Grade.GRADE_08, ContentArea.GEOMETRY));
        questions.add(new Question(15, URL_PREFIX + "15.html", "D", 0.2956, Grade.GRADE_08, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question(16, URL_PREFIX + "16.html", "C", 0.6647, Grade.GRADE_12, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question(17, URL_PREFIX + "17.html", "C", 0.6408, Grade.GRADE_12, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question(18, URL_PREFIX + "18.html", "E", 0.8475, Grade.GRADE_12, ContentArea.GEOMETRY));
        questions.add(new Question(19, URL_PREFIX + "19.html", "B", 0.7333, Grade.GRADE_12, ContentArea.GEOMETRY));
        questions.add(new Question(20, URL_PREFIX + "20.html", "A", 0.6009, Grade.GRADE_12, ContentArea.ANALYSIS_AND_PROBABILITY));
        questions.add(new Question(21, URL_PREFIX + "21.html", "A", 0.5468, Grade.GRADE_12, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question(22, URL_PREFIX + "22.html", "C", 0.4835, Grade.GRADE_12, ContentArea.ALGEBRA));
        questions.add(new Question(23, URL_PREFIX + "23.html", "A", 0.4850, Grade.GRADE_12, ContentArea.GEOMETRY));
        questions.add(new Question(24, URL_PREFIX + "24.html", "D", 0.4799, Grade.GRADE_12, ContentArea.ALGEBRA));
        questions.add(new Question(25, URL_PREFIX + "25.html", "C", 0.4812, Grade.GRADE_12, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question(26, URL_PREFIX + "26.html", "D", 0.3084, Grade.GRADE_12, ContentArea.ALGEBRA));
        questions.add(new Question(27, URL_PREFIX + "27.html", "D", 0.2406, Grade.GRADE_12, ContentArea.ANALYSIS_AND_PROBABILITY));
        questions.add(new Question(28, URL_PREFIX + "28.html", "E", 0.2521, Grade.GRADE_12, ContentArea.PROPERTIES_AND_OPERATIONS));
        questions.add(new Question(29, URL_PREFIX + "29.html", "D", 0.3006, Grade.GRADE_12, ContentArea.ALGEBRA));
        questions.add(new Question(30, URL_PREFIX + "30.html", "D", 0.2361, Grade.GRADE_12, ContentArea.ANALYSIS_AND_PROBABILITY));   
    }
    
    public Question getQuestion(int questionID) {
        if(questionID < 1 || questionID > questions.size())
            return null;
        else
            return questions.get(questionID - 1);
    }
    
    public Question randomQuestion() {
        int position = (int)(Math.random() * questions.size());
        return questions.get(position);
    }
    
    public Question randomQuestion(Grade grade, Difficulty difficulty) {
        
        ArrayList<Question> validQuestions = new ArrayList<Question>();
        
        for(Question q : questions) {
            if(q.gradeLevel == grade && q.difficulty == difficulty)
                validQuestions.add(q);
        }
        
        if(validQuestions.size() == 0)
            return null;
        
        int position = (int)(Math.random() * validQuestions.size());
        return validQuestions.get(position);
    }
    
    /* START STUDENT CODE */
    
    // add any methods you want to retrieve specific types of questions here
    
    /* END STUDENT CODE */
}
