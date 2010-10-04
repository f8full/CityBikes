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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class StationOverlay extends Overlay {
		
	private int status;
	private Handler handler;
	private boolean selected = false;

	public static final int BLACK_STATE = 0;
	public static final int RED_STATE = 1;
	public static final int YELLOW_STATE = 2;
	public static final int GREEN_STATE = 3;

	public static final int TOUCHED = 10;

	private int radiusInPixels;
	private int radiusInMeters;

	private static final int RED_STATE_MAX = 0;
	private static final int YELLOW_STATE_MAX = 8;

	private static final int RED_STATE_RADIUS = 80;
	private static final int YELLOW_STATE_RADIUS = 80;
	private static final int GREEN_STATE_RADIUS = 80;
	private static final int SELECTED_STATE_RADIUS = 120;

	private Paint currentPaint;
	private Paint currentBorderPaint;
	private Paint selectedPaint;

	private int position = -1;
	
	private Station station;

	public StationOverlay(Station station, boolean mode){
		this.station = station;
		this.initPaint();
		this.updateStatus(mode);
	}
	
	public StationOverlay(Station station){
		this.station = station;
		this.initPaint();
		this.updateStatus();
	}
	
	
	private void initPaint(){
		this.currentPaint = new Paint();
		this.currentBorderPaint = new Paint();
		this.selectedPaint = new Paint();

		this.currentPaint.setAntiAlias(true);

		this.currentBorderPaint.setStyle(Paint.Style.STROKE);
		this.currentBorderPaint.setStrokeWidth(4);

		this.selectedPaint = new Paint();
		this.selectedPaint.setARGB(75, 0, 0, 0);
		this.selectedPaint.setAntiAlias(true);
		this.selectedPaint.setStrokeWidth(4);
		this.selectedPaint.setStyle(Paint.Style.STROKE);
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return this.position;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public int getState() {
		return this.status;
	}
	
	public Station getStation(){
		return this.station;
	}
	
	public GeoPoint getCenter() {
		return this.station.getCenter();
	}
	
	public void updateStatus (boolean onGetMode){
		if (onGetMode){
			updateStatus();
		}else{
			if (station.getFree() > YELLOW_STATE_MAX) {
				this.status = GREEN_STATE;
				this.radiusInMeters = GREEN_STATE_RADIUS;
				this.currentPaint.setARGB(75, 168, 255, 87);
				this.currentBorderPaint.setARGB(100, 168, 255, 87);
			} else if (station.getFree() > RED_STATE_MAX) {
				this.status = YELLOW_STATE;
				this.radiusInMeters = YELLOW_STATE_RADIUS;
				this.currentPaint.setARGB(75, 255, 210, 72);
				this.currentBorderPaint.setARGB(100, 255, 210, 72);

			} else {
				this.status = RED_STATE;
				this.radiusInMeters = RED_STATE_RADIUS;
				this.currentPaint.setARGB(75, 240, 35, 17);
				this.currentBorderPaint.setARGB(100, 240, 35, 17);
			}
		}
	}

	public void updateStatus(){
		if (station.getBikes() > YELLOW_STATE_MAX) {
			this.status = GREEN_STATE;
			this.radiusInMeters = GREEN_STATE_RADIUS;
			this.currentPaint.setARGB(75, 168, 255, 87);
			this.currentBorderPaint.setARGB(100, 168, 255, 87);
		} else if (station.getBikes() > RED_STATE_MAX) {
			this.status = YELLOW_STATE;
			this.radiusInMeters = YELLOW_STATE_RADIUS;
			this.currentPaint.setARGB(75, 255, 210, 72);
			this.currentBorderPaint.setARGB(100, 255, 210, 72);

		} else {
			this.status = RED_STATE;
			this.radiusInMeters = RED_STATE_RADIUS;
			this.currentPaint.setARGB(75, 240, 35, 17);
			this.currentBorderPaint.setARGB(100, 240, 35, 17);
		}
	}

	
	public void setSelected(boolean selected) {
		this.selected = selected;
		updateStatus();
		if (this.selected) {
			this.radiusInMeters = SELECTED_STATE_RADIUS;
		}
			
	}
	
	public void setSelected(boolean selected, boolean mode) {
		this.selected = selected;
		updateStatus(mode);
		if (this.selected) {
			this.radiusInMeters = SELECTED_STATE_RADIUS;
		}
	}

	public void update() {
		// TODO Update aviability.. :D
		this.updateStatus();
	}

	private void calculatePixelRadius(MapView mapView) {
		this.radiusInPixels = (int) mapView.getProjection()
				.metersToEquatorPixels(this.radiusInMeters);
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		// TODO Auto-generated method stub
		return super.draw(canvas, mapView, shadow, when);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

		calculatePixelRadius(mapView);

		Projection astral = mapView.getProjection();
		Point screenPixels = astral.toPixels(this.getCenter(), null);

		RectF oval = new RectF(screenPixels.x - this.radiusInPixels,
				screenPixels.y - this.radiusInPixels, screenPixels.x
						+ this.radiusInPixels, screenPixels.y
						+ this.radiusInPixels);

		canvas.drawOval(oval, this.currentPaint);

		if (this.selected) {
			canvas.drawCircle(screenPixels.x, screenPixels.y,
					this.radiusInPixels, this.selectedPaint);
		} else {
			canvas.drawCircle(screenPixels.x, screenPixels.y,
					this.radiusInPixels, this.currentBorderPaint);
		}
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		// TODO Auto-generated method stub

		if ((p.getLatitudeE6() <= this.getCenter().getLatitudeE6() + 800 && p
				.getLatitudeE6() >= this.getCenter().getLatitudeE6() - 800)
				&& (p.getLongitudeE6() <= this.getCenter().getLongitudeE6() + 800 && p
						.getLongitudeE6() >= this.getCenter().getLongitudeE6() - 800)) {

			if (this.handler != null) {
				Message msg = new Message();
				msg.what = TOUCHED;
				msg.arg1 = this.position;
				msg.obj = this.station;
				this.handler.sendMessage(msg);
			}
		}

		return super.onTap(p, mapView);
	}

	public boolean getSelected() {
		return this.selected;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(e, mapView);
	}

}
