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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.os.Handler;

import com.google.android.maps.Overlay;

public class StationOverlayList {

	private List<Overlay> mapOverlays;
	private Handler handler;
	private List<Overlay> shitOverlays;

	public StationOverlayList(List<Overlay> mapOverlays, Handler handler) {
		shitOverlays = new LinkedList <Overlay> ();
		this.mapOverlays = mapOverlays;
		this.handler = handler;
	}

	public List<Overlay> getList() {
		return mapOverlays;
	}
	
	public void addOverlay(Overlay overlay){
		/**
		 * Add any Overlay, it will be ignored in the "inner" working.
		 * A reference is also added in shitOverlays.
		 */
		mapOverlays.add(overlay);
		shitOverlays.add(overlay);
	}

	public void addStationOverlay(StationOverlay overlay) {
		/****
		 * Add a StationOverlay, set with the predefined Station Handler
		 */
		overlay.setHandler(handler);
		mapOverlays.add(overlay);
	}
	
	public void setOverlayList(List<Overlay> list){
		mapOverlays = list;
	}

	
	public void clearStationOverlays() {
		mapOverlays.clear();
		for (Iterator <Overlay> io = shitOverlays.iterator(); io.hasNext();){
			mapOverlays.add(io.next());
		}
	}
}