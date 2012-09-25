package net.moosen.commutetracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Tracker extends Service {
	public static final int ONGOING_NOTIFICATION = 1; 

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		showNotification();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// All a consumer can do for now is start and stop
		return null;
	}

	@SuppressWarnings("deprecation")
	protected void showNotification() {
		Notification notification = new Notification(android.R.drawable.ic_menu_compass, getText(R.string.tracker_starting), System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, Main.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.tracker_title), getText(R.string.tracker_running), pendingIntent);
		startForeground(ONGOING_NOTIFICATION, notification);
	}
}
