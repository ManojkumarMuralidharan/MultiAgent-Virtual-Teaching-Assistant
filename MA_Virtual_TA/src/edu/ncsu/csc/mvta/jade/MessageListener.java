package edu.ncsu.csc.mvta.jade;


import jade.lang.acl.ACLMessage;
import android.util.Log;


public class MessageListener implements ACLMessageListener {

	private static final String TAG = "MessageListener";
	
	public void onMessageReceived(ACLMessage message) {
		Log.i(TAG," Message has received: " + message.getContent() + " from: " + message.getSender());
		
		/* START STUDENT CODE */
	    
	        // add your code to manage receiving messages from other agents
	    
	    /* END STUDENT CODE */
	}

}
	
