package com.example.electrick;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ModelViewHolder> {
    private List<Model> models;
    private FirebaseFirestore db;
    private RecyclerViewClickListener listener;
    public ModelAdapter(List<Model> models, RecyclerViewClickListener listener) {
        this.models = models;
        this.listener = listener;
    }

    public class ModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView name, people;
        private final ImageView image;
        private final Context appContext;
        public ModelViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            people = (TextView) itemView.findViewById(R.id.people);
            image = (ImageView) itemView.findViewById(R.id.image);
            appContext = itemView.getContext().getApplicationContext();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ModelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_item, viewGroup, false);
        return new ModelViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ModelViewHolder modelViewHolder, final int position) {
        Model model = models.get(position);
        modelViewHolder.name.setText(String.format("%s %s", model.getBrand(), model.getName()));
        modelViewHolder.people.setText(String.format("%s Person car ride", model.getSeats().intValue()));
        Glide.with(modelViewHolder.appContext)
                .load(model.getPhoto())
                .into(modelViewHolder.image);

    }
    @Override
    public int getItemCount() {
        return models.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View view, int position);
    }
}
