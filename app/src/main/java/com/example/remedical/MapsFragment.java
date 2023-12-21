package com.example.remedical;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback, PlacesListener, View.OnClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private List<Marker> previous_marker = null;
    private LatLng fixedLocation;
    private ImageButton hospital, pharmacy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        previous_marker = new ArrayList<>();

        hospital = view.findViewById(R.id.hospital);
        pharmacy = view.findViewById(R.id.pharmacy);

        // 지도 프래그먼트 설정 및 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // 위치 권한 요청
        requestLocationPermission();
        // 이벤트 리스너 설정
        hospital.setOnClickListener(this);
        pharmacy.setOnClickListener(this);
        return view;
    }

    public void onClick(View v) {
        // 병원 또는 약국 버튼 클릭 처리
        if (v.getId() == R.id.hospital) {
            mMap.clear();
            new NRPlaces.Builder()
                    .listener(this)
                    .key("AIzaSyC51AtMlnu-5IPQ_1GaxAZ8da8RlsR9vJo") // Google Places API 키
                    .latlng(fixedLocation.latitude, fixedLocation.longitude)
                    .radius(1000) // 1km 내에서 검색
                    .type(PlaceType.HOSPITAL) // 병원 검색
                    .build()
                    .execute();
        } else if (v.getId() == R.id.pharmacy) {
        mMap.clear();
        new NRPlaces.Builder()
                .listener(this)
                .key("AIzaSyC51AtMlnu-5IPQ_1GaxAZ8da8RlsR9vJo") // Google Places API 키
                .latlng(fixedLocation.latitude, fixedLocation.longitude)
                .radius(1000) // 1km 내에서 검색
                .type(PlaceType.PHARMACY) // 약국 검색
                .build()
                .execute();
        }
    }

    private void requestLocationPermission() {
        // 위치 권한 요청 메서드
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 위치 권한 요청 결과 처리
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여되었다면 현재 위치 표시
                getCurrentLocationAndDisplay();
            } else {
                // 권한 거부 처리
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // 지도가 준비되었을 때 호출되는 콜백
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationAndDisplay();
        }
    }

    private void getCurrentLocationAndDisplay() {
        // 현재 위치를 가져와 지도에 표시하는 메서드
        fixedLocation = new LatLng(36.819603, 127.156207); //신세계백화점 천안아산점 좌표
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fixedLocation, 17));
        mMap.addMarker(new MarkerOptions().position(fixedLocation).title("현재 위치"));
        showPlaceInformation(fixedLocation);
//        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Location loc_Current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (loc_Current != null) {
//                Log.e("loc_Current", "Open");
//                fixedLocation = new LatLng(loc_Current.getLatitude(), loc_Current.getLongitude());
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fixedLocation, 15));
//                mMap.addMarker(new MarkerOptions().position(fixedLocation).title("현재 위치"));
//                showPlaceInformation(fixedLocation);
//            }
//        }
    }

    @Override
    public void onPlacesStart(){
        // 장소 검색 시작 처리
    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        // 장소 검색 성공 처리
        getActivity().runOnUiThread(() -> {
            for (Place place : places) {
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
            }
        });
    }
    @Override
    public void onPlacesFailure(PlacesException e) {
        // 장소 검색 실패 처리
        Log.e("MapsFragment", "Place search failed: " + e.getMessage());
    }

    @Override
    public void onPlacesFinished() {
        // 검색 완료 처리
    }

    public void showPlaceInformation(LatLng location) {
        // 지정된 위치 주변의 장소 정보 표시 메서드
        new NRPlaces.Builder()
                .listener(this)
                .key("AIzaSyC51AtMlnu-5IPQ_1GaxAZ8da8RlsR9vJo") // Google Places API 키
                .latlng(location.latitude, location.longitude)
                .radius(1000) // 1km 내에서 검색
                .type(PlaceType.HOSPITAL) // 병원 검색
                .build()
                .execute();
    }

}
