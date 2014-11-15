package com.myowntrainer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.cloudcode.IBMCloudCode;
import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.file.IBMFileSync;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;


public class MainActivity extends Activity {
    public static final String APPLICATION_ID = "d435ede4-530e-4cde-b116-0aa7002e0f27"
            , APPLICATION_SECRET = "1aff7af281a13147b427b5d1c7fd5bea10533d9b"
            , APPLICATION_ROUTE = "MYOwnTrainerCloud.mybluemix.net";

    public String poseString = "", rollString = "", pitchString = "", yawString = "";
    MainPanel panel;
    LinearLayout ll;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ll = new LinearLayout(this);
       // setContentView(R.layout.activity_main);
        panel = new MainPanel(this);
        ll.addView(panel);
        setContentView(ll);
        //status = (TextView) findViewById(R.id.status);
        //status.setBackgroundColor(Color.DKGRAY);

        initHub();
        initBluemix();

    }
    private void initHub(){
        Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e("Error", "Could not initialize the Hub.");
            //status.setText("Could not initialize the Hub.");
            finish();
            return;
        } else {
            //status.setTextColor(Color.CYAN);
            //status.setText("Hub Initialized");
        }
        Hub.getInstance().addListener(mListener);
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
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);
        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
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

    public void findMyo() {
        Intent intent = new Intent(this, ScanActivity.class);
        this.startActivity(intent);
    }
    
    private DeviceListener mListener = new AbstractDeviceListener() {
        private Arm mArm = Arm.UNKNOWN;
        private XDirection mXDirection = XDirection.UNKNOWN;
        
        @Override
        public void onConnect(Myo myo, long timestamp) {
            Toast.makeText(MainActivity.this, "Myo Connected!", Toast.LENGTH_SHORT).show();
            //status.setTextColor(Color.GREEN);
            //status.setText("Myo Connected");
        }

        @Override
        public void onArmRecognized(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            mArm = arm;
            mXDirection = xDirection;
        }
        
        @Override
        public void onArmLost(Myo myo, long timestamp) {
            mArm = Arm.UNKNOWN;
            mXDirection = XDirection.UNKNOWN;
        }
        
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));
            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (mXDirection == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }
            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
           /* mTextView.setRotation(roll);
            mTextView.setRotationX(pitch);
            mTextView.setRotationY(yaw);*/
            rollString = "" + roll;
            pitchString = "" + pitch;
            yawString = "" + yaw;
            panel.invalidate();
        }

        
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            Toast.makeText(MainActivity.this, "Myo Disconnected!", Toast.LENGTH_SHORT).show();
            //status.setTextColor(Color.RED);
            //status.setText("Myo Connected");
        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            Toast.makeText(MainActivity.this, "Pose: " + pose, Toast.LENGTH_SHORT).show();
            //status.setTextColor(Color.GREEN);
            //status.setText(pose.toString());
            
            poseString = pose.toString();
            panel.invalidate();
            //TODO: Do something awesome.
        }
    };

}
