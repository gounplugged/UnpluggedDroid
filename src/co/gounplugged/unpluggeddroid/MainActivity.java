package co.gounplugged.unpluggeddroid;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	
	private TextView lastPost;
	private Button submitButton;
	private EditText newPostText; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        boolean isBluetoothSupported = isBluetoothSupported();
        
        if (isBluetoothSupported) {
        	startApplication();
        } else {
        	setErrorMessage();
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
    
    private boolean isBluetoothSupported() {
    	boolean isSupported = true;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        	isSupported = false;
        }
        return isSupported;
    }
    
    private void setErrorMessage() {
    	lastPost = (TextView) findViewById(R.id.last_post);
    	String errorMsg = "Sorry Bluetooth is not supported on your phone";
    	lastPost.setText(errorMsg);
    }
    
    private void startApplication() {
        submitButton = (Button) findViewById(R.id.submit_button);
        lastPost = (TextView) findViewById(R.id.last_post);
        newPostText = (EditText) findViewById(R.id.new_post_text);
        
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	lastPost.setText(newPostText.getText());
            }
        });
    }
}
