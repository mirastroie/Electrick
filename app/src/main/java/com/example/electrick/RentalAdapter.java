package com.example.electrick;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.time.format.DateTimeFormatter;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RentalAdapter extends RecyclerView.Adapter<RentalAdapter.RentalViewHolder>{
    private List<Rental> rentals;
    public RentalAdapter(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public class RentalViewHolder extends RecyclerView.ViewHolder {
        private final TextView date, totalPrice, carName, duration;
        private final ImageView carImage;
        private final Context appContext;
        public RentalViewHolder(@NonNull View itemView) {
            super(itemView);
            // get references id
            date = (TextView) itemView.findViewById(R.id.date);
            totalPrice = (TextView) itemView.findViewById(R.id.totalPrice);
            carName = (TextView) itemView.findViewById(R.id.carName);
            carImage = (ImageView) itemView.findViewById(R.id.carImage);
            duration = (TextView)itemView.findViewById(R.id.duration);
            appContext = itemView.getContext().getApplicationContext();
        }
    }
    @NonNull
    @Override
    public RentalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rental_item, viewGroup, false);
        return new RentalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RentalViewHolder rentalViewHolder, final int position) {
        Rental rental = rentals.get(position);
        if(rental.getEv() != null && rental.getEv().getModel() != null) {
            Model model = rental.getEv().getModel();
            rentalViewHolder.carName.setText(String.format("%s %s", model.getBrand(), model.getName()));
            Glide.with(rentalViewHolder.appContext)
                    .load(model.getPhoto())
                    .into(rentalViewHolder.carImage);
        }
        rentalViewHolder.duration.setText(String.format("%dh %02dm", rental.getDuration().intValue()/60, rental.getDuration().intValue()%60));
        rentalViewHolder.totalPrice.setText(String.format("%s RON",rental.getTotalPrice().toString()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            rentalViewHolder.date.setText(rental.getDate().format(DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm")));
        };
    }
    @Override
    public int getItemCount() {
        return rentals.size();
    }
}
