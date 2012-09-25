package net.moosen.commutetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Simple activity that just spawns the Tracker service and exits.
 */
public class Start extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, Tracker.class);
        startService(intent);
        finish();
	}
}
