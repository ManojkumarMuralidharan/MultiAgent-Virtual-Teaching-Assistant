package edu.ncsu.csc.mvta.jade;


import java.util.ArrayList;

import edu.ncsu.csc.mvta.data.Question;
import edu.ncsu.csc.mvta.service.ExamService;
import edu.ncsu.csc.mvta.service.QuestionService;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import android.content.Context;
import android.util.Log;


public class MessageListener implements ACLMessageListener {

	private static final String TAG = "MessageListener";
	private VTAgent vTAgent;
	private ExamService examService;
	
	public static ArrayList<ACLMessage> incomingRequest ;
	public static ArrayList<ACLMessage> SuggestedQuestionId ;
	
	public MessageListener() {
		
		// TODO Auto-generated constructor stub
		Log.d("Virtual TA"+TAG,"Default Constructor");
	}

	public MessageListener(VTAgent vTAgent) {
		// TODO Auto-generated constructor stub
		incomingRequest=new ArrayList<ACLMessage>();
		SuggestedQuestionId=new ArrayList<ACLMessage>();
		if(vTAgent!=null)
		this.setvTAgent(vTAgent);
		else
		Log.d("Virtual TA"+TAG,"vTAget is null");
	}
	
	
	public void onMessageReceived(ACLMessage message) {
		Log.i(TAG," Message has received: " + message.getContent() + " from: " + message.getSender());
		
		
		
		String messageReceived=message.getContent();
		
		
		String receivedPerformative=ACLMessage.getPerformative(message.getPerformative());
		/* START STUDENT CODE */
		
		//If the message type is of REQUEST create a new ACLMessage and send him a response
		//VTAgent tempVTAgent= new VTAgent(msisdn, host, port, all_msisdn);
		
		//Log.d("Virtual TA", message)
		
		
		if(receivedPerformative.equalsIgnoreCase("REQUEST")){
			incomingRequest.add(message);
			String parameters[]=new String[3];
			parameters=message.getContent().toString().split(",");
				
			QuestionService ques_service=new QuestionService();
			
			int grade_index=getIndexOfEnum("grade",parameters[0]);
			int difficulty_index=getIndexOfEnum("difficulty",parameters[1]);
			int con_Area_index=getIndexOfEnum("contentArea",parameters[2]);
			
			Question remoteQuestion=ques_service.randomQuestion(Question.Grade.values()[grade_index], Question.Difficulty.values()[difficulty_index], Question.ContentArea.values()[con_Area_index]);
			if(remoteQuestion==null){
				remoteQuestion=ques_service.randomQuestion(Question.Grade.values()[grade_index], Question.Difficulty.values()[difficulty_index], Question.ContentArea.values()[con_Area_index]);
			}
			
			AID senderAID=new AID(message.getSender().getLocalName()+"@192.168.1.120:1097/JADE",true);
		    //String senderId=message.getSender().getLocalName();
		    //String senderId2=message.getSender();
			//this.getvTAgent().sendMessage(""+remoteQuestion.id, "INFORM", senderAID );
			//this.getvTAgent().sendMessage(""+remoteQuestion.id, "REQUEST", this.getvTAgent().getAllReceivers().get(0) );
			
		}else if(receivedPerformative.equalsIgnoreCase("INFORM")){
			String parameter=message.getContent().toString();
			SuggestedQuestionId.add(message);
			
		}else{
			Log.d("Virtual TA","Shouldn't Log this" );
		}
			
		
		
		//messageReceived.split(regularExpression)
	        // add your code to manage receiving messages from other agents
	    
	    /* END STUDENT CODE */
	}
	 public static int getIndexOfEnum(String name,String parameter){
	    	Question.Grade[] grades;
	    	Question.ContentArea[] contentAreas;
	    	Question.Difficulty[] difficulties;

	    	
	    	if(name.equalsIgnoreCase("grade")){
	    		grades= Question.Grade.values();
	    		for(int j=0;j<grades.length;j++){
	    			if(parameter.equalsIgnoreCase(grades[j].toString())){
	    				   return j;
	    			}
	    		}
	    	}else if(name.equalsIgnoreCase("contentArea")){
	    		contentAreas=Question.ContentArea.values();
	    		for(int j=0;j<contentAreas.length;j++){
	    			if(parameter.equalsIgnoreCase(contentAreas[j].toString())){
	    				   return j;
	    			}
	    		}
	    	}else if(name.equalsIgnoreCase("difficulty")){
	    		difficulties=Question.Difficulty.values();
	    		for(int j=0;j<difficulties.length;j++){
	    			if(parameter.equalsIgnoreCase(difficulties[j].toString())){
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

	public void setvTAgent(VTAgent vTAgent) {
		this.vTAgent = vTAgent;
	}

	public VTAgent getvTAgent() {
		return vTAgent;
	}
	public void setExamService(ExamService examService) {
		this.examService = examService;
	}
	public ExamService getExamService() {
		return examService;
	}


}
	
