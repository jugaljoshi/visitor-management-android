package visitor.app.com.visitormanagement.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.interfaces.NavigationCodes;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.view.SignatureView;

/**
 * Created by jugal on 30/7/16.
 */
public class SignatureFragment extends BaseFragment{

    private SignatureView signatureView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_container, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        renderSignatureView();
    }

    private void renderSignatureView(){
        if (getActivity() == null) return;
        ViewGroup contentView = getContentView();
        if (contentView == null) return;
        contentView.removeAllViews();

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View signatureViewLayout = layoutInflater.inflate(R.layout.signature_layout, contentView, false);

        signatureView  = (SignatureView) signatureViewLayout.findViewById(R.id.signatureView);
        signatureView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 140));

        Button  saveBtn = (Button) signatureViewLayout.findViewById(R.id.btnSelectSignature);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signatureView.signatureDrawn) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.SIGN_IMAGE_BYTE_DATA, signatureView.mBitmap);
                    getActivity().setResult(NavigationCodes.RC_SINGNATURE, intent);
                }
            }
        });

        contentView.addView(signatureViewLayout);
    }

    @Nullable
    @Override
    public ViewGroup getContentView() {
        return getView() != null ? (ViewGroup) getView().findViewById(R.id.contentContainer) : null;
    }

    @Override
    public String getScreenTag() {
        return getClass().getSimpleName();
    }

    @NonNull
    @Override
    public String getFragmentTxnTag() {
        return "SignatureFragment";
    }
}
