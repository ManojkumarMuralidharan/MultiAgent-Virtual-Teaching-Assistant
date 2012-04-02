package edu.ncsu.csc.mvta.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Question implements Serializable {

    public int id;
    
    public String questionURL;
    public String answer;
    public double avgCorrect;
    
    public Grade gradeLevel;
    public ContentArea contentArea;
    public Difficulty difficulty;

    
    public enum Grade {
        GRADE_08,
        GRADE_12;
    }
    
    public enum ContentArea {
        PROPERTIES_AND_OPERATIONS,
        GEOMETRY,
        ANALYSIS_AND_PROBABILITY,
        ALGEBRA;
    }
    
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD;
    }
    
    public Question(int id, String questionURL, String answer, double avgCorrect,
            Grade grade, ContentArea area) {
        this.id = id;
        this.questionURL = questionURL;
        this.answer = answer;
        this.avgCorrect = avgCorrect;
        this.gradeLevel = grade;
        this.contentArea = area;
        
        if(avgCorrect >= .6)
            this.difficulty = Difficulty.EASY;
        else if(avgCorrect >= .4)
            this.difficulty = Difficulty.MEDIUM;
        else
            this.difficulty = Difficulty.HARD;
    }
    
}
