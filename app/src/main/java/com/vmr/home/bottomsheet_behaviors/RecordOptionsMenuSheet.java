package com.vmr.home.bottomsheet_behaviors;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.record.Record;
import com.vmr.model.VmrSharedItem;

/*
 * Created by abhijit on 8/31/16.
 */
public class RecordOptionsMenuSheet extends BottomSheetDialogFragment {

    private OnOptionClickListener optionClickListener;
    private Record record;
    private VmrSharedItem vmrSharedItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.options_records, null);
        dialog.setContentView(contentView);

        ((TextView)contentView.findViewById(R.id.tvItemName)).setText(record.getRecordName());

        contentView.findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onOpenClicked(record);
                dismiss();
            }
        });
        contentView.findViewById(R.id.btnIndex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onIndexClicked(record);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onShareClicked(record);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnRename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onRenameClicked(record);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDownloadClicked(record);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnMove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onMoveClicked(record);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnCopy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onCopyClicked(record);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnDuplicate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDuplicateClicked(record);  dismiss();
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

    public void setOptionClickListener(OnOptionClickListener optionClickListener) {
        this.optionClickListener = optionClickListener;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public void setVmrSharedItem(VmrSharedItem vmrSharedItem) {
        this.vmrSharedItem = vmrSharedItem;
    }

    public interface OnOptionClickListener{
        void onOpenClicked(Record record);
        void onIndexClicked(Record record);
        void onShareClicked(Record record);
        void onRenameClicked(Record record);
        void onDownloadClicked(Record record);
        void onMoveClicked(Record record);
        void onCopyClicked(Record record);
        void onDuplicateClicked(Record record);
        void onPropertiesClicked(Record record);
        void onMoveToTrashClicked(Record record);
        void onOptionsMenuDismiss();
    }
}
