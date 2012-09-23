package com.hillbomber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MainActivity extends MapActivity {

	private LocationManager locationManager;
	private LocationListener locationListener;

	private MapView mapView;

	private MapController mapController;
	private GoogleParser googleParser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mapView = (MapView) findViewById(R.id.mapview);
		googleParser = new GoogleParser();

		BufferedReader in = null;
		String line = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					getConnection("http://hillbomber.herokuapp.com/trails.json")));
			while ((line = in.readLine()) != null) {
				JSONArray routes = (JSONArray) new JSONTokener(line)
						.nextValue();
				for (int i = 0; i < routes.length(); i++) {
					JSONObject route = routes.getJSONObject(i);
                    String url = googleParser.directions(new GeoPoint((int)(route.getDouble("s_lat")*1E6),(int)(route.getDouble("s_long")*1E6)), new GeoPoint((int)(route.getDouble("e_lat")*1E6),(int)(route.getDouble("e_long")*1E6)));
                    RouteOverlay routeOverlay = new RouteOverlay(googleParser.parse(getConnection(url)), Color.BLUE);
                    mapView.getOverlays().add(routeOverlay);
				}
				mapView.invalidate();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		mapView = (MapView) findViewById(R.id.mapview);
		mapController = mapView.getController();
		mapController.setZoom(18); // Fixed Zoom Level

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LongboardLocationListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);

		// Get the current location in start-up
		centerLocation(new GeoPoint((int) (locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER)
				.getLatitude() * 1000000), (int) (locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER)
				.getLongitude() * 1000000)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private InputStream getConnection(String url) {
		InputStream is = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			is = conn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}

	private class LongboardLocationListener implements LocationListener {

		public void onLocationChanged(Location argLocation) {
			// TODO Auto-generated method stub
			GeoPoint geoPoint = new GeoPoint(
					(int) (argLocation.getLatitude() * 1000000),
					(int) (argLocation.getLongitude() * 1000000));

			centerLocation(geoPoint);
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}

	private void centerLocation(GeoPoint centerGeoPoint) {
		mapController.animateTo(centerGeoPoint);
	}

}
