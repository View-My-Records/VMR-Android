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
import com.vmr.db.record.Record;
import com.vmr.model.VmrSharedItem;
import com.vmr.utils.FileUtils;

/*
 * Created by abhijit on 8/31/16.
 */
public class RecordOptionsMenu extends BottomSheetDialogFragment {

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
            optionShare.setVisibility(View.GONE);
            optionMove.setVisibility(View.GONE);
            optionCopy.setVisibility(View.GONE);
            if(Vmr.getClipBoard() != null){
                optionPaste.setVisibility(View.VISIBLE);
            } else {
                optionPaste.setVisibility(View.GONE);
            }
            optionDuplicate.setVisibility(View.GONE);
            optionDownload.setVisibility(View.GONE);
        } else {

            if(record.getRecordName().contains(".")) {
                String extension = (record.getRecordName().substring(record.getRecordName().lastIndexOf('.') + 1)).toLowerCase();
                String mimeType = FileUtils.getMimeTypeFromExtension(extension);

                if(mimeType!=null) {
                    if (mimeType.contains("image")) {
                        ivInfo.setImageResource(R.drawable.ic_file_image);
                    } else if (mimeType.contains("video")) {
                        ivInfo.setImageResource(R.drawable.ic_file_video);
                    } else {
                        switch (extension) {
                            case "pdf":
                                ivInfo.setImageResource(R.drawable.ic_file_pdf);
                                break;
                            case "xml":
                                ivInfo.setImageResource(R.drawable.ic_file_xml);
                                break;
                            default:
                                ivInfo.setImageResource(R.drawable.ic_file);
                                break;
                        }
                    }
                }
            }
            optionIndex.setVisibility(View.VISIBLE);
            optionShare.setVisibility(View.VISIBLE);
            optionMove.setVisibility(View.VISIBLE);
            optionCopy.setVisibility(View.VISIBLE);
            optionPaste.setVisibility(View.GONE);
            optionDuplicate.setVisibility(View.VISIBLE);
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
        optionMove = (RelativeLayout) contentView.findViewById(R.id.btnMove);
        optionCopy = (RelativeLayout) contentView.findViewById(R.id.btnCopy);
        optionDuplicate = (RelativeLayout) contentView.findViewById(R.id.btnDuplicate);
        optionPaste = (RelativeLayout) contentView.findViewById(R.id.btnPaste);
        optionDownload = (RelativeLayout) contentView.findViewById(R.id.btnDownload);
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
        optionDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDuplicateClicked(record);  dismiss();
            }
        });
        optionPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onPasteClicked(record);  dismiss();
            }
        });
        optionDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDownloadClicked(record);  dismiss();
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
        void onMoveClicked(Record record);
        void onCopyClicked(Record record);
        void onDuplicateClicked(Record record);
        void onPasteClicked(Record record);
        void onDownloadClicked(Record record);
        void onPropertiesClicked(Record record);
        void onMoveToTrashClicked(Record record);
        void onOptionsMenuDismiss();
    }
}
