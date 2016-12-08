package com.gumio_inf.android.walkingquest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import static com.gumio_inf.android.walkingquest.MainActivity.attack;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private SharedPreferences pref;

    private final static int LOCATION_PERMISSIONS = 1000;

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    private double latitude;
    private double longitude;

    private TextView attackText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        pref = getSharedPreferences("Data", Context.MODE_PRIVATE);

        attackText = (TextView)findViewById(R.id.attackText);
        attackText.setText("あなたの今の攻撃力：" + attack);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        createLocationRequest();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // latitude and longitude
        double latitude = 34.984996;
        double longitude = 135.752048;

        // 位置情報
        LatLng kcg = new LatLng(latitude, longitude);

        // 許可されたかのパーミッション
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSIONS);
            return;
        }
        // 現在地取得ボタン設置
        map.setMyLocationEnabled(true);

        // 地図の表示形式
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // マーカー立て
        //map.addMarker(new MarkerOptions().position(kcg).title("ここだよ!!!"));

        // 東京駅の位置、ズーム設定
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(kcg).zoom(18.5f).build();

        // 地図の中心の変更する
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            Log.d("latidude:", String.valueOf(latitude));
            Log.d("longitude:", String.valueOf(longitude));
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "残念、なんか繋がらないっぽい", Toast.LENGTH_SHORT).show();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2500);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        // 位置情報
        LatLng nowPlace = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        // 東京駅の位置、ズーム設定
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(nowPlace).zoom(18.5f).build();

        // 地図の中心の変更する
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Log.d("latidude:", String.valueOf(mCurrentLocation.getLatitude()));
        Log.d("longitude:", String.valueOf(mCurrentLocation.getLongitude()));
        attack += 20;
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("attack", attack);
        edit.apply();
        attackText.setText("あなたの今の攻撃力：" + attack);

        if (attack == 100) {
            Toast.makeText(this, "まだまだだね。そんなんじゃやってけないよ", Toast.LENGTH_SHORT).show();
        } else if (attack == 500) {
            Toast.makeText(this, "何浮かれてるの？さっさと走ってこい！！！", Toast.LENGTH_SHORT).show();
        } else if (attack == 1000) {
            Toast.makeText(this, "スライムくらいなら、倒せんじゃね？", Toast.LENGTH_SHORT).show();
        } else if (attack > 10000) {
            Toast.makeText(this, "ぎょえぇぇぇえぇえええ！！！！！", Toast.LENGTH_SHORT).show();
        }
    }
}
