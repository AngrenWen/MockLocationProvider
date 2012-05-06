package pl.angrenwen.mocklocationprovider;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;

public class LocationProviderService extends Service {

	private static final String PROVIDER = "Test";
	private static final int FREQ = 1000;
	private static final boolean LOGGING_ON = true;
	
	private LocationManager locationManager;
	private Handler handler;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationProvider provider = locationManager.getProvider(PROVIDER);
		
		if(provider == null)
			locationManager.addTestProvider(PROVIDER, false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
        
		locationManager.setTestProviderEnabled(PROVIDER, true);
        
        handler = new Handler();
        handler.postDelayed(t, FREQ);
	}
	
	Thread t = new Thread() {
		public void run() {
			Location location = new Location(PROVIDER);
			location.setTime(System.currentTimeMillis());
			location.setLatitude(10.0);
			location.setLongitude(20.0);
			log(DateFormat.format("MM-dd-kk mm:ss, Z", location.getTime()) + ": " + location.getLatitude() + ", " + location.getLongitude());
			locationManager.setTestProviderLocation(PROVIDER, location);
			handler.postDelayed(this, FREQ);
		};
	};
	
	@Override
	public void onDestroy() {
		handler.removeCallbacks(t);
		locationManager.setTestProviderEnabled(PROVIDER, false);
		locationManager.removeTestProvider(PROVIDER);

		super.onDestroy();
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void log(String message) {
		if(LOGGING_ON) {
			Log.d(LocationProviderService.class.getName(), message);
		}
	}
}
