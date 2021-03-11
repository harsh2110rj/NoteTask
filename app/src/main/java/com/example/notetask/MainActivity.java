package com.example.notetask;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notetask.debug.TestData;

public class MainActivity
        extends AppCompatActivity
        implements CursorRecyclerViewAdapter.OnTaskClickListener
        ,AddEditActivityFragment.OnSaveClicked
        ,AppDialog.DialogEvents
{
    private static final String TAG = "MainActivity";
    //It tells us whether the activity is in two panes mode or not..
    // like in landscape mode on tablet
    private boolean mTwoPane=false;
    public static final String ADD_EDIT_FRAGMENT="AddEditFragment";
    public static final int DELETE_DIALOG_ID=1;
    public static final int CANCEL_EDIT_DIALOG_ID=2;
    public static final int CANCEL_EDIT_DIALOG_ID_UP=3;
    private AlertDialog mDialog=null;

    private Timing mCurrentTiming=null;

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager=getSupportFragmentManager();
        Fragment fragment=fragmentManager.findFragmentById(R.id.task_details_container);
        if(fragment!=null)
        {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }
        View addEditLayout=findViewById(R.id.task_details_container);
        View mainFragment=findViewById(R.id.fragment);
        if(!mTwoPane)
        {
            //Now user has clicked the save button so we just hide addEditFragment

            addEditLayout.setVisibility(View.GONE);
            mainFragment.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mTwoPane=(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE);
        Log.d(TAG, "onCreate: mTwoPane is "+mTwoPane);
        FragmentManager fragmentManager=getSupportFragmentManager();
        //If AddEditActivityFragment exists then we are editing .SO.......
        Boolean editing=fragmentManager.findFragmentById(R.id.task_details_container)!=null;
        Log.d(TAG, "onCreate: editing is "+editing);
        //Now we need references to fragments to show and hide them according to the orientation.
        //No need to cast them ,just create two View objects as all methods which we are using are applicable to all fragments in general.
        View addEditLayout=findViewById(R.id.task_details_container);
        View mainFragment=findViewById(R.id.fragment);

        if(mTwoPane)
        {
            Log.d(TAG, "onCreate: Entering in landscape mode");
            //Both are visible
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.VISIBLE);
        }
        else if(editing)
        {
            Log.d(TAG, "onCreate: Single pane And Editing mode is ON");
            //Only addEditLayout is visible
            mainFragment.setVisibility(View.GONE);
        }
        else
        {
            Log.d(TAG, "onCreate: Single Pane And Editing Mode is OFF");
            //only mainFragment is visible
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.GONE);
        }






//
//        String[] projection = {TasksContract.Columns._ID,
//                TasksContract.Columns.TASKS_NAME,
//                TasksContract.Columns.TASKS_DESCRIPTION,
//                TasksContract.Columns.TASKS_SORTORDER};
//        ContentResolver contentResolver = getContentResolver();
//        ContentValues values = new ContentValues();





//        values.put(TasksContract.Columns.TASKS_SORTORDER,"9");
//        values.put(TasksContract.Columns.TASKS_DESCRIPTION,"For deletion");
//        String selection=TasksContract.Columns.TASKS_SORTORDER+" = "+2;
//        String selection=TasksContract.Columns.TASKS_SORTORDER+" = ?" ;
//        String[] args={"9"};
//        int count=contentResolver.update(TasksContract.CONTENT_URI,values,selection,args);
//        Log.d(TAG, "onCreate: "+count+" records updated");
//        values.put(TasksContract.Columns.TASKS_NAME,"New Task 1");
//        values.put(TasksContract.Columns.TASKS_DESCRIPTION,"Des");
//        values.put(TasksContract.Columns.TASKS_SORTORDER,2);
//
//        Uri uri=contentResolver.insert(TasksContract.CONTENT_URI,values);
//        values.put(TasksContract.Columns.TASKS_NAME,"ghfhgfhf");
//        values.put(TasksContract.Columns.TASKS_DESCRIPTION,"hjgjhghjbjhb");
//        int count=contentResolver.update(TasksContract.buildTaskUri(1),values,null,null);
////
//        int count=contentResolver.delete(TasksContract.buildTaskUri(3),null,null);
//        Log.d(TAG, "onCreate: "+count+" record(s) deleted");
//        String selection=TasksContract.Columns.TASKS_DESCRIPTION + " =?";
//        String[] args={"Completed"};
//        int count=contentResolver.delete(TasksContract.CONTENT_URI,selection,args);
//        Log.d(TAG, "onCreate: "+count+" record(s) deleted");
//;        Cursor cursor=contentResolver.query(
//                TasksContract.CONTENT_URI,
//                projection,
//
//                null,
//                null,
//                TasksContract.Columns.TASKS_NAME
//
//        );
//        if(cursor!=null)
//        {
//            Log.d(TAG, "onCreate: number of rows: "+cursor.getCount());
//            while(cursor.moveToNext())
//            {
//                for(int i=0;i<cursor.getColumnCount();i++)
//                {
//                    Log.d(TAG, "onCreate: "+cursor.getColumnName(i)+": "+cursor.getString(i));
//                }
//                Log.d(TAG, "onCreate: ********************************************");
//            }
//            cursor.close();
//        }
//
//
////        AppDatabase appDatabase=AppDatabase.getInstance(this);
////        final SQLiteDatabase db=appDatabase.getReadableDatabase();
//
////
////        FloatingActionButton fab = findViewById(R.id.fab);
////        fab.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
////            }
////        });
//    }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(BuildConfig.DEBUG)
        {
            MenuItem generate=menu.findItem(R.id.menumain_generate);
            generate.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;
            case R.id.menumain_showDurations:
                startActivity(new Intent(this,DurationsReport.class));
                break;
            case R.id.menumain_settings:
                break;
            case R.id.menumain_showAbout:
                showAboutDialog();
                break;
            case R.id.menumain_generate:
                TestData.generateTestData(getContentResolver());
                break;
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home button pressed");
                AddEditActivityFragment fragment=(AddEditActivityFragment)
                        getSupportFragmentManager().findFragmentById(R.id.task_details_container);
                if(fragment.canClose())
                {
                    return super.onOptionsItemSelected(item);
                }
                else
                {
                    showConfirmationDialog(CANCEL_EDIT_DIALOG_ID_UP);
                    return true;// indicate we are handling this
                }


        }

        return super.onOptionsItemSelected(item);
    }
    public void showAboutDialog()
{
    View messageView=getLayoutInflater().inflate(R.layout.about,null,false);
    AlertDialog.Builder builder=new AlertDialog.Builder(this);
    builder.setTitle(R.string.app_name);
    builder.setIcon(R.mipmap.ic_launcher);
    builder.setView(messageView);
    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(mDialog!=null&&mDialog.isShowing())
            {mDialog.dismiss();}
        }
    });
    mDialog=builder.create();
    mDialog.setCanceledOnTouchOutside(false);

    TextView tv=(TextView) messageView.findViewById(R.id.about_version);
    tv.setText("v" +BuildConfig.VERSION_NAME);
    mDialog.show();
}

    @Override
    public void onEditClick(@NonNull Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        Log.d(TAG, "onDeleteClick: starts");

        AppDialog dialog=new AppDialog();
        Bundle args=new Bundle();
        args.putInt(AppDialog.DIALOG_ID,DELETE_DIALOG_ID);
        args.putString(AppDialog.DIALOG_MESSAGE,getString(R.string.deldiag_message,task.getId(),task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID,(R.string.deldiag_positive_caption));

        args.putLong("TaskId",task.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);

    }

    private void taskEditRequest(Task task)
    {
        Log.d(TAG, "taskEditRequest: starts");


            Log.d(TAG, "taskEditRequest: in two-pane mode (tablet)");
            AddEditActivityFragment fragment=new AddEditActivityFragment();
            Bundle arguments= new Bundle();
            arguments.putSerializable(Task.class.getSimpleName(),task);
            fragment.setArguments(arguments);
//            FragmentManager fragmentManager=getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.task_details_container,fragment);
//            fragmentTransaction.commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_details_container,fragment)
                    .commit();


        if(!mTwoPane)
        {
            Log.d(TAG, "taskEditRequest: in single pane mode (phone)");
          //Hide the mainFragment and show the addEditFragment
            View addEditLayout=findViewById(R.id.task_details_container);
            View mainFragment=findViewById(R.id.fragment);
            mainFragment.setVisibility(View.GONE);
            addEditLayout.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "taskEditRequest: Exiting now ....");
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
//        if(args.getLong("TaskId")!=null)
        switch (dialogId) {
            case DELETE_DIALOG_ID:
            long taskId = args.getLong("TaskId");
            if (BuildConfig.DEBUG && taskId == 0) throw new AssertionError("Task Id is zero");
            getContentResolver().delete(TasksContract.buildTaskUri(taskId), null, null);
            break;
            case CANCEL_EDIT_DIALOG_ID:
            {//no action required;
                break;

            }
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch(dialogId)
        {
            case DELETE_DIALOG_ID:
                //no action required
                break;
            case CANCEL_EDIT_DIALOG_ID:
            case CANCEL_EDIT_DIALOG_ID_UP:
                FragmentManager fragmentManager=getSupportFragmentManager();
                Fragment fragment=fragmentManager.findFragmentById(R.id.task_details_container);
                if(fragment!=null) {
                    getSupportFragmentManager().
                            beginTransaction()
                            .remove(fragment)
                            .commit();

                    if (mTwoPane) {
                        if(dialogId==CANCEL_EDIT_DIALOG_ID)
                        {finish();}
                    } else {
                        View addEditLayout = findViewById(R.id.task_details_container);
                        View mainFragment = findViewById(R.id.fragment);
                        mainFragment.setVisibility(View.VISIBLE);
                        addEditLayout.setVisibility(View.GONE);
                    }
                }
                else
                {
                    //no editing, so quit regardless of orientation
                    finish();
                }

                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager=getSupportFragmentManager();
        AddEditActivityFragment fragment=(AddEditActivityFragment)fragmentManager.findFragmentById(R.id.task_details_container);
        if(fragment==null||fragment.canClose())
        {
        super.onBackPressed();
        }
        else
        {//show dialogue to get confirmation to quit editing
              showConfirmationDialog(CANCEL_EDIT_DIALOG_ID);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mDialog!=null&&mDialog.isShowing())
        {
            mDialog.dismiss();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.d(TAG, "onAttachFragment: called, fragment is "+fragment.toString());

        super.onAttachFragment(fragment);
    }
    private void showConfirmationDialog(int dialogId)
    {
        AppDialog appDialog=new AppDialog();
        Bundle args=new Bundle();
        args.putInt(AppDialog.DIALOG_ID,dialogId);
        args.putString(AppDialog.DIALOG_MESSAGE,getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID,R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID,R.string.cancelEditDiag_negative_caption);
        appDialog.setArguments(args);
        appDialog.show(getSupportFragmentManager(),null);
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
    //Just to satisfy interface
        //Implementation is in MainActivityFragment.java

    }

 }





