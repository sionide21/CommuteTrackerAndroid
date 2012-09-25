package net.moosen.commutetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Main extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void stopTracker(View v) {
        Intent intent = new Intent(this, Tracker.class);
        stopService(intent);
        finish();
    }
}
