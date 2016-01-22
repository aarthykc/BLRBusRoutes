package com.aarthychandrasekhar.busroutes;

import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MapView myMapView = null;
    LocationManager mLocationManager;
    ArrayList<LatLng> points = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myMapView = (MapView) findViewById(R.id.mapboxMapView);
        myMapView.setAccessToken("pk.eyJ1IjoiYWFydGh5a2MiLCJhIjoiNTA2NWNkNTg5NTE0MGY5MmIxODkwZmY0MDhkYzFmMWYifQ.F8vMO8hrFUXZyntsz3zLQw");
        myMapView.setCenterCoordinate(new LatLng(12.9667, 77.5667));
        myMapView.setStyle(Style.DARK);
        myMapView.setZoomLevel(11);
        myMapView.onCreate(savedInstanceState);
        try {
            InputStream inputStream = getAssets().open("busstop.geojson");
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }

            inputStream.close();

            JSONObject json = new JSONObject(sb.toString());
            JSONArray coordinates = json.getJSONObject("geometry").getJSONArray("coordinates");
            Log.e("coordinates", coordinates.toString());
            for (int lc = 0; lc < coordinates.length(); lc++) {
                JSONArray coord = coordinates.getJSONArray(lc);
                LatLng latLng = new LatLng(coord.getDouble(1), coord.getDouble(0));
                points.add(latLng);
            }
            if (points.size() > 0) {
                LatLng[] pointsArray = points.toArray(new LatLng[points.size()]);

                // Draw Points on MapView
                Log.d("ohno", myMapView.addPolyline(new PolylineOptions()
                        .add(pointsArray)
                        .color(Color.parseColor("#ffff00"))
                        .width(2)).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        TestAdapter mDbHelper = new TestAdapter(MainActivity.this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        Cursor testdata = mDbHelper.getTestData();
        if (testdata.moveToFirst()) {
            while (!testdata.isAfterLast()) {
                String name = testdata.getString(testdata.getColumnIndex("name"));
                String dataLat = testdata.getString(testdata.getColumnIndex("lat"));
                String dataLong = testdata.getString(testdata.getColumnIndex("lng"));
                Log.e("data", name+ " " +dataLat + " " + dataLong);
                testdata.moveToNext();
            }
        }
        testdata.close();
        mDbHelper.close();


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
    }


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String lat = String.valueOf(latitude);
            String lon = String.valueOf(longitude);

        }


        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}