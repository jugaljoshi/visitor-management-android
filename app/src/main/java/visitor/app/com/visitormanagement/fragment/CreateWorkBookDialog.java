package visitor.app.com.visitormanagement.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.interfaces.OnWorkBookTypeSubmitBtnClickAware;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 28/10/16.
 */

/**
 * Created by jugal on 26/10/16.
 */


public class CreateWorkBookDialog extends AppCompatDialogFragment {

    public static CreateWorkBookDialog newInstance() {
        return new CreateWorkBookDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.create_work_book_type_layout, null, false);

        final EditText editTextWorkBookTypeName = (EditText)  view.findViewById(R.id.editTextWorkBookTypeName);
        TextView txtCancel = (TextView) view.findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                    //onCancel(getDialog());
                }
            }
        });

        TextView txtSubmit = (TextView) view.findViewById(R.id.txtSubmit);
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UIUtil.isEmpty(editTextWorkBookTypeName.getText().toString())){
                    return;
                }
                onSubmitBtmClicked(editTextWorkBookTypeName.getText().toString().trim());
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    public void onSubmitBtmClicked(String workBookTypeName){
        ((OnWorkBookTypeSubmitBtnClickAware)getActivity()).onWorkBookTypeSubmitBtnClickAware(workBookTypeName);
        try {
            dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


