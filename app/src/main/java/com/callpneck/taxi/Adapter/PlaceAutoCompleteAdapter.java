package com.callpneck.taxi.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.taxi.TaxiMainActivity;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;

public class PlaceAutoCompleteAdapter
        extends RecyclerView.Adapter<PlaceAutoCompleteAdapter.PlacesViewHolder> {

    private Context mContext;
    private CharacterStyle styleBold;
    private CharacterStyle styleNormal;
    private List<AutocompletePrediction> predictions;

    public PlaceAutoCompleteAdapter(Context context) {
        mContext = context;
        styleBold = new StyleSpan(Typeface.BOLD);
        styleNormal = new StyleSpan(Typeface.NORMAL);
        predictions = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_location, viewGroup, false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder placesViewHolder, int i) {
        placesViewHolder.area.setText(predictions.get(i).getPrimaryText(styleBold).toString());
        placesViewHolder.address.setText(predictions.get(i).getSecondaryText(styleNormal).toString());
        placesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TaxiMainActivity)mContext).onItemClickAddressRecycler(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public List<AutocompletePrediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<AutocompletePrediction> predictions) {
        this.predictions = predictions;
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {

        private TextView area;
        private TextView address;

        PlacesViewHolder(View itemView) {
            super(itemView);
            area = itemView.findViewById(R.id.area);
            address = itemView.findViewById(R.id.address);
        }
    }
}
