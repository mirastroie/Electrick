package com.example.electrick;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment {
    private FirebaseFirestore db;
    private static final String TAG = MapsFragment.class.getSimpleName();
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */


        @Override

        public void onMapReady(GoogleMap googleMap) {
            try {

                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.map_style));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }


            LatLng bucharest = new LatLng(44.43225, 26.10626);
            googleMap.addMarker(new MarkerOptions().position(bucharest).title("Bucharest"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bucharest,11));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TO DO: for each car load its model and WAIT for the response from firestore
        db = FirebaseFirestore.getInstance();
        db.collection("cars")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<EV> EVs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                EV ev = new EV();
                                ev.setCapacity(document.getDouble("capacity"));
                                // set location
                                String modelId = document.getString("model");
                                ev.setId(document.getId());
                                // we have to call async load Model from database with modelId
                                EVs.add(ev);

                            }
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
}