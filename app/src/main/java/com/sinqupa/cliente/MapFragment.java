package com.sinqupa.cliente;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinqupa.cliente.entity.Employee;
import com.sinqupa.cliente.utility.Utility;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private DatabaseReference databaseReference;
    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map,container,false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.viewMap);
        mapFragment.getMapAsync(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        databaseReference.child("Employee").orderByChild("activated").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (Marker marker : realTimeMarkers){
                    marker.remove();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Employee employee = snapshot.getValue(Employee.class);

                    Utility.locationEmployee = new Location(Utility.TITLE_MARKER_EMPLOYEE);
                    Utility.locationEmployee.setLatitude(employee.getLatitudeTravel());
                    Utility.locationEmployee.setLongitude(employee.getLongitudeTravel());

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(employee.getLatitudeTravel(),employee.getLongitudeTravel()));
                    tmpRealTimeMarkers.add(map.addMarker(markerOptions));
                }
                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
