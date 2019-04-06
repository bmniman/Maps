package com.example.hardikk.maplocation;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Interpolator;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

class DoubleArrayEvaluator implements TypeEvaluator<double[]> {

    private double[] mArray;

    /**
     * Create a DoubleArrayEvaluator that does not reuse the animated value. Care must be taken
     * when using this option because on every evaluation a new <code>double[]</code> will be
     * allocated.
     *
     * @see #DoubleArrayEvaluator(double[])
     */
    public DoubleArrayEvaluator() {
    }

    /**
     * Create a DoubleArrayEvaluator that reuses <code>reuseArray</code> for every evaluate() call.
     * Caution must be taken to ensure that the value returned from
     * {@link android.animation.ValueAnimator#getAnimatedValue()} is not cached, modified, or
     * used across threads. The value will be modified on each <code>evaluate()</code> call.
     *
     * @param reuseArray The array to modify and return from <code>evaluate</code>.
     */
    public DoubleArrayEvaluator(double[] reuseArray) {
        mArray = reuseArray;
    }

    /**
     * Interpolates the value at each index by the fraction. If
     * {@link #DoubleArrayEvaluator(double[])} was used to construct this object,
     * <code>reuseArray</code> will be returned, otherwise a new <code>double[]</code>
     * will be returned.
     *
     * @param fraction   The fraction from the starting to the ending values
     * @param startValue The start value.
     * @param endValue   The end value.
     * @return A <code>double[]</code> where each element is an interpolation between
     * the same index in startValue and endValue.
     */
    @Override
    public double[] evaluate(float fraction, double[] startValue, double[] endValue) {
        double[] array = mArray;
        if (array == null) {
            array = new double[startValue.length];
        }

        for (int i = 0; i < array.length; i++) {
            double start = startValue[i];
            double end = endValue[i];
            array[i] = start + (fraction * (end - start));
        }
        return array;
    }
}
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap,mMap1;
    double lat;
    double lng;
    Marker  marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lat= 22.555109;
        lng = 72.924639;
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * M anipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       // mMap1=googleMap;

        // Add a marker in Sydney and move the camera
        LatLng gls = new LatLng(23, 74);
//        marker = new MarkerOptions().position(gls).title("Marker in Sydney").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.dow)));
       marker= mMap.addMarker(new MarkerOptions().position(gls).title("Marker in Sydney").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.dow)));

// adding marker
       mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gls,18),5000,null);
//    run();


        double[] startValues = new double[]{lat, lng};
        double[] endValues = new double[]{23, 74};
        ValueAnimator latLngAnimator = ValueAnimator.ofObject(new DoubleArrayEvaluator(), startValues, endValues);
        latLngAnimator.setDuration(10000);
        latLngAnimator.setInterpolator(new DecelerateInterpolator());
        latLngAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                double[] animatedValue = (double[]) animation.getAnimatedValue();
                marker.setPosition(new LatLng(animatedValue[0], animatedValue[1]));
            }
        });
        latLngAnimator.start();
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(gls));
    }
    public void animateMarker(final Marker
                                      marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
    public void run()
    {
        mMap.clear();

        Thread t = new Thread() {


            @Override
            public void run() {

                while (!isInterrupted()) {

                    try {
                        Thread.sleep(1000);  //1000ms = 1 sec

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                lat += 1.50;
                                lng += 1;

                                LatLng gls = new LatLng(lat, lng);
                                mMap.addMarker(new MarkerOptions().position(gls).title("Your Parcel").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.dow)));
                                Log.i("vishal","l : " + lat);
//                                count++;
//                                textView.setText(String.valueOf(count));
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        t.start();
    }
}
