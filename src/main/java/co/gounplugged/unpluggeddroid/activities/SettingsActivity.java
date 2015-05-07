package co.gounplugged.unpluggeddroid.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import co.gounplugged.unpluggeddroid.R;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView profileOption = (TextView) findViewById(R.id.text_profile_settings_activity);
        profileOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingsActivity.this, ProfileActivity.class);
                SettingsActivity.this.startActivity(mainIntent);
                SettingsActivity.this.finish();
            }
        });

        TextView notificationsOption= (TextView) findViewById(R.id.text_notifications_settings_activity);
        notificationsOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingsActivity.this, NotificationsActivity.class);
                SettingsActivity.this.startActivity(mainIntent);
                SettingsActivity.this.finish();
            }
        });

        TextView chatOption= (TextView) findViewById(R.id.option_chat_settings_activity);
        chatOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingsActivity.this, ChatActivity.class);
                SettingsActivity.this.startActivity(mainIntent);
                SettingsActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
