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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;

import com.google.android.maps.GeoPoint;


public class StationsDBAdapter implements Runnable {

	public static final int FETCH = 0;
	public static final int UPDATE_MAP = 1;
	public static final int UPDATE_MAP_LESS = 2;
	public static final int UPDATE_DATABASE = 3;
	public static final int NETWORK_ERROR = 4;
	public static final String TIMESTAMP_FORMAT = "HH:mm:ss dd/MM/yyyy";
	
	private Handler handlerOut;
	private StationOverlayList stationsDisplayList;
	private List<StationOverlay> stationsMemoryMap;
	private Queue<Integer> toDo;
	private GeoPoint center;
	private String last_updated;	
	private RESTHelper connector;
	
	public StationsDBAdapter (Handler handler, StationOverlayList stationsDisplayList){
		this.handlerOut = handler;
		this.stationsDisplayList = stationsDisplayList;
		this.connector = new RESTHelper(false, null, null);
		this.toDo = new LinkedList<Integer>();
		this.center = null;
	}
	
	public StationsDBAdapter (Handler handler, StationOverlayList stationsDisplayList, GeoPoint center){
		this.handlerOut = handler;
		this.stationsDisplayList = stationsDisplayList;
		this.connector = new RESTHelper(false, null, null);
		this.toDo = new LinkedList<Integer>();
		this.center = center;
	}
	
	public void setCenter(GeoPoint point){
		center = point;
	}
	
	public String fetchStations(String url) throws Exception {
		return connector.restGET(url);
	}
	
	public Station parseStation(JSONObject json_station) throws Exception{
		int id, lat, lng, bikes, free;
		String name, timestamp;
		
		id = json_station.getInt("id");
		name = json_station.getString("name");
		lat = Integer.parseInt(json_station.getString("lat"));
		lng = Integer.parseInt(json_station.getString("lng"));
		
		
	}
	private void buildMemory(JSONArray stations) throws Exception{
		stationsMemoryMap = new LinkedList<StationOverlay>();
		
		
	}
	@Override
	public void run() {
	
		
	}
}
