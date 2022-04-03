package com.example.electrick;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment implements OnMapReadyCallback,ClusterManager.OnClusterClickListener<MapItem>, ClusterManager.OnClusterInfoWindowClickListener<MapItem>, ClusterManager.OnClusterItemClickListener<MapItem>, ClusterManager.OnClusterItemInfoWindowClickListener<MapItem>  {
    private FirebaseFirestore db;
    private static final String TAG = MapsFragment.class.getSimpleName();


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        private ClusterManager<MapItem> clusterManager;
        private GoogleMap mMap;

        @Override
        public boolean onClusterClick(Cluster<MapItem> cluster) {

            String title = cluster.getItems().iterator().next().getTitle();
            Toast.makeText(getContext(), cluster.getSize() + " (including " + title + ")", Toast.LENGTH_SHORT).show();

            LatLngBounds.Builder builder = LatLngBounds.builder();
            for (ClusterItem item : cluster.getItems()) {
                builder.include(item.getPosition());
            }

            final LatLngBounds bounds = builder.build();

            try {
                getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

    private GoogleMap getMap() {
            return mMap;
    }

    @Override
        public void onClusterInfoWindowClick(Cluster<MapItem> cluster) {
            // Does nothing, but you could go to a list of the users.
        }

        @Override
        public boolean onClusterItemClick(MapItem item) {
            // Does nothing, but you could go into the user's profile page, for example.
            return false;
        }

        @Override
        public void onClusterItemInfoWindowClick(MapItem item) {
            // Does nothing, but you could go into the user's profile page, for example.
        }


        private void setUpClusterer(GoogleMap map, Context context) {
            LatLng bucharest = new LatLng(44.43225, 26.10626);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(bucharest, 11));

            clusterManager = new ClusterManager<MapItem>(context, map);

            clusterManager.setRenderer(new MarkerClusterRenderer(getContext(), map, clusterManager));

            map.setOnCameraIdleListener(clusterManager);
            map.setOnMarkerClickListener(clusterManager);
            map.setOnInfoWindowClickListener(clusterManager);
            clusterManager.setOnClusterClickListener(this);
            clusterManager.setOnClusterInfoWindowClickListener(this);
            clusterManager.setOnClusterItemClickListener(this);
            clusterManager.setOnClusterItemInfoWindowClickListener(this);

            // Add cluster items (markers) to the cluster manager.
            addItems();
            clusterManager.cluster();
        }


        private void addItems() {

            // Set some lat/lng coordinates to start with.
            double lat = 44.43225;
            double lng = 26.10626;

            // Add ten cluster items in close proximity, for purposes of this example.
            for (int i = 0; i < 10; i++) {
                double offset = i / 60d;
                lat = lat + offset;
                lng = lng + offset;
                MapItem offsetItem = new MapItem(lat, lng, "Title " + i, "Snippet " + i);
                clusterManager.addItem(offsetItem);
            }
        }


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

            setUpClusterer(googleMap,getContext());
        }


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
            mapFragment.getMapAsync(this);
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