package com.example.notetask;

import android.content.Context;
import android.database.Cursor;
//import android.icu.text.DateFormat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;


public class DurationsRVAdapter extends RecyclerView.Adapter<DurationsRVAdapter.ViewHolder> {
    private Cursor mCursor;
    private final java.text.DateFormat mDateFormat;//module level so we don't keep on instantiating in BindView

    public DurationsRVAdapter(Context context,Cursor cursor) {
        this.mCursor = cursor;
        mDateFormat= DateFormat.getDateFormat(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_durations_items,parent,false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    if((mCursor!=null)&&(mCursor.getCount()!=0))
    {
        if(!mCursor.moveToPosition(position))
        {
            throw new IllegalStateException("Couldn't move the cursor to the position: "+position);
        }
        String name=mCursor.getString(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_NAME));
        String description=mCursor.getString(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DESCRIPTION));
        Long startTime=mCursor.getLong(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_START_TIME));
        long totalDuration=mCursor.getLong(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DURATION));
        holder.name.setText(name);

        if(holder.description!=null)
        {
            holder.description.setText(description);
        }
        String userDate=mDateFormat.format(startTime*1000);
        String totalTime=formatDuration(totalDuration);
        holder.startDate.setText(userDate);
        holder.duration.setText(totalTime);


    }

    }

    @Override
    public int getItemCount() {
        return mCursor!=null?mCursor.getCount():0;
    }

    private String formatDuration(long duration)
    {
        //Note: duration is in seconds
        long hours=duration/3600;
        long rem=duration-(hours*3600);
        long minutes=rem/60;
        long seconds=rem-(minutes*60);
        return String.format(Locale.US,"%02d:%02d:%02d",hours,minutes,seconds);

    }
    /**
     * Swap in a new cursor, returning the old cursor
     * The returned old cursor is <em>not</em> closed
     *
     * @param newCursor  - The newCursor to be used
     * @return Returns the previously set cursor, or null if there wasn't one
     * if the newCursor is same as the oldCursor null is also returned;
     */
    Cursor swapCursor(Cursor newCursor)
    {
        if(newCursor==mCursor)
        {
            return null;
        }
        final Cursor oldCursor=mCursor;
        mCursor=newCursor;
        if(newCursor!=null)
        {
            //notify the observer about the new Cursor
            notifyDataSetChanged();
        }
        else
        {
            //notify the observers about the lack of a data set
            notifyItemRangeRemoved(0,getItemCount());
        }
        return oldCursor;

    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView description;
        TextView startDate;
        TextView duration;

        public ViewHolder(View itemView) {
            super(itemView);
            this.name=itemView.findViewById(R.id.td_name);
            this.description=itemView.findViewById(R.id.td_description);
            this.startDate=itemView.findViewById(R.id.td_start);
            this.duration=itemView.findViewById(R.id.td_duration);
        }
    }
}
