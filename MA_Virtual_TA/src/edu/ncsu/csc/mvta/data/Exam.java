package edu.ncsu.csc.mvta.data;

import java.io.Serializable;
import java.util.Date;

import edu.ncsu.csc.mvta.service.ExamService;

@SuppressWarnings("serial")
public class Exam implements Serializable {

    public Answer[] answers = new Answer[ExamService.T0TAL_QUESTION_COUNT];
    public int currentQuestion = 0;
    public double finalScore = 0.0;
    public double averageScore = 0.0;
    
    public Date completionTime;

    public void addAnswer(Answer answer) {
        answers[currentQuestion] = answer;
        currentQuestion++;
    }
    
    public boolean isComplete() {
        return currentQuestion >= ExamService.T0TAL_QUESTION_COUNT;
    }
    
    public String toString() {
        return "currentQuestion:" + currentQuestion + "\n" +
               "finalScore:" + finalScore + "\n" +
               "averageScore:" + averageScore + "\n" +
               "answers: " + answers;   
    }
    
}
