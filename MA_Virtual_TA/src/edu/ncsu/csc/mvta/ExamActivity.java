package edu.ncsu.csc.mvta;

import java.text.DecimalFormat;

import edu.ncsu.csc.mvta.service.ExamService;
import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class ExamActivity extends Activity {

    protected Handler examServiceHandler;
    protected ExamService examService;
    
    protected final DecimalFormat percentageFormatter = new DecimalFormat("#0.00%");
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // default the screen orientation to landscape mode
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        // store a copy of the exam service locally
        examService = ((ExamApplication)this.getApplication()).getExamService();
        
        examServiceHandler = new Handler() {
            public void handleMessage(Message msg) {
                
                Application app = ExamActivity.this.getApplication();
                examService = ((ExamApplication)app).getExamService();
                
                if(examService == null)
                    this.sendEmptyMessage(0);
                else
                    examServiceIsReady();
            }
        };
        examServiceHandler.sendEmptyMessage(0);
    }
    
    /** intended to be overridden */
    public void examServiceIsReady() { }
}