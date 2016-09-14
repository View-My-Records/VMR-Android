package com.vmr.home.bottomsheet_behaviors;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.shared.SharedRecord;

/*
 * Created by abhijit on 8/31/16.
 */
public class SharedOptionsMenuSheet extends BottomSheetDialogFragment {

    private OnOptionClickListener optionClickListener;
    private SharedRecord record;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.options_shared, null);
        dialog.setContentView(contentView);

        ((TextView)contentView.findViewById(R.id.tvItemName)).setText(record.getRecordName());

        contentView.findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onOpenClicked(record);
                dismiss();
            }
        });


        contentView.findViewById(R.id.btnDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDownloadClicked(record);  dismiss();
            }
        });

        contentView.findViewById(R.id.btnRevokeAccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onRevokeAccessClicked(record);  dismiss();
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
                optionClickListener.onMoveToTrashClicked(record);  dismiss();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        optionClickListener.onOptionsMenuDismiss();
    }

    public void setOptionClickListener(SharedOptionsMenuSheet.OnOptionClickListener optionClickListener) {
        this.optionClickListener = optionClickListener;
    }

    public void setRecord(SharedRecord record) {
        this.record = record;
    }

    public interface OnOptionClickListener{
        void onOpenClicked(SharedRecord record);
        void onDownloadClicked(SharedRecord record);
        void onRevokeAccessClicked(SharedRecord record);
        void onPropertiesClicked(SharedRecord record);
        void onMoveToTrashClicked(SharedRecord record);
        void onOptionsMenuDismiss();
    }
}
