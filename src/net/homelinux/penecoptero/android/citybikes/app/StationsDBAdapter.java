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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.homelinux.penecoptero.android.citybikes.utils.CircleHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;


public class StationsDBAdapter implements Runnable {

	private Handler handlerOut;
	
	private StationOverlayList stationsDisplayList;
	
	private List<StationOverlay> stationsMemoryMap;
	
	private Queue<Integer> toDo;
	
	private GeoPoint center;
	
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
		timestamp = json_station.getString("timestamp");
		bikes = json_station.getInt("bikes");
		free = json_station.getInt("free");
		return new Station(id, name, bikes, free, timestamp, new GeoPoint(lat, lng));
	}
	
	public void buildMemory(JSONArray stations) throws Exception{
		stationsMemoryMap = new LinkedList<StationOverlay>();
		StationOverlay tmp;
		for (int i = 0; i < stations.length(); i++){
			tmp = new StationOverlay(parseStation(stations.getJSONObject(i)));
			if (center != null)
				tmp.getStation().setMetersDistance(CircleHelper.gp2m(center, tmp.getStation().getCenter()));
			stationsMemoryMap.add(tmp);
		}
	}
	
	public void orderMemory(GeoPoint center) throws Exception{
		StationOverlay tmp;
		for (Iterator<StationOverlay> so = stationsMemoryMap.iterator(); so.hasNext();){
			tmp = so.next();
			tmp.getStation().setMetersDistance(CircleHelper.gp2m(center, tmp.getStation().getCenter()));
		}
	}
	
	public void filterDisplayList(int radius) throws Exception{
		stationsDisplayList.clearStationOverlays();
		StationOverlay tmp;
		for (Iterator<StationOverlay> so = stationsMemoryMap.iterator(); so.hasNext();){
			tmp = so.next();
			if ((tmp.getStation().getMetersDistance() + tmp.getStation().getMetersDistance() * 0.35) <= radius)
				stationsDisplayList.addStationOverlay(tmp);
		}
	}
	
	public void allDisplayList() throws Exception{
		stationsDisplayList.clearStationOverlays();
		for (Iterator<StationOverlay> so = stationsMemoryMap.iterator(); so.hasNext();){
			stationsDisplayList.addStationOverlay(so.next());
		}
	}
	
	@Override
	public void run() {
	
		
	}
	
	public void doShit() throws Exception{
		String stations = fetchStations("http://penecoptero.homelinux.net/citybikes/site/site.py/api/bicing.json");
		buildMemory(new JSONArray(stations));
		orderMemory(center);
		filterDisplayList(1000);
	}
}
