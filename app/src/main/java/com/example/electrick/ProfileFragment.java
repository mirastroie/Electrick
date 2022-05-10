package com.example.electrick;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private List<Rental> rentals;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private AppCompatButton button;
    private TextView helloMessage;
    private RecyclerView rentalRV;
    private RentalAdapter rentalAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        button = (AppCompatButton) getView().findViewById(R.id.buttonLogOut);
        helloMessage = (TextView) getView().findViewById(R.id.helloMessage);
        helloMessage.setText(String.format("Hello, %s", (currentUser.getDisplayName() != "" && currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "stranger!" )));
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        rentalRV = getView().findViewById(R.id.rentalsRV);
        rentals = new ArrayList<>();
        rentalRV.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rentalRV.setLayoutManager(layoutManager);
        rentalAdapter = new RentalAdapter(rentals);
        rentalRV.setAdapter(rentalAdapter);

        db = FirebaseFirestore.getInstance();
        db.collection("rentals").whereEqualTo("user", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                Rental rental = new Rental();
                                rental.setUserId(document.getString("user"));
                                rental.setDuration(document.getDouble("duration"));
                                rental.setTotalPrice(document.getDouble("totalPrice"));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    rental.setDate(Instant.ofEpochMilli(document.getDate("date") .getTime())
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDateTime());
                                }
                                db.collection("cars").document(document.getString("ev")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = (DocumentSnapshot) task.getResult();
                                            EV ev = new EV();
                                            ev.setBattery(documentSnapshot.getDouble("capacity"));
                                            GeoPoint geoPoint = documentSnapshot.getGeoPoint("location");
                                            LatLng latLng = null;
                                            if (geoPoint != null) {
                                                latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                            }
                                            ev.setLocation(latLng);
                                            db.collection("models").document(documentSnapshot.getString("model")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot documentSnapshot1 = (DocumentSnapshot) task.getResult();
                                                    Model model = new Model();
                                                    model.setId(documentSnapshot1.getId());
                                                    model.setBrand(documentSnapshot1.getString("brand"));
                                                    model.setName(documentSnapshot1.getString("name"));
                                                    model.setRange(documentSnapshot1.getDouble("range"));
                                                    model.setPrice(documentSnapshot1.getDouble("price"));
                                                    model.setSeats(documentSnapshot1.getDouble("seats"));
                                                    model.setPhoto(documentSnapshot1.getString("photo"));
                                                    ev.setModel(model);
                                                    rental.setEv(ev);
                                                    rentals.add(rental);
                                                    rentalAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }
                                });

                                // should we have a state manager for each entity so they can be
                                // accesed globally - kinda like redux - too much for solving too little


                            }

                        }
                    }
                });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}