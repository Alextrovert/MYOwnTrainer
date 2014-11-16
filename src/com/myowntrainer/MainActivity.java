package com.myowntrainer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import bolts.Continuation;
import bolts.Task;

import com.myowntrainer.R;
import com.ibm.mobile.services.cloudcode.IBMCloudCode;
import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.file.IBMFileSync;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.scanner.ScanActivity;

public class MainActivity extends Activity {
	
    public static final String APPLICATION_ID = "294e7073-3b20-4ce2-aff1-56eb59a624fc"
            , APPLICATION_SECRET = "075afadf49b2dc31d448abdc6e0b54c59a7a6fdd"
             , APPLICATION_ROUTE = "sportshack2014cloud.mybluemix.net";
    
    static Hub hub;
    LinearLayout ll;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                		     WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        ll = new LinearLayout(this);
        ll.addView(new MainPanel(this));
        setContentView(ll);
        //setContentView(R.layout.activity_main);

        //status = (TextView) findViewById(R.id.status);
        //status.setBackgroundColor(Color.DKGRAY);

        initHub();
        initBluemix();
    }
    
    private void initHub(){
        hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e("Error", "Could not initialize the Hub.");
            //status.setText("Could not initialize the Hub.");
            finish();
            return;
        } else {
            //status.setTextColor(Color.CYAN);
            //status.setText("Hub Initialized");
        }
        //Hub.getInstance().addListener(ExcerciseActivity.mListener);
    }
    
    private void initBluemix() {
        IBMBluemix.initialize(this, APPLICATION_ID, APPLICATION_SECRET, APPLICATION_ROUTE);
        IBMCloudCode cloudCodeService = IBMCloudCode.initializeService();
        IBMData.initializeService();
        IBMData dataService = IBMData.initializeService(); //Initializing object storage capability
        IBMFileSync fileSync = IBMFileSync.initializeService(); //Initializing file storage capability
        Player.registerSpecialization(Player.class); //Registering a specialization
        //IBMPush.initializeService();
        //createAndSavePlayer("11", "Vanshil");
    }
    
    private void createAndSavePlayer(String number, String name) {
        Player player = new Player(number, name);
        player.save().continueWith(new Continuation<IBMDataObject, Void>() {
        	@Override
        	public Void then(Task<IBMDataObject> task) throws Exception {
                if (task.isFaulted()) {
                    // Handle errors
                    //status.setText("no work");
                } else {
                    Player myPlayer = (Player) task.getResult();
                    //status.setText("player created!");
                    // Do more work
                }
                return null;
        	}
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // If Bluetooth is not enabled, request to turn it on.
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void MyFitness() {
        Intent intent = new Intent(this, ExerciseListActivity.class);
        this.startActivity(intent);
    }
    
    public void findMyo() {
        Intent intent = new Intent(this, ScanActivity.class);
        this.startActivity(intent);
    }
    
    public void Friends() {
        Intent intent = new Intent(this, FriendsActivity.class);
        this.startActivity(intent);
    }
    
    public void Today() {
        Intent intent = new Intent(this, ProgressActivity.class);
        this.startActivity(intent);
    }
    
    public void DailyChallenge() {
        Intent intent = new Intent(this, ChallengeActivity.class);
        this.startActivity(intent);
    }
    
    public void Leaderboards() {
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        this.startActivity(intent);
    }
}
