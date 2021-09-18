package com.example.connect.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connect.Model.InterestModel;
import com.example.connect.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.MyViewHolder> {
    Context context;
    List<InterestModel> interestModelList;

    public InterestAdapter(Context context, List<InterestModel> interestModelList) {
        this.context = context;
        this.interestModelList = interestModelList;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InterestAdapter.MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.layout_interest,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Resources res = context.getResources();
        final InterestModel interestModel = interestModelList.get(position);
        final int newColor = res.getColor(R.color.colorImage);
       Glide.with(context).load(interestModelList.get(position).getImage()).into(holder.imageViewInterest);
        holder.textViewInterestName.setText(interestModelList.get(position).getName());
        holder.imageViewInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interestModel.setSelected(!interestModel.isSelected());
                if (interestModel.isSelected()){

                    Glide.with(context).load(interestModelList.get(position).getImage()).into(holder.imageViewInterest);
                    holder.imageViewInterest.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
                    holder.imageViewCheck.setVisibility(View.VISIBLE);

                }

                else
                {
                    Glide.with(context).load(interestModelList.get(position).getImage()).into(holder.imageViewInterest);
                    holder.imageViewCheck.setVisibility(View.GONE);

                    holder.imageViewInterest.setColorFilter(null);
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return interestModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Unbinder unbinder;

        @BindView(R.id.imageView_interest)
        ImageView imageViewInterest;

        @BindView(R.id.txt_interest_name)
        TextView textViewInterestName;

        @BindView(R.id.imageView_check)
        ImageView imageViewCheck;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }

    public List<InterestModel> getSelected() {
        List<InterestModel> selected = new ArrayList<>();
        for (int i = 0; i < interestModelList.size(); i++) {
            if (interestModelList.get(i).isSelected()) {
                selected.add(interestModelList.get(i));
            }
        }
        return selected;
    }
}
