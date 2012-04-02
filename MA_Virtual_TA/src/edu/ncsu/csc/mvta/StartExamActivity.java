package edu.ncsu.csc.mvta;

import java.text.SimpleDateFormat;
import java.util.List;

import edu.ncsu.csc.mvta.data.Exam;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StartExamActivity extends ExamActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start);
        
        /* NOTE: since this is our initial entry point to the application the 
         * examService may not be bound at the time this method is called. We
         * therefore wait until examServiceIsReady() to do any setup requiring
         * that service.
         */
    }
    
    @Override
    public void onResume() {
        super.onResume();
        examServiceHandler.sendEmptyMessage(0);
    }
    
    public void examServiceIsReady() {
        
        
        // fill in the recent exams table
        TableLayout recentExamsTable = (TableLayout)findViewById(R.id.RecentExamsTable);

        // clear out the table but keep the header
        TableRow recentExamHeader = (TableRow)findViewById(R.id.RecentExamHeader);
        recentExamsTable.removeAllViews();
        recentExamsTable.addView(recentExamHeader);
        
        List<Exam> recentExams = examService.getPreviousExams(5);
        for(int x = 0; x < recentExams.size(); x++) {
            
            Exam exam = recentExams.get(x);
            
            TableRow row = new TableRow(this);
            row.setPadding(0, 4, 0, 0);
            row.setFocusable(true);
            row.setClickable(true);
            row.setBackgroundResource(R.color.recent_exams);

            TextView number = new TextView(this);
            number.setMaxLines(1);
            number.setMinWidth(25);
            number.setText((x+1) + "");
            
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            
            TextView date = new TextView(this);
            date.setMaxLines(1);
            date.setMinWidth(100);
            date.setText(formatter.format(exam.completionTime));
            
            TextView score = new TextView(this);
            score.setMaxLines(1);
            score.setMinWidth(50);
            score.setText(percentageFormatter.format(exam.finalScore));
            
            TextView avgScore = new TextView(this);
            avgScore.setMaxLines(1);
            avgScore.setMinWidth(50);
            avgScore.setText(percentageFormatter.format(exam.averageScore));
            
            row.addView(number);
            row.addView(date);
            row.addView(score);
            row.addView(avgScore);
            recentExamsTable.addView(row);
            
        }
        
        // setup the start button
        Button start = (Button)this.findViewById(R.id.StartExamButton);
        if(examService.isActiveExam())
            start.setText("Resume");
        start.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                // fire an intent to start the exam activity...
                Context context = view.getContext();
                Intent intent = new Intent(context, ActiveExam.class);
                context.startActivity(intent);
            }
        });
        
        // setup the quit button
        Button quit = (Button)this.findViewById(R.id.QuitExamButton);
        quit.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                // if an exam is in progress then quit the exam and close the activity
                examService.quitExam();
                finish();
            }
        });
    }
}