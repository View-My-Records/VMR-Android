package com.vmr.home.bottomsheet_behaviors;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    private ImageView ivInfo;
    private TextView tvRecordName;
    private RelativeLayout tvRecordOpen;
    private RelativeLayout tvRecordIndex;
    private RelativeLayout tvRecordShare;
    private RelativeLayout optionRename;
    private RelativeLayout tvRecordDownload;
    private RelativeLayout tvRecordMove;
    private RelativeLayout tvRecordCopy;
    private RelativeLayout tvRecordDuplicate;
    private RelativeLayout tvRecordProperties;
    private RelativeLayout tvRecordDelete;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getActivity(), R.layout.options_records, null);
        dialog.setContentView(contentView);

        setupViews(contentView);
        setupListeners();
        setupOptions(record);
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

    private void setupOptions(Record record) {
        if(record.isFolder()) {
            ivInfo.setImageResource(R.drawable.ic_folder_black_24dp);
        } else {
            ivInfo.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
        }

        tvRecordName.setText(record.getRecordName());

        if(record.getRecordOwner().equals("admin")){
            optionRename.setVisibility(View.GONE);
        } else {
            optionRename.setVisibility(View.VISIBLE);
        }
    }

    public void setVmrSharedItem(VmrSharedItem vmrSharedItem) {
        this.vmrSharedItem = vmrSharedItem;
    }

    private void setupViews(View contentView){
        ivInfo = ((ImageView) contentView.findViewById(R.id.infoImageView));
        tvRecordName = (TextView) contentView.findViewById(R.id.tvItemName);
        tvRecordOpen = (RelativeLayout) contentView.findViewById(R.id.btnOpen);
        tvRecordIndex = (RelativeLayout) contentView.findViewById(R.id.btnIndex);
        tvRecordShare = (RelativeLayout) contentView.findViewById(R.id.btnShare);
        optionRename = (RelativeLayout) contentView.findViewById(R.id.btnRename);
        tvRecordDownload = (RelativeLayout) contentView.findViewById(R.id.btnDownload);
        tvRecordMove = (RelativeLayout) contentView.findViewById(R.id.btnMove);
        tvRecordCopy = (RelativeLayout) contentView.findViewById(R.id.btnCopy);
        tvRecordDuplicate = (RelativeLayout) contentView.findViewById(R.id.btnDuplicate);
        tvRecordProperties = (RelativeLayout) contentView.findViewById(R.id.btnProperties);
        tvRecordDelete = (RelativeLayout) contentView.findViewById(R.id.btnDelete);
    }

    private void setupListeners(){
        tvRecordOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onOpenClicked(record);
                dismiss();
            }
        });
        tvRecordIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onIndexClicked(record);  dismiss();
            }
        });
        tvRecordShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onShareClicked(record);  dismiss();
            }
        });
        optionRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onRenameClicked(record);  dismiss();
            }
        });
        tvRecordDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDownloadClicked(record);  dismiss();
            }
        });
        tvRecordMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onMoveClicked(record);  dismiss();
            }
        });
        tvRecordCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onCopyClicked(record);  dismiss();
            }
        });
        tvRecordDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDuplicateClicked(record);  dismiss();
            }
        });
        tvRecordProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onPropertiesClicked(record);  dismiss();
            }
        });
        tvRecordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onMoveToTrashClicked(record);  dismiss();
            }
        });
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
