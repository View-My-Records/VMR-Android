package com.vmr.home.bottomsheet_behaviors;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.trash.TrashRecord;

/*
 * Created by abhijit on 8/31/16.
 */
public class TrashOptionsMenuSheet extends BottomSheetDialogFragment {

    private OnOptionClickListener optionClickListener;
    private TrashRecord record;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.options_trash, null);
        dialog.setContentView(contentView);

        ((TextView)contentView.findViewById(R.id.tvItemName)).setText(record.getRecordName());

        contentView.findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onOpenClicked(record);
                dismiss();
            }
        });
        contentView.findViewById(R.id.btnRestore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onRestoreClicked(record);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnProperties).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onPropertiesClicked(record);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDeleteClicked(record);  dismiss();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        optionClickListener.onOptionsMenuDismiss();
    }

    public void setOptionClickListener(TrashOptionsMenuSheet.OnOptionClickListener optionClickListener) {
        this.optionClickListener = optionClickListener;
    }

    public void setRecord(TrashRecord record) {
        this.record = record;
    }


    public interface OnOptionClickListener{
        void onOpenClicked(TrashRecord record);
        void onRestoreClicked(TrashRecord record);
        void onPropertiesClicked(TrashRecord record);
        void onDeleteClicked(TrashRecord record);
        void onOptionsMenuDismiss();
    }
}
