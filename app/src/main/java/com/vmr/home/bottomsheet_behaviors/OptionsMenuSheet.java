package com.vmr.home.bottomsheet_behaviors;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.model.folder_structure.VmrItem;

/*
 * Created by abhijit on 8/31/16.
 */
public class OptionsMenuSheet extends BottomSheetDialogFragment {

    private OnOptionClickListener optionClickListener;
    private VmrItem vmrItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.options_layout, null);
        dialog.setContentView(contentView);

        ((TextView)contentView.findViewById(R.id.tvFolderName)).setText(vmrItem.getName());

        contentView.findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onOpenClicked(vmrItem);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnIndex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onIndexClicked(vmrItem);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onShareClicked(vmrItem);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnRename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onRenameClicked(vmrItem);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDownloadClicked(vmrItem);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnMove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onMoveClicked(vmrItem);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnCopy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onCopyClicked(vmrItem);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnDuplicate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onDuplicateClicked(vmrItem);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnProperties).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onPropertiesClicked(vmrItem);  dismiss();
            }
        });
        contentView.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.onMoveToTrashClicked(vmrItem);  dismiss();
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

    public void setVmrItem(VmrItem vmrItem) {
        this.vmrItem = vmrItem;
    }

    public interface OnOptionClickListener{
        void onOpenClicked(VmrItem vmrItem);
        void onIndexClicked(VmrItem vmrItem);
        void onShareClicked(VmrItem vmrItem);
        void onRenameClicked(VmrItem vmrItem);
        void onDownloadClicked(VmrItem vmrItem);
        void onMoveClicked(VmrItem vmrItem);
        void onCopyClicked(VmrItem vmrItem);
        void onDuplicateClicked(VmrItem vmrItem);
        void onPropertiesClicked(VmrItem vmrItem);
        void onMoveToTrashClicked(VmrItem vmrItem);
        void onOptionsMenuDismiss();
    }
}
