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

	/**
	 * Coordinates of Wroclaw, Poland: 51.11 N, 17.03 E
	 */
	private static final double INITIAL_LATITUDE = 51.11;
	private static final double INITIAL_LONGITUDE = 17.03;

	/**
	 * Minimal latitude/longitude difference between two consecutive points in
	 * degrees. 1 degree ~ 111km on the equator 10m ~ 0.00009 degree
	 * 
	 * 0.00002 degree ~ 2.22m
	 */
	private static final double MIN_DIFFERENCE = 0.00002;

	/**
	 * Maximal latitude/longitude difference between two consecutive points in
	 * degrees. 1 degree ~ 111km on the equator 10m ~ 0.00009 degree
	 * 
	 * 0.0002 degree ~ 22.2m
	 */
	private static final double MAX_DIFFERENCE = 0.0002;

	/**
	 * Minimal location accuracy of randomized points in meters.
	 */
	private static final double MIN_ACCURACY = 0;

	/**
	 * Maximal location accuracy of randomized points in meters.
	 */
	private static final double MAX_ACCURACY = 40;

	private LocationManager locationManager;
	private Handler handler;
	private Location lastLocation;

	/**
	 * To avoid completely random points couple of consecutive points will be in
	 * the same direction.
	 */
	private static final int MIN_POINTS_IN_ONE_DIRECTION = 3;
	private static final int MAX_POINTS_IN_ONE_DIRECTION = 15;

	private int pointsInOneDirectionCounter = 0;

	private double randomLatitude = 0;
	private double randomLongitude = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		lastLocation = new Location(PROVIDER);

		lastLocation.setTime(System.currentTimeMillis());
		lastLocation.setLatitude(INITIAL_LATITUDE);
		lastLocation.setLongitude(INITIAL_LONGITUDE);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationProvider provider = locationManager.getProvider(PROVIDER);

		if (provider == null)
			locationManager.addTestProvider(PROVIDER, false, false, false,
					false, false, false, false, Criteria.POWER_LOW,
					Criteria.ACCURACY_FINE);

		locationManager.setTestProviderEnabled(PROVIDER, true);

		handler = new Handler();
		handler.postDelayed(locationUpdateThread, FREQ);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(locationUpdateThread);
		locationManager.setTestProviderEnabled(PROVIDER, false);
		locationManager.removeTestProvider(PROVIDER);

		super.onDestroy();
	}

	Thread locationUpdateThread = new Thread() {

		public void run() {
			Location location = getRandomLocation();
			location.setTime(System.currentTimeMillis());

			log(DateFormat.format("yyyy-MM-dd kk:mm:ss, Z", location.getTime())
					+ ": " + location.getLatitude() + ", "
					+ location.getLongitude());

			locationManager.setTestProviderLocation(PROVIDER, location);
			lastLocation = location;
			handler.postDelayed(this, FREQ);
		};

	};

	private Location getRandomLocation() {

		if (pointsInOneDirectionCounter <= 0) {
			int sign = (Math.random() <= 0.5) ? -1 : 1;
			randomLatitude = sign
					* (Math.random() * (MAX_DIFFERENCE - MIN_DIFFERENCE) + MIN_DIFFERENCE);

			sign = (Math.random() < 0.5) ? -1 : 1;
			randomLongitude = sign
					* (Math.random() * (MAX_DIFFERENCE - MIN_DIFFERENCE) + MIN_DIFFERENCE);

			pointsInOneDirectionCounter = Math.round((float) (Math.random()
					* (MAX_POINTS_IN_ONE_DIRECTION - MIN_POINTS_IN_ONE_DIRECTION) + MIN_POINTS_IN_ONE_DIRECTION));
		}

		pointsInOneDirectionCounter -= 1;
		
		float randomAccuracy = (float) (Math.random()
				* (MAX_ACCURACY - MIN_ACCURACY) + MIN_ACCURACY);

		Location location = new Location(PROVIDER);
		location.setLatitude(randomLatitude + lastLocation.getLatitude());
		location.setLongitude(randomLongitude + lastLocation.getLongitude());
		location.setAccuracy(randomAccuracy);
		
		return location;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * If LOGGING_ON param is set to TRUE, the given message is sent as DEBUG
	 * log.
	 * 
	 * @param message
	 *            Message you would like to log.
	 */
	private void log(String message) {
		if (LOGGING_ON) {
			Log.d(LocationProviderService.class.getName(), message);
		}
	}
}
