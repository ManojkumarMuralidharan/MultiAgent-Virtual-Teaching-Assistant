package edu.ncsu.csc.mvta.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Answer implements Serializable {

    public int questionId;
    public String answer;
    
    public String toString() {
        return "questionId: " + questionId + "\n" +
               "answer:" + answer;
    }
    
}
