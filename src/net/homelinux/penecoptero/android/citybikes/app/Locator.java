package net.homelinux.penecoptero.android.citybikes.app;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.maps.GeoPoint;

public class Locator {
	public static final int LOCATION_CHANGED = 101;
	
	private Handler handler;
	private Location currentLocation;
	private GeoPoint currentGeoPoint;
	
	private List<LocationListener> listeners;
	
	private LocationManager locationManager;
	
	public Locator(Context context, Handler handler){
		this.handler = handler;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		update(getLastKnownLocation());
		startUpdates();
	}
	
	public Location getLastKnownLocation() {
		Location location = locationManager.getLastKnownLocation("gps");
		if (location == null) {
			location = locationManager.getLastKnownLocation("network");
		}
		return location;
	}
	
	public GeoPoint getCurrentGeoPoint(){
		return currentGeoPoint;
	}
	
	public Location getCurrentLocation(){
		return currentLocation;
	}
	
	public void startUpdates(){
		listeners = new LinkedList<LocationListener>();
		LocationListener ll;
		
		for (Iterator<String> i = locationManager.getProviders(true).iterator(); i.hasNext(); ){
			ll = new LocationListener(){

				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub
					update(location);
				}

				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub
					
				}
			};
			listeners.add(ll);
			locationManager.requestLocationUpdates(i.next(), 60000, 25,ll);
		}
	}
	
	public void stopUpdates(){
		for( Iterator<LocationListener> ll = listeners.iterator(); ll.hasNext(); ){
			locationManager.removeUpdates(ll.next());
		}
	}
	
	public void restartUpdates(){
		stopUpdates();
		startUpdates();
	}
	
	private void update(Location newLocation){
		currentLocation = newLocation;
		currentGeoPoint = new GeoPoint((int) (currentLocation.getLatitude()*1E6), (int) (currentLocation.getLongitude()*1E6)); 
		Message msg = new Message();
		msg.what = LOCATION_CHANGED;
		msg.arg1 = currentGeoPoint.getLatitudeE6(); 
		msg.arg2 = currentGeoPoint.getLongitudeE6();
		msg.obj = currentLocation;
		handler.sendMessage(msg);
	}
}
