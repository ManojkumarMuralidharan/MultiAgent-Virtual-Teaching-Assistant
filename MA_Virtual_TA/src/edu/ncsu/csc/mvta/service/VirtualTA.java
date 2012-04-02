package edu.ncsu.csc.mvta.service;

import android.view.View;
import edu.ncsu.csc.mvta.data.Answer;
import edu.ncsu.csc.mvta.data.Question;
import edu.ncsu.csc.mvta.jade.VTAgent;

/**
 * The lifecycle of the vTA is as follows:
 * 
 * 1) Instantiated by the exam service when the exam service is created
 * 2) Exam service requests a question from the vTA -- getNextQuestion()
 * 3) The question is presented to the user and the user answers
 * 4) The vTA is given the answer and asked if stats should be displayed -- displayStatistics(...)
 * 5) The vTA is given the chance to present feedback from the user -- setupConfigurableContent(...)
 * 6) The vTA is given the chance to collect feedback from the user -- receiveFeedback(...)
 * 7) Step 2-6 is repeated until all practice questions have been given and answered
 *
 */
public class VirtualTA {

    private ExamService examService;
    private QuestionService questionService;
    private VTAgent vtAgent;
    
    public VirtualTA(ExamService examService, QuestionService questionService) {
        this.examService = examService;
        this.questionService = questionService;
    }
    
    /**
     * Called by the exam service when it needs the next practice question to
     * present the user.
     * 
     * @return the question selected by the virtual TA
     */
    public Question nextQuestion() {
        return questionService.randomQuestion();
    }
    
    /**
     * Used to determine if the national statistics should be displayed to the
     * user when showing the user both their answer and the correct answer to
     * the question that was last returned by getNextQuestion().
     * 
     * @param answer to the last question posed by the virtual TA
     * @return true if the statistics are to be displayed to the user; false otherwise
     */
    public boolean displayStatistics(Answer answer) {
        return true;
    }
    
    /**
     * Called prior to displaying the correct answer to allow the vTA to setup an
     * UI widgets needed to gather user feedback
     * 
     * @param parentView the view that contains all content defined in vta_answer.xml
     */
    public void setupConfigurableContent(View parentView) {
        
    }
    
    /**
     * This method is used to gather any feedback left by the user before they 
     * proceed to the next question.
     * 
     * Called after setupConfigurableContent(...) and before getNextQuestion().
     * 
     * @param parentView the view that contains all content defined in vta_answer.xml
     */
    public void receiveFeedback(View parentView) {
        
    }
}
