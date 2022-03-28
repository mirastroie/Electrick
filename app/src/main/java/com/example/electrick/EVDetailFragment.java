package com.example.electrick;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Show info about a particular ev
 * TO DO: should receive a Car object as parameter (see ModelDetailFragment for reference)
 * */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EVDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EVDetailFragment extends Fragment {

    private static final String EV_KEY = "ev_key";
    private EV ev;
    public EVDetailFragment() {
        // Required empty public constructor
    }

    public static EVDetailFragment newInstance(EV ev) {
        EVDetailFragment fragment = new EVDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(EV_KEY, ev);
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
        ev = (EV) getArguments().getSerializable(EV_KEY);
        return inflater.inflate(R.layout.fragment_e_v_detail, container, false);
    }
}