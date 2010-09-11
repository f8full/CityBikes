/*
 * Copyright (C) 2010 Lluís Esquerda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.homelinux.penecoptero.android.citybikes.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class MainActivity extends MapActivity{
	
	private MapView mapView;
	private SharedPreferences settings;
	private Locator locator;
	
	private Context context;
	
	private HomeOverlay hOverlay;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		settings = getSharedPreferences(CityBikes.PREFERENCES_NAME,0);
		context = getApplicationContext();
		
		locator = new Locator(context, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Locator.LOCATION_CHANGED) {
					GeoPoint point = new GeoPoint(msg.arg1, msg.arg2);
					hOverlay.moveCenter(point);
					mapView.getController().animateTo(point);
				}
			}
		});
		
		mapView = (MapView) findViewById(R.id.mapview);
		hackZoom(mapView);
		applyMapViewLongPressListener(mapView);
		
		hOverlay = new HomeOverlay(locator.getCurrentGeoPoint(),new Handler(){
			@Override
			public void handleMessage(Message msg) {
				
			}
		});
		
		mapView.getOverlays().add(hOverlay);
	}
	
	@SuppressWarnings("deprecation")
	private void hackZoom(MapView mapView){
		RelativeLayout.LayoutParams zoomControlsLayoutParams = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		zoomControlsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		zoomControlsLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mapView.addView(mapView.getZoomControls(), zoomControlsLayoutParams);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
	    switch (item.getItemId()) {
		    case R.id.menu_item_location:
		        locator.unlockCenter();
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	
	protected void applyMapViewLongPressListener(MapView mapView) {
		final MapView finalMapView = mapView;
		
        final GestureDetector gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
                @Override
                public void onLongPress(MotionEvent e) {
                        Log.i("CityBikes","LONG PRESS!");
                        Projection astral = finalMapView.getProjection();
                        GeoPoint center = astral.fromPixels((int) e.getX(),(int) e.getY());
                        locator.lockCenter(center);
                }                      
        });
        mapView.setOnTouchListener(new OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent ev) {
                        return gd.onTouchEvent(ev);
                }
        });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}