package com.example.notetask;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.InvalidParameterException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
                    CursorRecyclerViewAdapter.OnTaskClickListener
{
    private static final String TAG = "MainActivityFragment";
    public static final int LOADER_ID = 0;
    private CursorRecyclerViewAdapter mAdapter;//add adapter reference
    private Timing mCurrentTiming=null;
    @Override
    public void onEditClick(@NonNull Task task) {
        Log.d(TAG, "onEditClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener=(CursorRecyclerViewAdapter.OnTaskClickListener)getActivity();
        if(listener!=null)
        {
            listener.onEditClick(task);
        }
    }


    @Override
    public void onTaskLongClick(@NonNull Task task) {
        Log.d(TAG, "onTaskLongClick: called");
        if (mCurrentTiming != null) {
            if (task.getId() == mCurrentTiming.getTask().getId()) {
                // the current task was tapped a second time, so stop timing
                saveTiming(mCurrentTiming);
                mCurrentTiming = null;
                setTimingText(null);
            } else {
                // a new task is being timed, so stop the old one first
                saveTiming(mCurrentTiming);
                mCurrentTiming = new Timing(task);
                setTimingText(mCurrentTiming);
            }
        } else {
            // no task being timed, so start timing the new task
            mCurrentTiming = new Timing(task);
            setTimingText(mCurrentTiming);
        }
    }

    private void saveTiming(@NonNull Timing currentTiming) {
        Log.d(TAG, "Entering saveTiming");

        // If we have an open timing, set the duration and save
        currentTiming.setDuration();

        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(TimingsContract.Columns.TIMINGS_TASK_ID, currentTiming.getTask().getId());
        values.put(TimingsContract.Columns.TIMINGS_START_TIME, currentTiming.getStartTime());
        values.put(TimingsContract.Columns.TIMINGS_DURATION, currentTiming.getDuration());

        // update table in database
        contentResolver.insert(TimingsContract.CONTENT_URI, values);

        Log.d(TAG, "Exiting saveTiming");
    }

    // This method is for saving the timing on rotation. Everytime the device is rotated
    //the activity gets destroyed but fragment doesn't but still the views inside the fragment
    //need to be inflated again and that's how we lose the timing if it was running when the device rotated;
    //So, this method prevents it;
    private void setTimingText(Timing timing) {
        TextView taskName = getActivity().findViewById(R.id.current_task);

        if (timing != null) {
            taskName.setText("Timing: " +timing.getTask().getName());
        } else {
            taskName.setText(R.string.no_task_message);
        }
    }


    @Override
    public void onDeleteClick(@NonNull Task task) {
        Log.d(TAG, "onDeleteClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener=(CursorRecyclerViewAdapter.OnTaskClickListener)getActivity();
        if(listener!=null)
        {
            listener.onDeleteClick(task);
        }
    }

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: starts");
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: onActivityCREATED starts");
        super.onActivityCreated(savedInstanceState);


        //Activities containing this fragment must implement it's callbacks
        Activity activity=getActivity();
        if(!(activity instanceof CursorRecyclerViewAdapter.OnTaskClickListener))
        {
            throw new ClassCastException(activity.getClass().getSimpleName()+" must implement CursorRecyclerViewAdapter.OnTaskClickListener interface");

        }

        getLoaderManager().initLoader(LOADER_ID,null,this);
        setTimingText(mCurrentTiming);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: starts");
        View view=inflater.inflate(R.layout.fragment_main,container,false);
        RecyclerView recyclerView=view.findViewById(R.id.task_list);
       LinearLayoutManager manager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
//        recyclerView.setHasFixedSize(true);
        if(mAdapter==null) {
            mAdapter = new CursorRecyclerViewAdapter(null, this);
        }
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "onCreateView: returning");

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: starts with id "+id);
        String[] projection={TasksContract.Columns._ID,TasksContract.Columns.TASKS_NAME,
        TasksContract.Columns.TASKS_DESCRIPTION,TasksContract.Columns.TASKS_SORTORDER};
        String sortOrder=TasksContract.Columns.TASKS_SORTORDER+","+TasksContract.Columns.TASKS_NAME+" COLLATE NOCASE ";
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        TasksContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
                default:
                    throw new InvalidParameterException(TAG+".onCreateLoader called with invalid loader id "+id);

        }
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: Entering");
        mAdapter.swapCursor(data);

        int count=mAdapter.getItemCount();
//        if(data!=null)
//        {
//            while(data.moveToNext())
//            {
//                for(int i=0;i<data.getColumnCount();i++)
//                {
//                    Log.d(TAG, "onLoadFinished: "+data.getColumnName(i)+": "+data.getString(i));
//
//                }
//                Log.d(TAG, "onLoadFinished: *************************************************************");
//            }
//            count=data.getCount();
//        }
        Log.d(TAG, "onLoadFinished: count is "+count);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        mAdapter.swapCursor(null);
    }

}

