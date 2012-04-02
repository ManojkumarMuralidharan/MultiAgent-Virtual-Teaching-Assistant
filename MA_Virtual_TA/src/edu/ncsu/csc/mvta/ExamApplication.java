package edu.ncsu.csc.mvta;


import java.net.ConnectException;


import jade.android.ConnectionListener;
import jade.android.JadeGateway;
import jade.core.Profile;
import jade.imtp.leap.JICP.JICPProtocol;
import jade.util.leap.Properties;
import edu.ncsu.csc.mvta.jade.VTAGatewayAgent;
import edu.ncsu.csc.mvta.jade.MessageListener;
import edu.ncsu.csc.mvta.jade.VTAgent;
import edu.ncsu.csc.mvta.service.ExamService;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class ExamApplication extends Application implements ConnectionListener{

    protected ExamService examService;
    private VTAgent vTAgent;
    
    private ServiceConnection mConnection = null;
    
    private JadeGateway gateway;
    private static final String TAG = "ExamApplication";
	private MessageListener messageListener;


	
    @Override
    public void onCreate() {
        super.onCreate();
        
        if(mConnection == null) {
            final Context context = this;
            mConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className, IBinder service) {
                    // This is called when the connection with the service has been
                    // established, giving us the service object we can use to
                    // interact with the service.  Because we have bound to a explicit
                    // service that we know is running in our own process, we can
                    // cast its IBinder to a concrete class and directly access it.
                    examService = ((ExamService.LocalBinder)service).getService();
                    examService.initialize(context);
                }
    
                public void onServiceDisconnected(ComponentName className) {
                    // This is called when the connection with the service has been
                    // unexpectedly disconnected -- that is, its process crashed.
                    // Because it is running in our same process, we should never
                    // see this happen.
                    examService = null;
                }
            };
        }
        
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        Intent intent = new Intent(this, ExamService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        
        // Connect to JADE Container 
    

        String msisdn = getResources().getString(R.string.msisdn);
        String all_msisdn = getResources().getString(R.string.all_msisdn);
        String host = getResources().getString(R.string.host);
        String port= getResources().getString(R.string.port);
        
        
        //Create The VTAgent
        vTAgent = new VTAgent(msisdn,host,port,all_msisdn);
       
        // CREATE THE Message Listener
        messageListener = new MessageListener();
		// CREATE AND THE JADE PROPERTIES CLASS
		Properties props = new Properties();
		props.setProperty(Profile.MAIN_HOST, host);
		props.setProperty(Profile.MAIN_PORT, port);
		props.setProperty(JICPProtocol.MSISDN_KEY, msisdn);
		Log.i(TAG, "JADE Registration Properties: " + msisdn+"@"+host+":"+port+"/JADE");
		Log.i(TAG, "JADE Other Agents: " + all_msisdn);
	
		try {
			Log.i(TAG, "Trying to connect to JADE Container ");
			 JadeGateway.connect(VTAGatewayAgent.class.getName(), null, props, this, this);
		} catch (Exception e) {
			Log.i(TAG, "Unable to connect", e);
		}
		
    }
    
    public void onConnected(JadeGateway gw) {
		Log.i(TAG, "Connected to JADE container ");
		gateway = gw;
		vTAgent.setGateway(gw);
		examService.setVTAgetnt(vTAgent);
		try {
			gateway.execute(messageListener);
			
		} catch (ConnectException ce) {
			ce.printStackTrace();
			Log.e(TAG, ce.getMessage(), ce);
		} catch (Exception e1) {
			e1.printStackTrace();
			Log.e(TAG, e1.getMessage(), e1);
		}
		
	}

	public void onDisconnected() {
		Log.i(TAG, "Disconnected from JADE container");

	}
	
    public void onTerminate() {
        // Detach our existing connection.
        unbindService(mConnection);
        
        Log.i(TAG, " onTerminate method is called. Agent will disconnect from JADE container.");
		
		//Disconnect From JADE
		try {
			if (gateway != null)
				gateway.shutdownJADE();

		} catch (ConnectException e) {
			e.printStackTrace();
		}
		if (gateway != null)
			gateway.disconnect(this);
		 
    }
    
    public ExamService getExamService() {
        return examService;
    }

	

	public VTAgent getVTAgent() {
		return vTAgent;
	}
}