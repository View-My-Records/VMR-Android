package com.vmr.screen.home.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.vmr.R;

import java.util.Date;

/*
 * Created by abhijit on 9/21/16.
 */

public class DateTimePickerDialog extends DialogFragment {

    VmrDateTimePicker dateTimePickerInterface;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Date nextActionDate = new Date();

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_datetimepicker, null);

        final DatePicker datePicker = (DatePicker)v.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker)v.findViewById(R.id.timePicker);

        datePicker.setMinDate(System.currentTimeMillis());

        final AlertDialog alertDialog =
                new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Next action date & time")
                .setMessage("Next action date must be future date")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dateTimePickerInterface.onDateTimePicked(nextActionDate);
                        DateTimePickerDialog.this.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DateTimePickerDialog.this.dismiss();
                    }
                })
//                .setNeutralButton("Reset", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Date dateNow = new Date(System.currentTimeMillis());
//                        datePicker.updateDate(dateNow.getYear(), dateNow.getMonth(), dateNow.getDate());
//                        timePicker.setCurrentHour(dateNow.getHours());
//                        timePicker.setCurrentMinute(dateNow.getMinutes());
//                    }
//                })
                .create();
        Date dateNow = new Date(System.currentTimeMillis());

        datePicker.init(dateNow.getYear(), dateNow.getMonth(), dateNow.getDate(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                nextActionDate.setYear(datePicker.getYear() - 1900);
                nextActionDate.setMonth(datePicker.getMonth());
                nextActionDate.setDate(datePicker.getDayOfMonth());
                nextActionDate.setHours(timePicker.getCurrentHour());
                nextActionDate.setMinutes(timePicker.getCurrentMinute());
                if(nextActionDate.before(new Date(System.currentTimeMillis()))){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                nextActionDate.setHours(timePicker.getCurrentHour());
                nextActionDate.setMinutes(timePicker.getCurrentMinute());
                if(nextActionDate.before(new Date(System.currentTimeMillis()))){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        return alertDialog;
    }

    public void setDateTimePickerInterface(VmrDateTimePicker dateTimePickerInterface) {
        this.dateTimePickerInterface = dateTimePickerInterface;
    }

    public interface VmrDateTimePicker{
        void onDateTimePicked(Date nextActionDate);
    }
}
