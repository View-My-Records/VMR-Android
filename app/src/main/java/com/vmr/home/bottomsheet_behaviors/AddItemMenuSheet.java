package com.vmr.home.bottomsheet_behaviors;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.ImageButton;

import com.vmr.R;

/*
 * Created by abhijit on 8/31/16.
 */
public class AddItemMenuSheet extends BottomSheetDialogFragment {

    private OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.add_items_layout, null);
        dialog.setContentView(contentView);

        ImageButton cameraImage = (ImageButton) contentView.findViewById(R.id.ibCamera);
        ImageButton uploadFile = (ImageButton) contentView.findViewById(R.id.ibUpload);
        ImageButton createFolder = (ImageButton) contentView.findViewById(R.id.ibNewFolder);

        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onCameraClick();
                dismiss();
            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onFileClick();
                dismiss();
            }
        });

        createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onFolderClick();
                dismiss();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemClickListener.onAddItemsMenuDismiss();
    }

    public interface OnItemClickListener{
        void onCameraClick();
        void onFileClick();
        void onFolderClick();
        void onAddItemsMenuDismiss();
    }
}
