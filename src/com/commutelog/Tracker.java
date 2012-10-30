package com.commutelog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class Tracker extends Service implements LocationListener {
	public static final int ONGOING_NOTIFICATION = 1;
	private Notification notification = null;
	private LocationManager lm;
	private NotificationManager notificationManager;
	private JSONOutputStream out;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30 * 1000, 0, this);
		try {
			out = new JSONOutputStream(openSessionFile());
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open output file.", e);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private OutputStream openSessionFile() throws FileNotFoundException {
		File filesDir = getExternalFilesDir(null);
		String filename = "Commute-" + System.currentTimeMillis() / 1000 + ".json";
		File file = new File(filesDir, filename);
		// Extremely unlikely
		for (int x = 1; file.exists(); x++) {
			file = new File(filesDir, filename + '.' + x);
		}
		return new FileOutputStream(file);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// All a consumer can do for now is start and stop
		return null;
	}

	@Override
	public void onDestroy() {
		lm.removeUpdates(this);
		try {
			out.close();
		} catch (IOException e) {
			// Log it but it doesn't much matter
			Log.e(this.getClass().getName(), "Error closing commute file.", e);
		}
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")  // Using old notification code
	protected void showNotification() {
		notification = new Notification(android.R.drawable.ic_menu_compass, getText(R.string.tracker_starting), System.currentTimeMillis());
		updateNotification(R.string.tracker_starting);
		startForeground(ONGOING_NOTIFICATION, notification);
	}

	@SuppressWarnings("deprecation")  // Using old notification code
	protected void updateNotification(int status_res) {
		Intent notificationIntent = new Intent(this, Main.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.tracker_title), getText(status_res), pendingIntent);
		notification.tickerText = getText(status_res);
		notificationManager.notify(ONGOING_NOTIFICATION, notification);
	}

	@Override
	public void onLocationChanged(Location location) {
		updateNotification(R.string.tracker_running);
		try {
			out.write(new JSONBuilder()
				.addInfo("Accuracy", location.getAccuracy())
				.addInfo("Altitude", location.getAltitude())
				.addInfo("Bearing", location.getBearing())
				.addInfo("Latitude", location.getLatitude())
				.addInfo("Longitude", location.getLongitude())
				.addInfo("Speed", location.getSpeed())
				.addInfo("Time", location.getTime())
			);
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Error writing commute file.", e);
			updateNotification(R.string.tracker_cannot_write);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.AVAILABLE) {
			updateNotification(R.string.tracker_running);
		} else {
			updateNotification(R.string.tracker_waiting);
		}
	}
}
/**
 * Quick and dirty JSON builder.
 */
class JSONBuilder {
	private StringBuilder sb;
	private boolean started = false;

	public JSONBuilder() {
		sb = new StringBuilder(512);
	}

	/**
	 * Add a key value pair. This method expects numerics and does not escape other values.
	 *
	 * @param key The key
	 * @param value A numerical value
	 * @return Myself
	 */
	public JSONBuilder addInfo(String key, Object value) {
		if (started) {
			sb.append(",\n");
		} else {
			started = true;
		}
		sb.append("  \"");
		sb.append(key);
		sb.append('"');
		sb.append(": ");
		sb.append(value);

		return this;
	}

	@Override
	public String toString() {
		return "{\n" + sb.toString() + "\n}";
	}
}

/**
 * A rudimentary list store.
 */
class JSONOutputStream {
	private final OutputStream out;
	private boolean started = false;

	public JSONOutputStream(OutputStream out) {
		this.out = out;
	}

	public void write(JSONBuilder object) throws IOException {
		if (started) {
			out.write(',');
		} else {
			out.write('[');
			started = true;
		}
		out.write(object.toString().getBytes());
	}

	public void close() throws IOException {
		if (started) {
			out.write(']');
		}
		out.close();
	}
}