package net.moosen.commutetracker;

import android.app.Notification;
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
	private boolean neverLocated = true;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		showNotification();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 0, this);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// All a consumer can do for now is start and stop
		return null;
	}

	@Override
	public void onDestroy() {
		lm.removeUpdates(this);
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
	}


	@Override
	public void onLocationChanged(Location location) {
		if (neverLocated) {
			neverLocated = false;
			updateNotification(R.string.tracker_running);
		}
		String json = new JSONBuilder()
			.addInfo("Accuracy", location.getAccuracy())
			.addInfo("Altitude", location.getAltitude())
			.addInfo("Bearing", location.getBearing())
			.addInfo("Latitude", location.getLatitude())
			.addInfo("Longitude", location.getLongitude())
			.addInfo("Speed", location.getSpeed())
			.addInfo("Time", location.getTime())
			.toString();

		Log.w("net.moosen.commutetracker", json);
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
		sb.append("  \"");
		sb.append(key);
		sb.append('"');
		sb.append(": ");
		sb.append(value);
		sb.append(",\n");

		return this;
	}

	@Override
	public String toString() {
		return "{\n" + sb.toString() + "\n}";
	}
}

