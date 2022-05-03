package com.example.electrick;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModelDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModelDetailFragment extends Fragment {

    private static final String MODEL_KEY = "model_key";
    private Model model;
    private FeatureAdapter featureAdapter;
    private RecyclerView featureRv;
    private List<String> features;
    TextView name, range, seats, price, overview;
    ImageView image;
    private MaterialButton backButton, shareButton;
    public ModelDetailFragment() {
        // Required empty public constructor
    }

    public static ModelDetailFragment newInstance(Model model) {
        ModelDetailFragment fragment = new ModelDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(MODEL_KEY, model);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        model = (Model) getArguments().getSerializable(MODEL_KEY);
        return inflater.inflate(R.layout.fragment_model_detail, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        name = (TextView) getView().findViewById(R.id.name);
        range = (TextView) getView().findViewById(R.id.range);
        seats = (TextView) getView().findViewById(R.id.seats);
        image = (ImageView) getView().findViewById(R.id.bg);
        price = (TextView) getView().findViewById(R.id.price);
        overview = (TextView) getView().findViewById(R.id.overview);
        backButton = (MaterialButton) getView().findViewById(R.id.buttonBack);
        name.setText(String.format("%s %s", model.getBrand(), model.getName()));
        range.setText(String.format("%s km", model.getRange()));
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //getFragmentManager().popBackStack();
                getActivity().onBackPressed();
            }
        });
//        range.setText(String.format("%s km\nrange", model.getRange()));
        overview.setText(model.getOverview());
        seats.setText(String.format("%s adults", model.getSeats().intValue()));
        price.setText(String.format("%s RON/min", model.getPrice()));
        Glide.with(getView().getContext().getApplicationContext())
                .load(model.getProfileBg())
                .into(image);

        featureRv = getView().findViewById(R.id.featureRV);
        featureRv.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        featureRv.setLayoutManager(layoutManager);
        features = model.getFeatures();
        featureAdapter = new FeatureAdapter(features);
        featureRv.setAdapter(featureAdapter);

        shareButton = getView().findViewById(R.id.buttonShare);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

                sendIntent.putExtra(Intent.EXTRA_TEXT,"Now available on Electrick: "+model.getBrand() + " " + model.getName()+". "+ model.getOverview()+ ". Get the app: link_to_play_store_or_something_else");

                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share using"));

            }
        });
    }
}