package edu.ncsu.csc.mvta.service;


import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.util.leap.HashMap;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.derekandbritt.koko.emotion.EmotionType;
import com.derekandbritt.koko.emotion.EmotionVector;

import android.app.Application;
import android.content.Context;
import android.nfc.Tag;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import edu.ncsu.csc.mvta.ExamActivity;
import edu.ncsu.csc.mvta.LocationActivity;
import edu.ncsu.csc.mvta.R;
import edu.ncsu.csc.mvta.data.Answer;
import edu.ncsu.csc.mvta.data.Exam;
import edu.ncsu.csc.mvta.data.Question;
import edu.ncsu.csc.mvta.jade.MessageListener;
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

	private static final String LOG_TAG = "VirtualTA";
    private ExamService examService;
    private QuestionService questionService;
    private VTAgent vtAgent;
    //private LocationActivity locationActivity;
    private LocationActivity locationActivity;
    private KokoService koko;
    private boolean remoteQuestionAvailble;
    private int remoteReceivedId;
    
    /*
     * 
     */
    int count=0;
    int QuestionsAnsweredData[][][];
    int QuestionsAskedData[][][];
    
    //double GradePercentage[];
    //int GradeQuestionsTotal[];
    int GradeProbablity[];
    
    //double ContentAreaPercentage[];
    //int ContentQuestionsTotal[];
    int ContentAreaProbablity[];
    
    //double DifficultyPercentage[];
    //int DifficultyQuestionsTotal[];
    int DifficultyProbablity[];
    Context appContext;
    
    public VirtualTA(final ExamService examService, QuestionService questionService) {
        this.examService = examService;
        this.questionService = questionService;
        //Creates an array to store Correct and wrong answers provided by user
        QuestionsAnsweredData=new int[Question.Grade.values().length][Question.ContentArea.values().length][Question.Difficulty.values().length];
        //Stores total of all questions asked data
        QuestionsAskedData=new int[Question.Grade.values().length][Question.ContentArea.values().length][Question.Difficulty.values().length];
        
      //  GradePercentage=new double[Question.Grade.values().length];
      //  GradeQuestionsTotal=new int[Question.Grade.values().length];
     //   ContentAreaPercentage=new double[Question.ContentArea.values().length];
     //   ContentQuestionsTotal=new int[Question.ContentArea.values().length];
     //   DifficultyPercentage=new double[Question.Difficulty.values().length];
     //   DifficultyQuestionsTotal= new int[Question.Difficulty.values().length];
        
        GradeProbablity=new int[Question.Grade.values().length];
        ContentAreaProbablity=new int[Question.ContentArea.values().length];
        DifficultyProbablity=new int[Question.Difficulty.values().length];
        
        initializeProbablity(GradeProbablity);
        initializeProbablity(ContentAreaProbablity);
        initializeProbablity(DifficultyProbablity);
       
        this.setKoko(new KokoService());
        Timer checkQuestionPool = new Timer();
        checkQuestionPool.schedule( new TimerTask() {
    	
    	
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//Check the message queue
			if(MessageListener.incomingRequest.size()>0){
				//Process the request
				ArrayList<Integer> remoteQuestionId=new ArrayList<Integer>();
				Iterator<ACLMessage> it = MessageListener.incomingRequest.iterator();
				while (it.hasNext()) {
				//System.out.println(it.next());
				ACLMessage message=(ACLMessage)it.next();
				it.remove();
				MessageListener.incomingRequest.remove(message);
				String parameters[]=new String[3];
				parameters=message.getContent().toString().split(",");
					
				QuestionService ques_service=new QuestionService();
				
				int grade_index=MessageListener.getIndexOfEnum("grade",parameters[0]);
				int difficulty_index=MessageListener.getIndexOfEnum("difficulty",parameters[1]);
				int con_Area_index=MessageListener.getIndexOfEnum("contentArea",parameters[2]);
				
				Question remoteQuestion=ques_service.randomQuestion(Question.Grade.values()[grade_index], Question.Difficulty.values()[difficulty_index], Question.ContentArea.values()[con_Area_index]);
				if(remoteQuestion==null){
					remoteQuestion=ques_service.randomQuestion(Question.Grade.values()[grade_index], Question.Difficulty.values()[difficulty_index], Question.ContentArea.values()[con_Area_index]);
				}
				//Send a message - content Question ID
				if(remoteQuestion==null){
					remoteQuestion=ques_service.randomQuestion();
				}
				remoteQuestionId.add(remoteQuestion.id);
				AID senderAID=new AID(message.getSender().getName(),true);
				Log.d(LOG_TAG, "Sender name"+message.getSender().getName());
			    //Send them a question id
				examService.getVTAgetnt().sendMessage(""+remoteQuestion.id, "INFORM",senderAID);
				}
				
				
				}
				if(MessageListener.SuggestedQuestionId.size()>0){
					Iterator<ACLMessage> suggestedQuestionIterator = MessageListener.SuggestedQuestionId.iterator();
					
					//Delete the questions already asked
					while(suggestedQuestionIterator.hasNext()){
						
						if(examService.isQuestionUsed(Integer.parseInt(suggestedQuestionIterator.next().getContent()))){
							suggestedQuestionIterator.remove();
						}
					}
					
					//Choose a question id from a list of question id from remaining
					
					int listSize=MessageListener.SuggestedQuestionId.size();
					if(listSize>0){
					ACLMessage chosenQuestion=MessageListener.SuggestedQuestionId.get((int)(Math.random()*listSize));
					Log.d(LOG_TAG, "Chosen Question no. is"+chosenQuestion.getContent());
					setRemoteQuestionAvailble(true);
					setRemoteReceivedId(Integer.parseInt(chosenQuestion.getContent()));
					
					}
					
					//
					
					//remove all messages
					MessageListener.SuggestedQuestionId=new ArrayList<ACLMessage>(); 
				}
				
			}
			
        	}, 0, 4000);
        //Initializa GPS
//        locationActivity.initializeLocation();
        /*
        LocationManager locationManager = (LocationManager) examService.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String provider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null)
          updateWithNewLocation(location);
        
        //ArrayList<Exam> examList=(ArrayList<Exam>) examService.getPreviousExams(10);
       // populateExamKnowledge(examList);
        //Log.d("Virtual TA",""+Question.Grade.valueOf(""+Question.Grade.GRADE_08));
        
      //  Log.d("Virtual TA",Question.Grade.valueOf(question.gradeLevel.toString()));
     */   
    }
   
    public Context getAppContext() {
		return appContext;
	}

	public void setAppContext(Context appContext) {
		this.appContext = appContext;
	}

	void initializeProbablity(int[] inputArray){
    	int probablity= 1;
    	for(int i=0;i<inputArray.length;i++){
    		inputArray[i]=probablity;
    	}
    }
    
       
    int getMaxIndex(int[] inputArray){
    	int index=0;
    	int sum=0;
    	for(int i=0;i<inputArray.length;i++){
    		sum=sum+inputArray[i];
    	}
    	int randomNumber=(int)(Math.random()*sum)+1;
    	int new_sum=0;
    	for(int i=0;i<inputArray.length;i++){
    		new_sum+=inputArray[i];
    		if(randomNumber<=new_sum){
    			index=i;
    			break;
    		}
    	}
    	return index;
    }
    
    void populatePreviousExamData(){
    	ArrayList<Exam> examList=(ArrayList<Exam>) examService.getPreviousExams(10);
    	for(Exam exam: examList){
    		int totalQuestions=exam.answers.length;
    		for(int i=0;i<totalQuestions;i++){
    			int questionId=exam.answers[i].questionId;
    			Answer answer=exam.answers[i];
    			Question question=questionService.getQuestion(questionId);
    			
    			updateProbablity(question, answer);
    			}
    					
    			
    				
    			
    		}
    		
    	}
    
    void updateProbablity(Question question,Answer answer){
    	if(answer.answer.equalsIgnoreCase(question.answer)){
			//	Log.d("Virtual TA","Incrementing in QuestionAnsweredData["+gradeIndex+"]["+contentAreaIndex+"]["+difficultyLevelIndex+"]");
				int grade_index=getIndexOfEnum("grade",question);
				int con_Area_index=getIndexOfEnum("contentArea",question);
				int difficulty_index=getIndexOfEnum("difficulty",question);
				
				//Increase Grade probablity of all other grades
				for(int grade_length=0;grade_length<GradeProbablity.length;grade_length++){
					if(grade_length!=grade_index){
						GradeProbablity[grade_length]+=1;
					}
					
				}
				//Increase Grade probablity of all other contentArea
				for(int conArea_length=0;conArea_length<ContentAreaProbablity.length;conArea_length++){
					if(conArea_length!=con_Area_index){
						ContentAreaProbablity[conArea_length]+=1;
					}
					
				}
				//Increase Grade probablity of all other difficulty
				for(int difficulty_length=0;difficulty_length<DifficultyProbablity.length;difficulty_length++){
					if(difficulty_length!=difficulty_index){
						DifficultyProbablity[difficulty_length]+=1;
					}

				}
				
			}else{
				//Increase value of grade probablity
				GradeProbablity[getIndexOfEnum("grade",question)]+=1;
				
				//Increase value of contentArea probablity
				ContentAreaProbablity[getIndexOfEnum("contentArea", question)]+=1;
				
				//Increase value of difficulty probablity
				DifficultyProbablity[getIndexOfEnum("difficulty", question)]+=1;
				
				}
    	
    }
    
    int getIndexOfEnum(String name,Question question){
    	Question.Grade[] grades;
    	Question.ContentArea[] contentAreas;
    	Question.Difficulty[] difficulties;

    	
    	if(name.equalsIgnoreCase("grade")){
    		grades= Question.Grade.values();
    		for(int j=0;j<grades.length;j++){
    			if(question.gradeLevel==grades[j]){
    				   return j;
    			}
    		}
    	}else if(name.equalsIgnoreCase("contentArea")){
    		contentAreas=Question.ContentArea.values();
    		for(int j=0;j<contentAreas.length;j++){
    			if(question.contentArea==contentAreas[j]){
    				   return j;
    			}
    		}
    	}else if(name.equalsIgnoreCase("difficulty")){
    		difficulties=Question.Difficulty.values();
    		for(int j=0;j<difficulties.length;j++){
    			if(question.difficulty==difficulties[j]){
    				   return j;
    			}
    		}
    	}else{
    		Log.d("Virtual TA","Virtual TA.java:getIndexOfEnum() Should not come here");
    	return -1;
    	}
    	Log.d("Virtual TA","Virtual TA.java:getIndexOfEnum() Should not come here");
		return -1;
		
		
    }
    
    /**
     * Called by the exam service when it needs the next practice question to
     * present the user.
     * 
     * @return the question selected by the virtual TA
     */
    public Question nextQuestion(Context context) {
    	int grade;
    	int conArea;
    	int difficulty;
    	
    	
    	
    	
    	 if(context!=null){
    	 this.setAppContext(context);
    	 locationActivity=new LocationActivity(this.appContext);
    	 locationActivity.initializeLocation();
    	 }
    	 else
    	 Log.d("Virtual TA", "VirtualTA.java: Context not available for location");
    	
    	
    	if(count==0){
    		count++;
    		 populatePreviousExamData();
    	}else{
     	Exam currentExam=examService.getActiveExam();
     	Answer current_answer=currentExam.answers[currentExam.currentQuestion-1];
     	Question currentQuestion=questionService.getQuestion(current_answer.questionId);
     	updateProbablity(currentQuestion,current_answer);
     	
     	Log.d("Virtual TA", "Check");
    	}
    	Question tempQuestion;
    	//If a person is close to library
    	if(locationActivity.isClose()==1)
    	 tempQuestion = new Question(-1, null, null, 0.6, null, null); // Easy Question
    	else
    	 tempQuestion = new Question(-1, null, null, 0.3, null, null); // Hard Question
    	
    	//DifficultyProbablity[getIndexOfEnum("difficulty", tempQuestion)]+=1;
    	
   	 	//Selecting a grade
    	grade=getMaxIndex(GradeProbablity);
    	//Selecting a ContentArea
   	 	conArea=getMaxIndex(ContentAreaProbablity);
   	 	//Selecting a difficulty
   	 	difficulty=getMaxIndex(DifficultyProbablity);
   	 	
   	 	
   	    examService.getVTAgetnt().sendMessageToAllAgents(Question.Grade.values()[grade].toString()+","+Question.Difficulty.values()[difficulty].toString()+","+Question.ContentArea.values()[conArea].toString(), "REQUEST");
   	    
   	    
   	 	
   	    
   	    
   	 	EmotionVector predictionVector= this.getKoko().invokePredictEmotion(Question.Grade.values()[grade].toString(), Question.ContentArea.values()[conArea].toString(), Question.Difficulty.values()[difficulty].toString());
   	 	
   	 	if(predictionVector.getValue(EmotionType.LIKE)>60||predictionVector.getValue(EmotionType.JOY)>60){
   	 	Log.d("Virtual TA", "VirtualTA.JAVA:nextQuestion() Good Question");
   	 	}else if(predictionVector.getValue(EmotionType.FEAR)>60||predictionVector.getValue(EmotionType.DISLIKE)>60){
   	 		Log.d("Virtual TA", "VirtualTA.JAVA:nextQuestion() Bad question");
   	 		nextQuestion(context);
   	 	}
   	 	
   	 	//Check if A remote question is available
   	 	int num=(int)(Math.random()*10);
   	 	if(num<6){
	   	 	if(isRemoteQuestionAvailble())
	   	 	{
	   	 	setRemoteQuestionAvailble(false);
	   	 	setRemoteReceivedId(-1);
	   	 	}
   	 	}else{
	   	 	if(isRemoteQuestionAvailble())
	   	 	{
	   	 		setRemoteQuestionAvailble(false);
	   	 		int remoteQuestionId=getRemoteReceivedId();
	   	 		setRemoteReceivedId(-1);
	   	 		Question remoteQuestion = questionService.getQuestion(remoteQuestionId);
	   	 		if(remoteQuestion!=null){
	   	 			return remoteQuestion;
	   	 		}else{
	   	 			Log.d(LOG_TAG,"The remote question ID turned up null");
	   	 		}
	   	 		
	   	 	}
   	 	}
   	 	//Plan to chose or to deny
   	 	
   	 	Question nextQuestion;
   	 	//Grade content difficulty
   	 	nextQuestion=questionService.randomQuestion(Question.Grade.values()[grade], Question.Difficulty.values()[difficulty], Question.ContentArea.values()[conArea]);
   	 	if(null!=nextQuestion){
   	 		return nextQuestion;
   	 	}else{
   	 		//Grade Difficulty
   	 		nextQuestion=questionService.randomQuestion(Question.Grade.values()[grade], Question.Difficulty.values()[difficulty]);
   	 		if(null!=nextQuestion){
   	 			return nextQuestion;
   	 		}else{
   	 			//Grade Content Area
   	 			nextQuestion=questionService.randomQuestion(Question.Grade.values()[grade], Question.ContentArea.values()[conArea]);
   	 			if(null!=nextQuestion){
   	 			return nextQuestion;	
   	 			}else{
   	 				//Grade 
   	 				nextQuestion=questionService.randomQuestion(Question.Grade.values()[grade]);
   	 				if(null!=nextQuestion){
   	 					return nextQuestion;
   	 				}else{
   	 					//random Question
   	 					nextQuestion=questionService.randomQuestion();
   	 					return nextQuestion;
   	 				}
   	 			}
   	 			
   	 		}
   	 	}
   	 		
   	 	/*	
   	 	if(null!=questionService.randomQuestion(Question.Grade.values()[grade], Question.Difficulty.values()[difficulty])){
   	 		
   	 	}else if(null!=questionService.randomQuestion(Question.Grade.values()[grade], Question.ContentArea.values()[conArea])){
   	 		
   	 	}else if(null!=questionService.randomQuestion(Question.Grade.values()[grade])){
   	 		
   	 	}else{
   	 	return questionService.randomQuestion();	
   	 	}*/
   	 	

        
    }
    
    /*void populateExamKnowledge(ArrayList<Exam> examList){
    	
    	for(Exam exam: examList){
    		int totalQuestions=exam.answers.length;
    		for(int i=0;i<totalQuestions;i++){
    			int questionId=exam.answers[i].questionId;
    			Answer answer=exam.answers[i];
    			Question question=questionService.getQuestion(questionId);
    			
    			int gradeIndex=-1;
    			int contentAreaIndex=-1;
    			int difficultyLevelIndex=-1;
    			
    			//Hoping that question is error free 
    			//Determine grade, ContentArea and Difficulty Level
    			for(Question.Grade grade: Question.Grade.values()){
    				gradeIndex++;
    				if(question.gradeLevel==grade){
    					Log.d("Vitrual TA","Grade Selected:"+gradeIndex);
    					break;
    				}
    				
    			}
    			
    			for(Question.Difficulty difficulty: Question.Difficulty.values()){
    				difficultyLevelIndex++;
    				if(question.difficulty==difficulty){
    					Log.d("Vitrual TA","Difficulty Selected:"+difficultyLevelIndex );
    					break;
    				}
    				
    			}

				for(Question.ContentArea contentArea: Question.ContentArea.values()){
					contentAreaIndex++;
					if(question.contentArea==contentArea){
						Log.d("Vitrual TA","contentArea Selected:"+contentAreaIndex);
						break;
					}
				}
				//Check for errors
    			if((gradeIndex!=-1&&gradeIndex<Question.Grade.values().length)&&
    			   (difficultyLevelIndex!=-1&&difficultyLevelIndex<Question.Difficulty.values().length)&&
    			   (contentAreaIndex!=-1&&contentAreaIndex<Question.ContentArea.values().length)){
    					QuestionsAskedData[gradeIndex][contentAreaIndex][difficultyLevelIndex]+=1;
    				//	Log.d("Virtual TA","Logging in QuestionAskedData["+gradeIndex+"]["+contentAreaIndex+"]["+difficultyLevelIndex+"]");
    					if(answer.answer.equalsIgnoreCase(question.answer)){
    						QuestionsAnsweredData[gradeIndex][contentAreaIndex][difficultyLevelIndex]+=1;
    					//	Log.d("Virtual TA","Incrementing in QuestionAnsweredData["+gradeIndex+"]["+contentAreaIndex+"]["+difficultyLevelIndex+"]");
    					}else{
    						QuestionsAnsweredData[gradeIndex][contentAreaIndex][difficultyLevelIndex]-=1;
    					//	Log.d("Virtual TA","Decrementing in QuestionAnsweredData["+gradeIndex+"]["+contentAreaIndex+"]["+difficultyLevelIndex+"]");
    					}
    					
    			}else{
    				Log.d("Virtual TA", "Error in values"+"gradeIndex="+gradeIndex+"difficultyIndex="+difficultyLevelIndex+"contentAreaIndex="+contentAreaIndex);
    			}
    				
    			
    		}
    		
    	}
    	Log.d("Virtual TA", "Completed Precomputing Data");
    	
    	for(int i=0;i<Question.Grade.values().length;i++){
    		for(int j=0;j<Question.ContentArea.values().length;j++){
    			for(int k=0;k<Question.Difficulty.values().length;k++){
    				Log.d("Virtual TA","QuestionsAskedData["+i+"]["+j+"]["+k+"]"+QuestionsAskedData[i][j][k]);
    			}
    		}
    	}
    	for(int i=0;i<Question.Grade.values().length;i++){
    		for(int j=0;j<Question.ContentArea.values().length;j++){
    			for(int k=0;k<Question.Difficulty.values().length;k++){
    				Log.d("Virtual TA","QuestionsAnsweredData["+i+"]["+j+"]["+k+"]"+QuestionsAnsweredData[i][j][k]);
    			}
    		}
    	}
    	
    	
    		
    	//getLeastIndex();
    	//If grades are equal 
    	  //ask questions based on no.of questions
    	    //If no of questions are equal ask question based on ContentArea
    	    
     	//Get gradePercentage index that has least percentage
    	 
    	//Log.d("Virtual TA","Grades"+Arrays.toString(GradePercentage));
    	//Log.d("Virtual TA","ContenArea"+Arrays.toString(ContentAreaPercentage));
    	//Log.d("Virtual TA","DifficultyLevel"+Arrays.toString(DifficultyPercentage));
    	
    }*/
    
    void computeGradePercentage(){
    	/*int score=0;
    	int total=0;
    	for(int i=0;i<Question.Grade.values().length;i++){
    		for(int j=0;j<Question.ContentArea.values().length;j++){
    			for(int k=0;k<Question.Difficulty.values().length;k++){
    				if(QuestionsAnsweredData[i][j][k]>0)
    				score+=QuestionsAnsweredData[i][j][k];
    				if(QuestionsAnsweredData[i][j][k]==0){
    					if(QuestionsAskedData[i][j][k]!=0)
    						score+=Math.rint(QuestionsAskedData[i][j][k]/2);
    				}
    				total+=QuestionsAskedData[i][j][k];
    			}
    		}
    		
    		GradePercentage[i]=(score*100/total);
    		GradeQuestionsTotal[i]=total;
    		Log.d("Virtual TA", "Score"+score+"total"+total);
    		score=0;
    		total=0;
    	}
    	*/
    }
    void computeContenAreaPercentage(){
    /*	int score=0;
    	int total=0;
    	for(int j=0;j<Question.ContentArea.values().length;j++){
    		for(int i=0;i<Question.Grade.values().length;i++){
			for(int k=0;k<Question.Difficulty.values().length;k++){
				if(QuestionsAnsweredData[i][j][k]>0)
    				score+=QuestionsAnsweredData[i][j][k];
    			if(QuestionsAnsweredData[i][j][k]==0){
    					if(QuestionsAskedData[i][j][k]!=0)
    						score+=Math.rint(QuestionsAskedData[i][j][k]/2);
    			}
				total+=QuestionsAskedData[i][j][k];
			}
    		}
    		ContentAreaPercentage[j]=(score*100/total);
    		ContentQuestionsTotal[j]=total;
    		Log.d("Virtual TA", "Score"+score+"total"+total);
    		score=0;
    		total=0;
		}
    	*/
    }
    void computeDifficultyLevelPercentage(){
    	/*int score=0;
    	int total=0;
		for(int k=0;k<Question.Difficulty.values().length;k++){
			for(int i=0;i<Question.Grade.values().length;i++){
	    		for(int j=0;j<Question.ContentArea.values().length;j++){
	    			if(QuestionsAnsweredData[i][j][k]>0)
	    				score+=QuestionsAnsweredData[i][j][k];
	    			if(QuestionsAnsweredData[i][j][k]==0){
	    					if(QuestionsAskedData[i][j][k]!=0)
	    						score+=Math.rint(QuestionsAskedData[i][j][k]/2);
	    			}
	    			total+=QuestionsAskedData[i][j][k];
	    	
	    		}
	    	}
			DifficultyPercentage[k]=(score*100/total);
			DifficultyQuestionsTotal[k]=total;
			Log.d("Virtual TA", "Score"+score+"total"+total);
    		score=0;
    		total=0;
		}
    	
		*/
    }
    
       
    int ExamHeuristics(double percentage[],int totalQuestion[]){
      	
    	if(getMaxDouble(percentage)!=getMinDouble(percentage)){
    		//return a index with least percentage
       		return getMinDouble(percentage); 
    	}
    	else{
    		if(getMaxInt(totalQuestion)!=getMinInt(totalQuestion)){
    			//return a index with least no of question
    			return getMinInt(totalQuestion);
    		}
    		else    			
    		return -1;
    	}
	 
    }
    
    int getMaxInt(int inputArray[]){
    	int max=Integer.MIN_VALUE;
    	int index=-1;
    	for(int i=0;i<inputArray.length;i++){
    		if(inputArray[i]>max){
    			max=inputArray[i];
    			index=i;
    		}
    		
    	}
    	Log.d("Virtual TA", "Maxvalue="+max);
    	return index;
    }
    int getMinInt(int inputArray[]){
    	int min=Integer.MAX_VALUE;
    	int index=-1;
    	for(int i=0;i<inputArray.length;i++){
    		if(inputArray[i]<min){
    			min=inputArray[i];
    			index=i;
    		}
    	}
    	Log.d("Virtual TA", "Minvalue="+min);
    	return index;
    }
    int getMaxDouble(double inputArray[]){
    	double  max=Double.MIN_VALUE;
    	int index=-1;
    	for(int i=0;i<inputArray.length;i++){
    		if(inputArray[i]>max){
    			max=inputArray[i];
    			index=i;
    		}
    	}
    	Log.d("Virtual TA", "Maxvalue="+max);
    	return index;
    }
    int getMinDouble(double inputArray[]){
    	double  min=Double.MAX_VALUE;
    	int index=-1;
    	for(int i=0;i<inputArray.length;i++){
    		if(inputArray[i]<min){
    			min=inputArray[i];
    			index=i;
    		}
    	}
    	Log.d("Virtual TA", "Minvalue="+min);
    	return index;
    }
    
    void previousExamHeuristics(){
    	ArrayList<Exam> exams=(ArrayList<Exam>) examService.getPreviousExams(10);
    	int[] exam_score=new int[10];
    	
    	//Create a Hashmap for all Grades
    	HashMap grades_map = new HashMap();
    	for(Question.Grade grade: Question.Grade.values()){
    		grades_map.put(grade.toString(), 0);
    	}
    	
    	
    	//Create a Hashmap for all Content Area
    	HashMap contentArea_map = new HashMap();
    	for(Question.ContentArea conArea: Question.ContentArea.values()){
    		grades_map.put(conArea.toString(), 0);
    	}
    	
    	//Create a Hashmap for all Difficulty
    	HashMap difficulty_map = new HashMap();
    	for(Question.Difficulty difficulty: Question.Difficulty.values()){
    		grades_map.put(difficulty.toString(), 0);
    	}
    	
    	
	   	int j=0;
	    	for(Exam e: exams){
//	    		exam_score[j]=0;
	    		for(int i=0;i<e.answers.length;i++){
	    			Answer answer= e.answers[i];
	    		    Question question=questionService.getQuestion(answer.questionId);
	    		    if(answer.answer.equalsIgnoreCase(question.answer)){
	    		    	exam_score[j]=exam_score[j]+1;   	
	    		    	Log.d("Virtual TA","Exam"+j);
	    		    	Log.d("Virtual TA",""+question.contentArea);
	    		    	Log.d("Virtual TA",""+question.difficulty);
	    		    	Log.d("Virtual TA",""+question.gradeLevel);
	    		    }
	    		}
	    		j++;
	    		
	    	}
    	for(j=0;j<10;j++)
    	Log.d("Virtual TA", "exam_score["+j+"]"+"="+exam_score[j]);
    	
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

    public void receiveFeedback(View view) {
    	 final RadioGroup choices = (RadioGroup)view.findViewById(R.id.Emotion_group);
          	 Exam currentExam=examService.getActiveExam();
           	 Answer current_answer=currentExam.answers[currentExam.currentQuestion-1];
             Question currentQuestion=questionService.getQuestion(current_answer.questionId);
               	
                 switch(choices.getCheckedRadioButtonId()) {
                     case R.id.RadioButton1:
                	    koko.invokeLearnEvent("1",currentQuestion.gradeLevel.toString(),currentQuestion.contentArea.toString(),currentQuestion.difficulty.toString());   	
                     	Log.d("Virtual TA", "VirtualTA.java:receive Feedback-option 1-Like");
                         break;
                     case R.id.RadioButton2:
                    	koko.invokeLearnEvent("1",currentQuestion.gradeLevel.toString(),currentQuestion.contentArea.toString(),currentQuestion.difficulty.toString());   	
                      	Log.d("Virtual TA", "VirtualTA.java:receive Feedback-option 2-Joy");
                         //answerQuestion("B");
                         break;
                     case R.id.RadioButton3:
                    	koko.invokeLearnEvent("1",currentQuestion.gradeLevel.toString(),currentQuestion.contentArea.toString(),currentQuestion.difficulty.toString());   	
                      	Log.d("Virtual TA", "VirtualTA.java:receive Feedback-option 3-Dislike");
                         //answerQuestion("C");
                         break;
                     case R.id.RadioButton4:
                    	koko.invokeLearnEvent("1",currentQuestion.gradeLevel.toString(),currentQuestion.contentArea.toString(),currentQuestion.difficulty.toString());   	
                      	Log.d("Virtual TA", "VirtualTA.java:receive Feedback-option 4-Fear");
                         //answerQuestion("D");
                         break;
                     default:
                         Toast.makeText(choices.getContext(), "Please Select a Feedback.", 2);
                 }
           
        
    }

   
	public void setKoko(KokoService koko) {
		this.koko = koko;
	}

	public KokoService getKoko() {
		return koko;
	}

	public void setRemoteQuestionAvailble(boolean remoteQuestionAvailble) {
		this.remoteQuestionAvailble = remoteQuestionAvailble;
	}

	public boolean isRemoteQuestionAvailble() {
		return remoteQuestionAvailble;
	}

	public void setRemoteReceivedId(int remoteReceivedId) {
		this.remoteReceivedId = remoteReceivedId;
	}

	public int getRemoteReceivedId() {
		return remoteReceivedId;
	}
}
