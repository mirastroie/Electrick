package com.example.electrick;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModelDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModelDetailFragment extends Fragment {

    private static final String MODEL_KEY = "model_key";
    private Model model;
    TextView name, brand, range, seats;
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
        brand= (TextView) getView().findViewById(R.id.brand);
        range = (TextView) getView().findViewById(R.id.range);
        seats = (TextView) getView().findViewById(R.id.seats);
        name.setText(model.getName());
        brand.setText(model.getBrand());
        range.setText(model.getRange().toString());
        seats.setText(model.getSeats().toString());
    }
}