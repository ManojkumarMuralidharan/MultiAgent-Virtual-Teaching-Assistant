package edu.ncsu.csc.mvta.jade;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import jade.android.JadeGateway;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class VTAgent {

	private final String TAG = "VTAgent";
	private String host;
	private String port;
	private String msisdn;
	private String all_msisdn;
	private JadeGateway gateway;

	public VTAgent(String msisdn, String host, String port, String all_msisdn) {
		this.msisdn = msisdn;
		this.host = host;
		this.port = port;
		this.all_msisdn = all_msisdn;
	}

	public List<AID> getAllReceivers() {
		AID aid;
		List<AID> aidList = new ArrayList<AID>();
		for (String agent_msidn : all_msisdn.split(","))
			if (!agent_msidn.equals(msisdn)) {
				aid = new AID(agent_msidn + "@" + host + ":" + port + "/JADE",AID.ISGUID);
				aidList.add(aid);
			}
		return aidList;
	}

	

	public void sendMessageToAllAgents(String content, String comAct) {
		sendMessage(content, comAct, getAllReceivers());
	}

	public void sendMessage(String content, String comAct, AID aid) {
		List<AID> singleAIDList = new ArrayList<AID>();
		singleAIDList.add(aid);
		sendMessage(content, comAct, singleAIDList);
	}

	public void sendMessage(String content, String comAct, List<AID> aidList) {
		ACLMessage msg = new ACLMessage(ACLMessage.getInteger(comAct));
		msg.setContent(content);
		for (AID aid : aidList)
			msg.addReceiver(aid);

		MessageSenderBehaviour messageSenderBehaviour = new MessageSenderBehaviour(msg);

		try {
			gateway.execute(messageSenderBehaviour);
		}

		catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public void setGateway(JadeGateway gateway) {
		this.gateway = gateway;
	}
	
    

}
