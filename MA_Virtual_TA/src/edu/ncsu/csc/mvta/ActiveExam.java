package edu.ncsu.csc.mvta;

import edu.ncsu.csc.mvta.data.Answer;
import edu.ncsu.csc.mvta.data.Question;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActiveExam extends ExamActivity {
    
    Question currentQuestion;
    int currentQuestionNumber;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askQuestion();
        //
    }
    
    public void askQuestion() {
        
        setContentView(R.layout.question);
        
        currentQuestion = examService.getNextQuestion();
        currentQuestionNumber = examService.getQuestionNumber();
        
        // set the header
        TextView questionNum = (TextView)findViewById(R.id.QuestionNumber);
        questionNum.setText("Question #" + currentQuestionNumber);        
        
        // fill in the question
        WebView questionView = (WebView) findViewById(R.id.QuestionView);

        WebSettings webSettings = questionView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        questionView.loadUrl(currentQuestion.questionURL);
        
        // set click listeners for the buttons
        final RadioGroup choices = (RadioGroup)this.findViewById(R.id.AnswerRadioGroup);
        Button answerButton = (Button)this.findViewById(R.id.AnswerButton);
        answerButton.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                switch(choices.getCheckedRadioButtonId()) {
                    case R.id.AnswerA:
                        answerQuestion("A");
                        break;
                    case R.id.AnswerB:
                        answerQuestion("B");
                        break;
                    case R.id.AnswerC:
                        answerQuestion("C");
                        break;
                    case R.id.AnswerD:
                        answerQuestion("D");
                        break;
                    case R.id.AnswerE:
                        answerQuestion("E");
                        break;
                    default:
                        Toast.makeText(choices.getContext(), "Please Select an Answer.", 2);
                }
            }
        });
    }
    
    public void answerQuestion(String answerValue) {
        
        //construct the answer, but store it later in the method
        Answer answer = new Answer();
        answer.questionId = currentQuestion.id;
        answer.answer = answerValue;

        
        // show the view
        setContentView(R.layout.answer);

        // set the header
        TextView questionNum = (TextView)findViewById(R.id.QuestionNumberResult);
        questionNum.setText("Question #" + currentQuestionNumber + " (Result)");
        
        // show their answer
        TextView yourAnswer = (TextView)findViewById(R.id.YourAnswer);
        yourAnswer.setText(answerValue);
        
        // show the correct answer
        TextView correctAnswer = (TextView)findViewById(R.id.CorrectAnswer);
        correctAnswer.setText(currentQuestion.answer);
        
        //  ask vTA if it is a question they provided and always show if testing
        if(examService.isTestingQuestion() || examService.getVirtualTA().displayStatistics(answer)) {

            TextView nationalAverage = (TextView)findViewById(R.id.NationalAverage);
            nationalAverage.setText(percentageFormatter.format(currentQuestion.avgCorrect));
            
            TextView difficulty = (TextView)findViewById(R.id.QuestionDifficulty);
            difficulty.setText(currentQuestion.difficulty.toString());
        } else {
            
            TableLayout statsTable = (TableLayout)findViewById(R.id.StatisticsTable);
            statsTable.setVisibility(View.GONE);
            
            TextView statsView = (TextView)findViewById(R.id.StatisticsUnavailable);
            statsView.setVisibility(View.VISIBLE);
        }
        
        View taContent = (View)findViewById(R.id.TAContent);
        if(examService.isTestingQuestion()) {
            
            taContent.setVisibility(View.GONE);
            
            TextView statsView = (TextView)findViewById(R.id.TAContentUnavailable);
            statsView.setVisibility(View.VISIBLE);
        } else {
            examService.getVirtualTA().setupConfigurableContent(taContent);
        }
               
        
        // store the answer
        examService.answerQuestion(answer);
        
        Button nextButton = (Button)this.findViewById(R.id.NextButton);
        // if the exam is complete then button will show results otherwise ask next question
        if(examService.isActiveExam()) {
            nextButton.setOnClickListener(new OnClickListener() {

                public void onClick(View view) {
                    
                    if(!examService.isTestingQuestion()) {
                        // take any feedback from the user based on the content
                        // configured by the vTA
                        View taContent = (View)findViewById(R.id.TAContent);
                        examService.getVirtualTA().receiveFeedback(taContent);
                    }
                    
                    askQuestion();
                }
            });
        } else {
            nextButton.setText("Exam Complete! Return Home To View Your Results.");
            nextButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }
}