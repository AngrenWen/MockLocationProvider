package pl.angrenwen.mocklocationprovider;

import pl.angrenwen.mocklocationmanager.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */

	private Intent service;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		service = new Intent(this, LocationProviderService.class);

		Button buttonStopService = (Button) findViewById(R.id.btn_stop_service);
		buttonStopService.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean result = stopService(service);
				if (result) {
					Toast.makeText(MainActivity.this,
							"Service stopped!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this,
							"No service running", Toast.LENGTH_SHORT).show();
				}
			}
		});

		Button buttonStartService = (Button) findViewById(R.id.btn_start_service);
		buttonStartService.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//boolean result =  == null ;

				if (isMyServiceRunning()) {
					Toast.makeText(MainActivity.this,
							"Service already running", Toast.LENGTH_SHORT).show();
				} else {
					startService(service);
					Toast.makeText(MainActivity.this,
							"Service started!", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}
	
	public boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (LocationProviderService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}