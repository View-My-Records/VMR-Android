package com.vmr.screen.home.context_menu;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbConstants;
import com.vmr.db.recently_accessed.Recent;
import com.vmr.db.record.Record;
import com.vmr.db.shared.SharedRecord;

/*
 * Created by abhijit on 8/31/16.
 */
public class RecentOptionsMenu extends BottomSheetDialogFragment {

    private OnOptionClickListener optionClickListener;
    private Recent recent;

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
        setupOptions(recent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        optionClickListener.onOptionsMenuDismiss();
    }

    public void setOptionClickListener(OnOptionClickListener optionClickListener) {
        this.optionClickListener = optionClickListener;
    }

    public void setRecent(Recent recent) {
        this.recent = recent;
    }

    private void setupViews(View contentView){
        ivInfo = ((ImageView) contentView.findViewById(R.id.infoImageView));
        tvRecordName = (TextView) contentView.findViewById(R.id.tvItemName);
        optionOpen = (RelativeLayout) contentView.findViewById(R.id.btnOpen);
        optionIndex = (RelativeLayout) contentView.findViewById(R.id.btnIndex);
        optionShare = (RelativeLayout) contentView.findViewById(R.id.btnShare);
        optionRename = (RelativeLayout) contentView.findViewById(R.id.btnRename);
        optionMove = (RelativeLayout) contentView.findViewById(R.id.btnMove);
        optionCopy = (RelativeLayout) contentView.findViewById(R.id.btnCopy);
        optionDuplicate = (RelativeLayout) contentView.findViewById(R.id.btnDuplicate);
        optionPaste = (RelativeLayout) contentView.findViewById(R.id.btnPaste);
        optionDownload = (RelativeLayout) contentView.findViewById(R.id.btnDownload);
        optionProperties = (RelativeLayout) contentView.findViewById(R.id.btnProperties);
        optionDelete = (RelativeLayout) contentView.findViewById(R.id.btnDelete);
    }

    private void setupOptions(Recent recent) {
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            Record record = Vmr.getDbManager().getRecord(recent.getNodeRef());

            ivInfo.setImageResource(R.drawable.ic_file);

            tvRecordName.setText(record.getRecordName());

            optionOpen.setVisibility(View.VISIBLE);
            optionIndex.setVisibility(View.VISIBLE);
            optionShare.setVisibility(View.VISIBLE);
            optionRename.setVisibility(View.VISIBLE);
            optionMove.setVisibility(View.GONE);
            optionCopy.setVisibility(View.VISIBLE);
            optionDuplicate.setVisibility(View.GONE);
            optionPaste.setVisibility(View.GONE);
            optionDownload.setVisibility(View.VISIBLE);
            optionProperties.setVisibility(View.VISIBLE);
            optionDelete.setVisibility(View.VISIBLE);

        } else if(recent.getLocation().equals(DbConstants.TABLE_SHARED)) {
            SharedRecord record = Vmr.getDbManager().getSharedRecord(recent.getNodeRef());

            ivInfo.setImageResource(R.drawable.ic_file);

            tvRecordName.setText(record.getRecordName());

            optionOpen.setVisibility(View.VISIBLE);
            optionIndex.setVisibility(View.GONE);
            optionShare.setVisibility(View.GONE);
            optionRename.setVisibility(View.GONE);
            optionMove.setVisibility(View.GONE);
            optionCopy.setVisibility(View.GONE);
            optionDuplicate.setVisibility(View.GONE);
            optionPaste.setVisibility(View.GONE);
            optionDownload.setVisibility(View.VISIBLE);
            optionProperties.setVisibility(View.VISIBLE);
            optionDelete.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners(){
        optionOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onOpenClicked(recent);
                dismiss();
            }
        });
        optionIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onIndexClicked(recent);  dismiss();
            }
        });
        optionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onShareClicked(recent);  dismiss();
            }
        });
        optionRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onRenameClicked(recent);  dismiss();
            }
        });
//        optionMove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                optionClickListener.onMoveClicked(recent);  dismiss();
//            }
//        });
        optionCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onCopyClicked(recent);  dismiss();
            }
        });
//        optionDuplicate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                optionClickListener.onDuplicateClicked(recent);  dismiss();
//            }
//        });
//        optionPaste.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                optionClickListener.onPasteClicked(recent);  dismiss();
//            }
//        });
        optionDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDownloadClicked(recent);  dismiss();
            }
        });
        optionProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onPropertiesClicked(recent);  dismiss();
            }
        });
        optionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onMoveToTrashClicked(recent);  dismiss();
            }
        });
    }

    public interface OnOptionClickListener{
        void onOpenClicked(Recent record);
        void onIndexClicked(Recent record);
        void onShareClicked(Recent record);
        void onRenameClicked(Recent record);
//        void onMoveClicked(Recent record);
        void onCopyClicked(Recent record);
//        void onDuplicateClicked(Recent record);
//        void onPasteClicked(Recent record);
        void onDownloadClicked(Recent record);
        void onPropertiesClicked(Recent record);
        void onMoveToTrashClicked(Recent record);
        void onOptionsMenuDismiss();
    }
}
