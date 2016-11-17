package visitor.app.com.visitormanagement.ImageUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 26/10/16.
 */


public class ShowImageDialog extends AppCompatDialogFragment {

    private static final String IMAGE_URL = "image_url";

    public static ShowImageDialog newInstance(String imageUrl) {
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageUrl);
        ShowImageDialog dialogFragment = new ShowImageDialog();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        String imageUrl = args.getString(IMAGE_URL);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View imageViewLayout = inflater.inflate(R.layout.image_view_dailog, null, false);


        ImageView imageView = (ImageView) imageViewLayout.findViewById(R.id.imageView);
        UIUtil.displayAsyncImage(imageView, imageUrl);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(imageViewLayout);
        AlertDialog alertDialog = builder.create();
        setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);

        return alertDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

    }
}

