package com.example.notetask;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DatePickerFragment";
    public static final String DATE_PICKER_ID="ID";
    public static final String DATE_PICKER_TITLE="TITLE";
    public static final String DATE_PICKER_DATE="DATE";

    int mDialogId = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ////Use the current date initially
        final GregorianCalendar cal=new GregorianCalendar();
        String title=null;
        Bundle arguments=getArguments();
        if(arguments!=null)
        {
            mDialogId=arguments.getInt(DATE_PICKER_ID);
            title=arguments.getString(DATE_PICKER_TITLE);
            Date givenDate=(Date) arguments.getSerializable(DATE_PICKER_DATE);
            if(givenDate!=null)
            {
                cal.setTime(givenDate);
                Log.d(TAG, "onCreateDialog: retreived date = "+givenDate.toString());

            }

        }
        int year=cal.get(GregorianCalendar.YEAR);
        int month=cal.get(GregorianCalendar.MONTH);
        int day=cal.get(GregorianCalendar.DAY_OF_MONTH);

        DatePickerDialog dpd=new DatePickerDialog(getContext(),this,year,month,day);
        if(title!=null)
        {
            dpd.setTitle(title);

        }
        return dpd;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Activities using this dialog must implement its callbacks
        if(!(context instanceof DatePickerDialog.OnDateSetListener))
        {
            throw new ClassCastException(context.toString()+" must implement DatePickerDialog.OnDateSetListener interface");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onDateSet: Entering");
        DatePickerDialog.OnDateSetListener listener=(DatePickerDialog.OnDateSetListener)getActivity();
        if(listener!=null)
        {
            view.setTag(mDialogId);//Pass the id back in the tag, to save the
            listener.onDateSet(view,year,month,dayOfMonth);
        }
        Log.d(TAG, "onDateSet: Exiting");
    }
}
