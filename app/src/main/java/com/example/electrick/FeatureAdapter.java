package com.example.electrick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder> {
    private Map<String, Integer> icons  = new HashMap<String, Integer>() {{
        put("Automatic Gearbox", R.drawable.ic_gearbox);
        put("Bluetooth", R.drawable.ic_bluetooth);
        put("Spotify", R.drawable.ic_spotify_brands);
        put("Glass Roof", R.drawable.ic_sun);
        put("Navigation", R.drawable.ic_navigation);
    }};
    private List<String> features;
    public FeatureAdapter(List<String> features) {
        this.features = features;
    }

    public class FeatureViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView icon;
        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.feature_item, viewGroup, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeatureViewHolder featureViewHolder, final int position) {
        String feature = features.get(position);
        featureViewHolder.name.setText(feature);
        featureViewHolder.icon.setImageResource(icons.get(feature));
    }
    @Override
    public int getItemCount() {
        return features.size();
    }
}
