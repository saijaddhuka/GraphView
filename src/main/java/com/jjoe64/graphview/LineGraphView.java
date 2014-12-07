/**
 * This file is part of GraphView.
 *
 * GraphView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GraphView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GraphView.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 *
 * Copyright Jonas Gehring
 */

package com.jjoe64.graphview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

/**
 * Line Graph View. This draws a line chart.
 */
public class LineGraphView extends GraphView {

    private final float TOUCH_TOLERANCE = 5;
    private static final String TAG="LineGraphView";
	private final Paint paintBackground;
	private boolean drawBackground;
	private boolean drawDataPoints;
	private float dataPointsRadius = 10f;
    private double difY,minXX,minYY,difX;
    private float tempborder;
    private boolean onTouch=false;
    private double x,y;
    private String label;
    private float mX,mY;




	public LineGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paintBackground = new Paint();
		paintBackground.setColor(Color.rgb(20, 40, 60));
		paintBackground.setStrokeWidth(4);
		paintBackground.setAlpha(128);
	}

	public LineGraphView(Context context, String title) {
		super(context, title);

		paintBackground = new Paint();
		paintBackground.setColor(Color.rgb(20, 40, 60));
		paintBackground.setStrokeWidth(4);
		paintBackground.setAlpha(128);

	}


    @Override
	public void drawSeries(Canvas canvas, com.aurnhammer.fitnesstracker.views.GraphViewDataInterface[] values, float graphwidth, float graphheight, float border, double minX, double minY, double diffX, double diffY, float horstart, GraphViewSeriesStyle style) {
		// draw background
		double lastEndY = 0;
		double lastEndX = 0;
        difY=diffY;
        tempborder=border;
        minXX=minX;
        minYY=minY;
        difX=diffX;
        if(onTouch&&label!=null){
            Log.d(TAG, "drawing label" + label + "at " + x + "     " + y);
            //Toast.makeText(getContext(),label.substring(0,4),Toast.LENGTH_SHORT).show();

           canvas.drawText(label,(float)x,(float)y,paint);
            canvas.drawLine((float)x,graphheight+tempborder, (float)x, tempborder, paint);           //invalidate();
        }

		// draw data
		paint.setStrokeWidth(8);
		paint.setColor(Color.WHITE);


		Path bgPath = null;
		if (drawBackground) {
			bgPath = new Path();
		}

		lastEndY = 0;
		lastEndX = 0;
		float firstX = 0;
		for (int i = 0; i < values.length; i++) {
          //  Log.d(TAG,values[i].getY()+"    Y value"+values[i].getX()+" x value");
			double valY = values[i].getY() - minY;
			double ratY = valY / diffY;
			double y = graphheight * ratY;

			double valX = values[i].getX() - minX;
			double ratX = valX / diffX;
			double x = graphwidth * ratX;

			if (i > 0) {
				float startX = (float) lastEndX + (horstart + 1);
				float startY = (float) (border - lastEndY) + graphheight;
				float endX = (float) x + (horstart + 1);
				float endY = (float) (border - y) + graphheight;

				// draw data point
				if (drawDataPoints) {
                   // Log.d(TAG,endX+"    Y point"+endY+" X point");
					//fix: last value was not drawn. Draw here now the end values
					canvas.drawCircle(endX, endY, dataPointsRadius, paint);
				}

				canvas.drawLine(startX, startY, endX, endY, paint);
				if (bgPath != null) {
                    Log.d(TAG,"bgpath");
					if (i==1) {
						firstX = startX;
						bgPath.moveTo(startX, startY);
					}
					bgPath.lineTo(endX, endY);
				}
			} else if (drawDataPoints) {
				//fix: last value not drawn as datapoint. Draw first point here, and then on every step the end values (above)

				float first_X = (float) x + (horstart + 1);
				float first_Y = (float) (border - y) + graphheight;
				canvas.drawCircle(first_X, first_Y, dataPointsRadius, paint);

			}
			lastEndY = y;
			lastEndX = x;
		}
        paint.setStyle(Paint.Style.FILL);

		if (bgPath != null) {
			Log.d(TAG,"bgpath");
			bgPath.lineTo((float) lastEndX, graphheight + border);
			bgPath.lineTo(firstX, graphheight + border);
			bgPath.close();
			canvas.drawPath(bgPath, paintBackground);
		}

	}

	public int getBackgroundColor() {
		return paintBackground.getColor();
	}

	public float getDataPointsRadius() {
		return dataPointsRadius;
	}

	public boolean getDrawBackground() {
		return drawBackground;
	}

	public boolean getDrawDataPoints() {
		return drawDataPoints;
	}

	/**
	 * sets the background color for the series.
	 * This is not the background color of the whole graph.
	 * @see #setDrawBackground(boolean)
	 */
	@Override
	public void setBackgroundColor(int color) {
		paintBackground.setColor(color);
	}

	/**
	 * sets the radius of the circles at the data points.
	 * @see #setDrawDataPoints(boolean)
	 * @param dataPointsRadius
	 */
	public void setDataPointsRadius(float dataPointsRadius) {
		this.dataPointsRadius = dataPointsRadius;
	}

	/**
	 * @param drawBackground true for a light blue background under the graph line
	 * @see #setBackgroundColor(int)
	 */
	public void setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
	}

	/**
	 * You can set the flag to let the GraphView draw circles at the data points
	 * @see #setDataPointsRadius(float)
	 * @param drawDataPoints
	 */
	public void setDrawDataPoints(boolean drawDataPoints) {
		this.drawDataPoints = drawDataPoints;
	}

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouch=true;
                mX=event.getX();
                mY=event.getY();
                graphViewContentView.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(event.getX() - mX);
                float dy = Math.abs(event.getY() - mY);

                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    onTouch = true;
                    label = String.valueOf(getValuefromY(event.getY()));
                    x = event.getX();
                    y = event.getY();
                    graphViewContentView.invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                onTouch=false;
                label=null;
                graphViewContentView.invalidate();
                break;
            default:
                return false;
        }
        graphViewContentView.invalidate();
        return true;

    }
*/
    protected double getValuefromY(double y){
        /*double temp=(tempborder+(x-getGraphHeight()))/getGraphHeight();
        temp= (temp*difY)+getMinY();
        return temp;*/

       double sum=tempborder+getGraphHeight()-y;
        double temp= (sum*difY)/getGraphHeight();
        temp=temp+getMinY();
       return (int)Math.round(temp);

    }
}
