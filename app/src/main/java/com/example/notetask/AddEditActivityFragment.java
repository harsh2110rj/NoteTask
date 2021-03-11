package com.example.notetask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment {
public static final String TAG="AddEditActivityFragment";

public enum FragmentEditMode {EDIT,ADD}
private FragmentEditMode mMode;
private EditText mNameTextView;
private EditText mDescriptionTextView;
private EditText mSortOrderTextView;
private Button mSaveButton;

private OnSaveClicked mSaveListener=null;
interface OnSaveClicked
{
    void onSaveClicked();
}





    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
}


public boolean canClose()
{
    return false;
}

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);
        //Activities containing this fragment must implement it's callbacks
        Activity activity=getActivity();
        if(!(activity instanceof OnSaveClicked))
        {
            throw new ClassCastException(activity.getClass().getSimpleName()+" must implement AddEditActivityFragment.OnSaveClicked interface");

        }
        mSaveListener=(OnSaveClicked)getActivity();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mSaveListener=null;
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_add_edit,container,false);
        mNameTextView=(EditText)view.findViewById(R.id.addedit_name);
        mDescriptionTextView=(EditText)view.findViewById(R.id.addedit_description);
        mSortOrderTextView=(EditText)view.findViewById(R.id.addedit_sortorder);
        mSaveButton=(Button)view.findViewById(R.id.addedit_save);

//         Bundle arguments=getActivity().getIntent().getExtras();
        Bundle arguments=getArguments();
         final Task task;
         if(arguments!=null)
         {
             Log.d(TAG, "onCreateView: retreiving task details");
           task=(Task)arguments.getSerializable(Task.class.getSimpleName());
             if(task!=null) {
                 Log.d(TAG, "onCreateView: Task is not null");

                 mNameTextView.setText(task.getName());
               mDescriptionTextView.setText(task.getDescription());
               mSortOrderTextView.setText(Integer.toString(task.getSortOrder()));
               mMode=FragmentEditMode.EDIT;
           }
           else
           {
               //No Task, So we must be adding a new task, and not editing an existing one
               Log.d(TAG, "onCreateView: No task found");
               mMode=FragmentEditMode.ADD;

           }
         }
         else
         {
             task=null;
             Log.d(TAG, "onCreateView: No arguments adding new record");
             mMode=FragmentEditMode.ADD;
         }
         mSaveButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //Update the database if at least one field has changed.
                 //There's no need to hit the database unless this has happened.
                 int so;   //to save repeated conversions to int
                 if(mSortOrderTextView.length()>0)
                 {
                     so=Integer.parseInt(mSortOrderTextView.getText().toString());
                 }else
                 {
                     so=0;
                 }
                 ContentResolver contentResolver=getActivity().getContentResolver();
                 ContentValues contentValues=new ContentValues();
                 switch(mMode)
                 {
                     case EDIT:
                         if(task==null)
                         {
                             //To suppress @lint warnings
                             break;
                         }
                         if(!mNameTextView.getText().toString().equals(task.getName()))
                         {
                             contentValues.put(TasksContract.Columns.TASKS_NAME,mNameTextView.getText().toString());
                         }
                         if(!mDescriptionTextView.getText().toString().equals(task.getDescription()))
                         {
                             contentValues.put(TasksContract.Columns.TASKS_DESCRIPTION,mDescriptionTextView.getText().toString());
                         }
                         if(so!=task.getSortOrder())
                         {
                             contentValues.put(TasksContract.Columns.TASKS_SORTORDER,so);
                         }
                         if(contentValues.size()!=0)
                         {
                             Log.d(TAG, "onClick: updating task");
                             contentResolver.update(TasksContract.buildTaskUri(task.getId()),contentValues,null,null);
                         }
                         break;
                     case ADD:
                         if(mNameTextView.length()>0)
                         {
                             Log.d(TAG, "onClick: adding new task");
                             contentValues.put(TasksContract.Columns.TASKS_NAME,mNameTextView.getText().toString());
                             contentValues.put(TasksContract.Columns.TASKS_DESCRIPTION,mDescriptionTextView.getText().toString());
                             contentValues.put(TasksContract.Columns.TASKS_SORTORDER,so);
                             contentResolver.insert(TasksContract.CONTENT_URI,contentValues);
                         }
                         break;
                 }
                 Log.d(TAG, "onClick: done editing");
                 if(mSaveListener!=null)
                 {
                     mSaveListener.onSaveClicked();
                 }
             }


         });
        Log.d(TAG, "onCreateView: Exiting...");
        return view;
    }
}
