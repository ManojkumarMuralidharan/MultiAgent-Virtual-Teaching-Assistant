package edu.ncsu.csc.mvta.jade;

import android.util.Log;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.gateway.GatewayAgent;

public class VTAGatewayAgent extends GatewayAgent {

	private ACLMessageListener messageListener;
	private final String TAG = "VTAGatewayAgent";

	protected void setup() {
		super.setup();
		addBehaviour(new MessageReceiverBehaviour());
	}

	protected void processCommand(final Object command) {
		if (command instanceof Behaviour) {
			SequentialBehaviour sb = new SequentialBehaviour(this);
			sb.addSubBehaviour((Behaviour) command);
			sb.addSubBehaviour(new OneShotBehaviour(this) {
				public void action() {
					VTAGatewayAgent.this.releaseCommand(command);
				}
			});
			addBehaviour(sb);
		} else if (command instanceof ACLMessageListener) {
			Log.i(TAG,"processCommand(): New ACLMessageListener received and registered!");

			ACLMessageListener listener = (ACLMessageListener) command;
			messageListener = listener;
			releaseCommand(command);
		}

		else {
			Log.w(TAG," processCommand: Unknown command " + command);
		}
	}

	private class MessageReceiverBehaviour extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = myAgent.receive();
			Log.i(TAG,"MessageReceiverBehaviour().Message received: "+ this.hashCode());
			// if a message is available and a listener is available
			if (msg != null && messageListener != null) {
				// callback the interface update function
				messageListener.onMessageReceived(msg);
			} else {
				block();
			}
		}

	}

}
