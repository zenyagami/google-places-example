package com.zenkun.estimote.adapter;

import android.Manifest;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zenkun.estimote.R;
import com.zenkun.estimote.model.ModelVenue;

import java.util.ArrayList;

/**
 * Created by Zen zenyagami@gmail.com on 23/02/2017.
 */

public class AdapterVenues  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ModelVenue> mDataset;
    OnLoadMoreListener loadMoreListener;
    public boolean isLoading = false, isMoreDataAvailable = true;
    public final int TYPE_VENUE = 1;
    public final int TYPE_LOAD = 0;

    private static OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mVenueTitle;
        public ImageView mVenuePhoto;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mVenueTitle = (TextView)v.findViewById(R.id.adapter_venue_title);
            mVenuePhoto = (ImageView) v.findViewById(R.id.adapter_venue_photo);
        }

        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClick(v,getAdapterPosition());
        }
    }
    static class LoadHolder extends RecyclerView.ViewHolder{
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterVenues(ArrayList<ModelVenue> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType==TYPE_VENUE)
                return new ViewHolder(inflater.inflate(R.layout.adapter_venue_row, parent, false));
        else
            return new LoadHolder(inflater.inflate(R.layout.adapter_row_load, parent, false));

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ModelVenue venue = mDataset.get(position);

        if(position>=getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null){
            isLoading = true;
            loadMoreListener.onLoadMore();
        }
        if(getItemViewType(position)==TYPE_VENUE)
        {
            ((ViewHolder)holder).mVenueTitle.setText(venue.venueName);
            Picasso.with(((ViewHolder)holder).mVenuePhoto.getContext()).load(venue.photoReferenceUrl)
                    .error(R.mipmap.ic_launcher)
                    .into(((ViewHolder)holder).mVenuePhoto);
        }


    }

    @Override
    public int getItemViewType(int position) {
        //TODO ENUM
        if(mDataset.get(position).type==1){
            return TYPE_VENUE;
        }else{
            return TYPE_LOAD;
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged(){
        notifyDataSetChanged();
        isLoading = false;
    }
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        AdapterVenues.mOnItemClickListener= clickListener;
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
}

