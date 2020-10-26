package com.shilo.myloginfirebase.mainactivity.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shilo.myloginfirebase.R;
import com.shilo.myloginfirebase.databinding.SoldiersRecyclerViewRawBinding;
import com.shilo.myloginfirebase.mainactivity.model.Soldier;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<Soldier> soliders = new ArrayList<>();
    private RecyclerViewClickListener listener;

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SoldiersRecyclerViewRawBinding rawBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                        R.layout.soldiers_recycler_view_raw, parent, false);
        ViewHolder viewHolder = new ViewHolder(rawBinding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        Soldier soldier = soliders.get(position);
        holder.rawBinding.setSoldier(soldier);
    }

    @Override
    public int getItemCount() {
        return soliders.size();
    }

    public void setSoldiers(List<Soldier> soliders) {
        this.soliders = soliders;
        notifyDataSetChanged();
    }

    public Soldier getSoldierAt(int position) {
        return soliders.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        SoldiersRecyclerViewRawBinding rawBinding;

        public ViewHolder(@NonNull SoldiersRecyclerViewRawBinding rawBinding) {
            super(rawBinding.getRoot());
            this.rawBinding = rawBinding;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onclick(soliders.get(position));
                    }
                }
                });
        }



    }

    public interface RecyclerViewClickListener {
        void onclick(Soldier soldier);
    }

    public void setOnRVClickListener(RecyclerViewClickListener listener){
        this.listener = listener;
    }
}
