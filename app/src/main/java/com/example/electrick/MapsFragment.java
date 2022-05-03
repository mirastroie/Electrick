package com.example.electrick;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import com.bumptech.glide.Glide;
import com.google.type.DateTime;;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
        db.collection("cars").whereEqualTo("available", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            EVs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("car", document.getId() + " => " + document);
                                EV ev = new EV();
                                ev.setId(document.getId());
                                ev.setBattery(document.getDouble("battery"));
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
                                db.collection("models").document(modelId).get().addOnCompleteListener(new OnCompleteListener() {

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
                                            model.setPhoto(documentSnapshot.getString("photo"));
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
            String battery_string = item.getBattery() + "%";
            battery.setText(battery_string);

        TextView seats = dialog.findViewById(R.id.car_seats);
        String seats_string = item.getModel().getSeats().intValue() + " adults";
        seats.setText(seats_string);

        TextView range = dialog.findViewById(R.id.car_range);
        String range_string = item.getModel().getRange() + " km";
        range.setText(range_string);

        TextView price = dialog.findViewById(R.id.price);
        String price_string = item.getModel().getPrice() + " RON/min";
        price.setText(price_string);



        ImageView image = dialog.findViewById(R.id.imageView);

        Glide.with(dialog.getContext())
                .load(item.getModel().getPhoto())
                .into(image);

        Button button  = (Button) dialog.findViewById(R.id.location_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri ="https://www.google.com/maps/search/?api=1&query=" + item.getPosition().latitude + "%2C" + item.getPosition().longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

                startActivity(intent);
            }
        });

        Button buttonR  = (Button) dialog.findViewById(R.id.reserve_button);
        buttonR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog2 = new Dialog(getContext());
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.reservation_popup);
                dialog2.show();
                dialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog2.getWindow().setGravity(Gravity.BOTTOM);

                EditText minutes = dialog2.findViewById(R.id.reservation_duration);
                TextView error_message = dialog2.findViewById(R.id.error_message);

                Button buttonR2  = (Button) dialog2.findViewById(R.id.reserve_button_2);
                buttonR2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Editable value = minutes.getText();

                        if (value.toString().equals("")){
                            String message = "Duration field cannot be empty";
                            error_message.setText(message);
                        }
                        else if (Integer.parseInt(value.toString()) > 360){
                            String message = "The reservation duration cannot be longer than 360 minutes";
                            error_message.setText(message);
                        }
                        else {
                            //get car data from firebase to check if the selected car is still available
                            db.collection("cars").document(item.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = (DocumentSnapshot) task.getResult();
                                        boolean available = documentSnapshot.getBoolean("available");
                                        if (available){
                                            //if available save reservation in firebase and set the car as unavailable
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            int duration = Integer.parseInt(value.toString());
                                            double price = item.getModel().getPrice() * duration;
                                            Timestamp localDateTime = Timestamp.now();

                                            Map<String, Object> data = new HashMap<>();
                                            data.put("date", localDateTime);
                                            data.put("duration", duration);
                                            data.put("ev", item.getId());
                                            data.put("totalPrice", price);
                                            data.put("user", user.getUid());

                                            db.collection("rentals").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()){
                                                        db.collection("cars").document(item.getId()).update("available",false);

                                                        PendingIntent pendingIntent = new NavDeepLinkBuilder(getContext())
                                                                .setGraph(R.navigation.nav_component)
                                                                .setDestination(R.id.profileFragment)
                                                                .createPendingIntent();
                                                        FirebaseMessageReceiver firebaseMessageReceiver = new FirebaseMessageReceiver();
                                                        firebaseMessageReceiver.showNotification("Reservation completed", "Click here to go to your profile", pendingIntent, getContext());

                                                        //show successful reservation screen
                                                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        if(ViewCompat.getRootWindowInsets(getView()).isVisible(WindowInsetsCompat.Type.ime())){
                                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                        }

                                                        dialog2.findViewById(R.id.before).setVisibility(View.GONE);
                                                        dialog2.findViewById(R.id.after).setVisibility(View.VISIBLE);

                                                        dialog2.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                dialog2.dismiss();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                    }
                                                }
                                            });



                                        }
                                        else{
                                            //if not available show error message
                                            String message = "The car you selected is no longer available. Please choose another car";
                                            error_message.setText(message);
                                        }
                                    }
                                }
                            });



                        }

                    }
                });

            }
        });

    }

}