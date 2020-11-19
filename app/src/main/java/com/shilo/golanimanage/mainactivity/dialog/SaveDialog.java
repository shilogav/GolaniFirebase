package com.shilo.golanimanage.mainactivity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.shilo.golanimanage.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class SaveDialog extends DialogFragment {
    SaveDialogListener listener;
    final SaveDialog dialog = this;
    public SaveDialog(Fragment fragment) {
        listener = (SaveDialogListener) fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.save_dialog, null);
        builder.setView(view);

        Button positiveButton= view.findViewById(R.id.buttonPositive);
        Button negativeButton= view.findViewById(R.id.buttonNegative);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPositiveClick(dialog);
                //dialog.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNegativeClick(dialog);
                //dialog.dismiss();
            }
        });



        return builder.create();
    }


    public interface SaveDialogListener {
        public void onPositiveClick(DialogFragment dialog);
        public void onNegativeClick(DialogFragment dialog);
    }
}
