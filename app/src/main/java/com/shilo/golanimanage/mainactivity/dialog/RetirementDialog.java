package com.shilo.golanimanage.mainactivity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shilo.golanimanage.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

public class RetirementDialog extends DialogFragment {
    DialogListener listener;
    final RetirementDialog dialog = this;
    public RetirementDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.retirement_dialog_layout, null);
        builder.setView(view);

        Button independentButton= view.findViewById(R.id.buttonIndependent);
        Button medicalButton= view.findViewById(R.id.buttonMedical);
        Button initiatedButton= view.findViewById(R.id.buttonInitiated);

        independentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogIndependentRetirement(dialog);
            }
        });

        medicalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogMedicalRetirement(dialog);
            }
        });

        initiatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogInitiatedRetirement(dialog);
            }
        });


        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DialogListener");
        }
    }

    public interface DialogListener {
        public void onDialogIndependentRetirement(DialogFragment dialog);
        public void onDialogMedicalRetirement(DialogFragment dialog);
        public void onDialogInitiatedRetirement(DialogFragment dizlog);
    }
}
