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
    private RelativeLayout optionOpen;
    private RelativeLayout optionIndex;
    private RelativeLayout optionShare;
    private RelativeLayout optionRename;
    private RelativeLayout optionDownload;
    private RelativeLayout optionMove;
    private RelativeLayout optionCopy;
    private RelativeLayout optionPaste;
    private RelativeLayout optionDuplicate;
    private RelativeLayout optionProperties;
    private RelativeLayout optionDelete;

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
            ivInfo.setImageResource(R.drawable.ic_folder);
            optionIndex.setVisibility(View.GONE);
            optionDownload.setVisibility(View.GONE);
        } else {
            ivInfo.setImageResource(R.drawable.ic_file);
            optionIndex.setVisibility(View.VISIBLE);
            optionDownload.setVisibility(View.VISIBLE);
        }

        tvRecordName.setText(record.getRecordName());

        if(record.getRecordOwner().equals("admin")){
            optionRename.setVisibility(View.GONE);
            optionMove.setVisibility(View.GONE);
            optionDuplicate.setVisibility(View.GONE);
            optionDelete.setVisibility(View.GONE);
        } else {
            optionRename.setVisibility(View.VISIBLE);
            optionMove.setVisibility(View.VISIBLE);
            optionDuplicate.setVisibility(View.VISIBLE);
            optionDelete.setVisibility(View.VISIBLE);
        }
    }

    public void setVmrSharedItem(VmrSharedItem vmrSharedItem) {
        this.vmrSharedItem = vmrSharedItem;
    }

    private void setupViews(View contentView){
        ivInfo = ((ImageView) contentView.findViewById(R.id.infoImageView));
        tvRecordName = (TextView) contentView.findViewById(R.id.tvItemName);
        optionOpen = (RelativeLayout) contentView.findViewById(R.id.btnOpen);
        optionIndex = (RelativeLayout) contentView.findViewById(R.id.btnIndex);
        optionShare = (RelativeLayout) contentView.findViewById(R.id.btnShare);
        optionRename = (RelativeLayout) contentView.findViewById(R.id.btnRename);
        optionDownload = (RelativeLayout) contentView.findViewById(R.id.btnDownload);
        optionMove = (RelativeLayout) contentView.findViewById(R.id.btnMove);
        optionCopy = (RelativeLayout) contentView.findViewById(R.id.btnCopy);
        optionPaste = (RelativeLayout) contentView.findViewById(R.id.btnPaste);
        optionDuplicate = (RelativeLayout) contentView.findViewById(R.id.btnDuplicate);
        optionProperties = (RelativeLayout) contentView.findViewById(R.id.btnProperties);
        optionDelete = (RelativeLayout) contentView.findViewById(R.id.btnDelete);
    }

    private void setupListeners(){
        optionOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onOpenClicked(record);
                dismiss();
            }
        });
        optionIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onIndexClicked(record);  dismiss();
            }
        });
        optionShare.setOnClickListener(new View.OnClickListener() {
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
        optionDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDownloadClicked(record);  dismiss();
            }
        });
        optionMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onMoveClicked(record);  dismiss();
            }
        });
        optionCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onCopyClicked(record);  dismiss();
            }
        });
        optionPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onPasteClicked(record);  dismiss();
            }
        });
        optionDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDuplicateClicked(record);  dismiss();
            }
        });
        optionProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onPropertiesClicked(record);  dismiss();
            }
        });
        optionDelete.setOnClickListener(new View.OnClickListener() {
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
        void onPasteClicked(Record record);
        void onDuplicateClicked(Record record);
        void onPropertiesClicked(Record record);
        void onMoveToTrashClicked(Record record);
        void onOptionsMenuDismiss();
    }
}
