package com.example.notetask;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {

    private static final String TAG = "CursorRecyclerViewAdapt";

    private Cursor mCursor;
    private OnTaskClickListener mListener;
    interface OnTaskClickListener
    {
        void onEditClick(@NonNull Task task);
        void onDeleteClick(@NonNull Task task);
        void onTaskLongClick(@NonNull Task task);
    }




    public CursorRecyclerViewAdapter(Cursor cursor,OnTaskClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor called");
        mCursor = cursor;
        mListener=listener;

    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items,parent,false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");
        if((mCursor==null)||(mCursor.getCount()==0))
        {
            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.name.setText((R.string.instruction_heading));
            holder.description.setText((R.string.instruction_description));
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
        else  {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }
            final Task task = new Task(mCursor.getLong(mCursor.getColumnIndex(TasksContract.Columns._ID))
                    , mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_NAME))
                    , mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION))
                    , mCursor.getInt(mCursor.getColumnIndexOrThrow(TasksContract.Columns.TASKS_SORTORDER)));


            holder.name.setText(task.getName());
            holder.description.setText(task.getDescription());
            holder.editButton.setVisibility(View.VISIBLE);//TODO add onClickListener
            holder.deleteButton.setVisibility(View.VISIBLE);//TODO add onClickListener

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: starts");
                    switch (view.getId()) {
                        case R.id.tli_edit:
                            if(mListener!=null)
                            mListener.onEditClick(task);
                            break;
                        case R.id.tli_delete:
                            if(mListener!=null)
                            mListener.onDeleteClick(task);
                            break;
                        default:
                            Log.d(TAG, "onClick: found unexpected button id");
                    }
                    Log.d(TAG, "onClick: button with id " + view.getId() + " clicked");
                    Log.d(TAG, "onClick: task name is " + task.getName());
                }
            };
            View.OnLongClickListener buttonLongListener=new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "onLongClick: starts");
                    if(mListener!=null)
                    {
                        mListener.onTaskLongClick(task);
                        return true;
                    }
                    return false;
                }
            };
            holder.editButton.setOnClickListener(buttonListener);
            holder.deleteButton.setOnClickListener(buttonListener);
            holder.itemView.setOnLongClickListener(buttonLongListener);
        }

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        if(mCursor==null||mCursor.getCount()==0)
            return 1;//To show the description of instructions
        else
        {
            return mCursor.getCount();
        }

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
   //This inner class is responsible for saving the data entered in the name,description and sortorder.
    //Then this class' instance is used in onBindViewHolder to provide the necesaary details to show in MainActivityFragment.
    
    static class TaskViewHolder extends  RecyclerView.ViewHolder{
        private static final String TAG = "TaskViewHolder";
        TextView name;
        TextView description;
        ImageButton editButton;
        ImageButton deleteButton ;
        View itemView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "TaskViewHolder: starts");
            this.name=(TextView)itemView.findViewById(R.id.tli_name);
            this.description=(TextView)itemView.findViewById(R.id.tli_description);
            this.editButton=(ImageButton)itemView.findViewById(R.id.tli_edit);
            this.deleteButton=(ImageButton)itemView.findViewById(R.id.tli_delete);
            this.itemView=itemView;
        }
    }
}
