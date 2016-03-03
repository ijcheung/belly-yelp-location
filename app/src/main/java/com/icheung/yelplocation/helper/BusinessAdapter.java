package com.icheung.yelplocation.helper;

import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icheung.yelplocation.R;
import com.icheung.yelplocation.model.Business;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {
    private final OnBusinessClickedListener mListener;
    private final List<Business> mBusinesses;

    public BusinessAdapter(OnBusinessClickedListener listener, List<Business> businesses){
        mListener = listener;
        mBusinesses = businesses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Business business = mBusinesses.get(position);

        Picasso.with(holder.thumb.getContext())
                .load(business.getThumb())
                .placeholder(R.drawable.ic_business)
                .into(holder.thumb);

        holder.name.setText(business.getName());

        if(business.getLocation().getCoordinate() != null) {
            holder.distance.setText(holder.distance.getResources().getString(R.string.miles_away, business.getDistance()));
        }
        else {
            holder.distance.setText("");
        }

        if(business.isClosed()) {
            holder.status.setText(R.string.closed);
            holder.status.setTextColor(holder.status.getResources().getColor(R.color.closed));
        }
        else {
            holder.status.setText(R.string.open);
            holder.status.setTextColor(holder.status.getResources().getColor(R.color.open));
        }

        if(business.getCategories() != null && business.getCategories().size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(business.getCategories().get(0).get(0));

            for(int i = 1; i < business.getCategories().size(); i++) {
                builder.append(", ");
                builder.append(business.getCategories().get(i).get(0));
            }

            holder.category.setVisibility(View.VISIBLE);
            holder.category.setText(builder.toString());
        }
        else {
            holder.category.setVisibility(View.INVISIBLE);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBusinessClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBusinesses.size();
    }

    public void refresh(List<Business> businesses) {
        mBusinesses.clear();
        mBusinesses.addAll(businesses);
        notifyDataSetChanged();
    }

    public void clear() {
        int num = mBusinesses.size();
        mBusinesses.clear();
        notifyItemRangeRemoved(0, num);
    }

    public interface OnBusinessClickedListener {
        void onBusinessClicked(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView thumb;
        public final TextView name;
        public final TextView distance;
        public final TextView category;
        public final TextView status;

        public ViewHolder(View view) {
            super(view);

            this.view = view;
            this.thumb = (ImageView) view.findViewById(R.id.thumb);
            this.name = (TextView) view.findViewById(R.id.name);
            this.distance = (TextView) view.findViewById(R.id.distance);
            this.category = (TextView) view.findViewById(R.id.category);
            this.status = (TextView) view.findViewById(R.id.status);

            ImageView chevron = (ImageView) view.findViewById(R.id.chevron);
            DrawableCompat.setAutoMirrored(chevron.getDrawable(), true);
            /*chevron.setColorFilter(chevron.getResources().getColor(R.color.divider));*/
        }
    }
}
