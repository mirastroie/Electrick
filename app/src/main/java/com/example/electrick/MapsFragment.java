package com.example.electrick;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment implements OnMapReadyCallback,ClusterManager.OnClusterClickListener<EV>, ClusterManager.OnClusterInfoWindowClickListener<EV>, ClusterManager.OnClusterItemClickListener<EV>, ClusterManager.OnClusterItemInfoWindowClickListener<EV>  {
    private FirebaseFirestore db;
    private static final String TAG = MapsFragment.class.getSimpleName();
    private ClusterManager<EV> clusterManager;
    private GoogleMap mMap;
    private ArrayList<EV> EVs;
    private ModelAdapter modelAdapter;
    private RecyclerView modelRV;
    private ModelAdapter.RecyclerViewClickListener listener;


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
        public boolean onClusterClick(Cluster<EV> cluster) {


            LatLngBounds.Builder builder = LatLngBounds.builder();
            for (ClusterItem item : cluster.getItems()) {
                builder.include(item.getPosition());
            }

            final LatLngBounds bounds = builder.build();

            try {
                getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300,300,0));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }



        private GoogleMap getMap() {
            return mMap;
    }

        @Override
        public void onClusterInfoWindowClick(Cluster<EV> cluster) {

        }

        @Override
        public boolean onClusterItemClick(EV item) {
            getMap().moveCamera(CameraUpdateFactory.newLatLng(item.getPosition()));

            showDialog(item);
            return true;
        }


        @Override
        public void onClusterItemInfoWindowClick(EV item) {

        }


        private void setUpClusterer(GoogleMap map, Context context) {
            LatLng bucharest = new LatLng(44.43225, 26.10626);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(bucharest, 11));

            clusterManager = new ClusterManager<EV>(context, map);

            clusterManager.setRenderer(new MarkerClusterRenderer(getContext(), map, clusterManager));

            map.setOnCameraIdleListener(clusterManager);
            map.setOnMarkerClickListener(clusterManager);
            map.setOnInfoWindowClickListener(clusterManager);
            clusterManager.setOnClusterClickListener(this);
            clusterManager.setOnClusterInfoWindowClickListener(this);
            clusterManager.setOnClusterItemClickListener(this);
            clusterManager.setOnClusterItemInfoWindowClickListener(this);

            clusterManager.cluster();
        }




        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
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
    public void onDestroyView() {
        super.onDestroyView();
        clusterManager.clearItems();
        clusterManager.cluster();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        db = FirebaseFirestore.getInstance();
        db.collection("cars")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            EVs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("car", document.getId() + " => " + document);
                                EV ev = new EV();
                                ev.setCapacity(document.getDouble("capacity"));
                                GeoPoint geoPoint = document.getGeoPoint("location");
                                LatLng latLng = null;
                                if (geoPoint != null) {
                                    latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                }
                                ev.setLocation(latLng);
                                String modelId = document.getString("model");
                                ev.setId(document.getId());
                                Log.d("modelid",document.getString("model"));
                                // we have to call async load Model from database with modelId
                                db.collection("models").document("BP7gPsbDqvIjtfdAwFSg").get().addOnCompleteListener(new OnCompleteListener() {

                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            Model model = new Model();
                                            DocumentSnapshot documentSnapshot = (DocumentSnapshot) task.getResult();
                                            model.setId(documentSnapshot.getId());
                                            model.setBrand(documentSnapshot.getString("brand"));
                                            model.setName(documentSnapshot.getString("name"));
                                            model.setRange(documentSnapshot.getDouble("range"));
                                            model.setPrice(documentSnapshot.getDouble("price"));
                                            model.setSeats(documentSnapshot.getDouble("seats"));
                                            Log.d("modelDetails",  " => " + documentSnapshot.getData());

                                            ev.setModel(model);
                                        }
                                    }

                                });
                                EVs.add(ev);

                                // add marker on map
                                addMarker(ev);


                            }
                            getMap().animateCamera(CameraUpdateFactory.zoomBy(0.5f));
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void addMarker(EV ev){

        clusterManager.addItem(ev);
    }

    private void showDialog(EV item){
            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_popup);
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);

            TextView title = dialog.findViewById(R.id.car_title);
            String title_string = item.getModel().getBrand() + " " +  item.getModel().getName();
            title.setText(title_string);

            TextView battery = dialog.findViewById(R.id.battery);
            String battery_string = "Battery: " + item.getCapacity() + "%";
            battery.setText(battery_string);

        TextView seats = dialog.findViewById(R.id.car_seats);
        String seats_string = "Seats: " + item.getModel().getSeats().intValue();
        seats.setText(seats_string);

        TextView range = dialog.findViewById(R.id.car_range);
        String range_string = "Range: " + item.getModel().getRange() + " km";
        range.setText(range_string);

        TextView price = dialog.findViewById(R.id.price);
        String price_string = "Price: " +  item.getModel().getPrice() + " RON/min";
        price.setText(price_string);

        TextView location = dialog.findViewById(R.id.location);
        String location_string = "Location: " + item.getPosition();
        location.setText(location_string);


    }
}