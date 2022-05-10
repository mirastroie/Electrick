package com.example.electrick;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EVsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EVsFragment extends Fragment{

    private FirebaseFirestore db;
    private List<Model> models;
    private ModelAdapter modelAdapter;
    private RecyclerView modelRV;
    private SearchView searchBar;
    private ModelAdapter.RecyclerViewClickListener listener;
    public EVsFragment() {
        // Required empty public constructor
    }

    public static EVsFragment newInstance(String param1, String param2) {
        EVsFragment fragment = new EVsFragment();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.fragment_e_vs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        modelRV = getView().findViewById(R.id.modelRV);
        db = FirebaseFirestore.getInstance();
        models = new ArrayList<>();
        modelRV.setHasFixedSize(true);
        modelRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        setOnClickListener();
        modelAdapter = new ModelAdapter(models, listener);
        modelRV.setAdapter(modelAdapter);
        searchBar = getView().findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(modelAdapter != null)
                  modelAdapter.getFilter().filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(modelAdapter != null)
                   modelAdapter.getFilter().filter(s);
                return true;
            }
        });

        db.collection("models")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Model m = new Model();
                                m.setSeats(document.getDouble("seats"));
                                m.setBrand(document.getString("brand"));
                                m.setName(document.getString("name"));
                                m.setPhoto(document.getString("photo"));
                                m.setRange(document.getDouble("range"));
                                m.setPrice(document.getDouble("price"));
                                m.setId(document.getId());
                                m.setFeatures((List<String>)document.get("features"));
                                m.setProfileBg(document.getString("profileBg"));
                                m.setOverview(document.getString("overview"));
                                Log.d("tagexp", document.getId() + " => " + document.getData());
                                models.add(m);
                            }
                             modelAdapter.notifyDataSetChanged();
                             modelAdapter.updateFullModels();
                        } else {
                           // Toast.makeText(.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void setOnClickListener(){
        listener = new ModelAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                // we change the fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("model_key", models.get(position));
                Navigation.findNavController(view).navigate(R.id.action_EVsFragment_to_modelDetailFragment, bundle);
            }
        };
    }
}