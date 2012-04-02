package edu.ncsu.csc.mvta.jade;

import jade.lang.acl.ACLMessage;


public interface ACLMessageListener {
	public void onMessageReceived(ACLMessage message); 
}
