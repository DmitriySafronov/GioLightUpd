package ikeagold.zimniy.giolight;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ServiceExample extends Service {

	// DEV
	public boolean Sync;
	public int Time;
	SharedPreferences prefs;

	public int INTERVAL = 36000000; //1hour, 10 sec public static final int INTERVAL = 10000;
	public static final int FIRST_RUN = 5000; // 5 seconds
	int REQUEST_CODE = 11223344;

	AlarmManager alarmManager;

	@Override
	public void onCreate() {
		super.onCreate();

		// DEV
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Time = prefs.getInt("timekey", 24);
		Sync = prefs.getBoolean("synckey", false);
		
		INTERVAL = Time*36000000;

		if (Sync == false) {
			stopService(new Intent(this, ServiceExample.class));
		} else {
			startService();
		}
		// DEV END

		// OFF startService();
		Log.v(this.getClass().getName(), "onCreate(..)");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(this.getClass().getName(), "onBind(..)");
		return null;
	}

	@Override
	public void onDestroy() {
		if (alarmManager != null) {
			Intent intent = new Intent(this, RepeatingAlarmService.class);
			alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE,
					intent, 0));
		}
		Toast.makeText(this, "Service Stopped!", Toast.LENGTH_LONG).show();
		Log.v(this.getClass().getName(),
				"Service onDestroy(). Stop AlarmManager at "
						+ new java.sql.Timestamp(System.currentTimeMillis())
								.toString());
	}

	private void startService() {

		Intent intent = new Intent(this, RepeatingAlarmService.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
				REQUEST_CODE, intent, 0);

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + FIRST_RUN, INTERVAL,
				pendingIntent);

		Toast.makeText(this, "Service Started.", Toast.LENGTH_LONG).show();
		Log.v(this.getClass().getName(), "AlarmManger started at "
				+ new java.sql.Timestamp(System.currentTimeMillis()).toString());
	}
}
