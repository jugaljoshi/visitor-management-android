package visitor.app.com.visitormanagement.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import visitor.app.com.visitormanagement.ImageUtil.ImageClickListener;
import visitor.app.com.visitormanagement.ImageUtil.ShowImageDialog;
import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.interfaces.AppOperationAware;
import visitor.app.com.visitormanagement.models.VisitorModel;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 22/7/16.
 */
public class VisitorListingRecyclerAdapter extends RecyclerView.Adapter<VisitorListingRecyclerAdapter.VisitorHolder> {

    private ArrayList<VisitorModel> visitorModelArrayList;
    private View.OnClickListener imageClickListener;

    public VisitorListingRecyclerAdapter(ArrayList<VisitorModel> visitorModelArrayList, AppCompatActivity appCompatActivity){
        this.visitorModelArrayList = visitorModelArrayList;
        this.imageClickListener = new ImageClickListener(appCompatActivity);
    }

    @Override
    public VisitorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visitor_listing_adapter_layout, parent, false);
        return new VisitorHolder(itemView);
    }

        @Override
    public void onBindViewHolder(VisitorHolder visitorHolder, int position) {
        VisitorModel visitorModel = visitorModelArrayList.get(position);

        ImageView imgVisitor = visitorHolder.getImgVisitor();
        if(!UIUtil.isEmpty(visitorModel.getVisitorImg())){
            UIUtil.displayAsyncImage(imgVisitor, visitorModel.getVisitorImg());
            imgVisitor.setVisibility(View.VISIBLE);
            imgVisitor.setTag(visitorModel.getVisitorImg());
        }else {
            imgVisitor.setVisibility(View.GONE);
        }

        ImageView imgSignature = visitorHolder.getImgSignature();
        if(!UIUtil.isEmpty(visitorModel.getVisitorSignUrl())){
            UIUtil.displayAsyncImage(imgSignature, visitorModel.getVisitorSignUrl());
            imgSignature.setVisibility(View.VISIBLE);
            imgSignature.setTag(visitorModel.getVisitorSignUrl());
        }else {
            imgSignature.setVisibility(View.GONE);
        }

        TextView txtVisitorName = visitorHolder.getTxtVisitorName();
        if(!UIUtil.isEmpty(visitorModel.getName())){
            txtVisitorName.setText(visitorModel.getName());
            txtVisitorName.setVisibility(View.VISIBLE);
        }else {
            txtVisitorName.setVisibility(View.GONE);
        }

        TextView txtPhoneNumber = visitorHolder.getTxtPhoneNumber();
        if(!UIUtil.isEmpty(visitorModel.getMobileNumber())){
            txtPhoneNumber.setText(visitorModel.getMobileNumber());
            txtPhoneNumber.setVisibility(View.VISIBLE);
        }else {
            txtPhoneNumber.setVisibility(View.GONE);
        }

        TextView txtVehicleNumber = visitorHolder.getTxtVehicleNumber();
        if(!UIUtil.isEmpty(visitorModel.getVehicleNo())){
            txtVehicleNumber.setText(visitorModel.getVehicleNo());
            txtVehicleNumber.setVisibility(View.VISIBLE);
        }else {
            txtVehicleNumber.setVisibility(View.GONE);
        }


        TextView txtFrom = visitorHolder.getTxtFrom();
        if(!UIUtil.isEmpty(visitorModel.getFromPlace())){
            txtFrom.setText(visitorModel.getFromPlace());
            txtFrom.setVisibility(View.VISIBLE);
        }else {
            txtFrom.setVisibility(View.GONE);
        }


        TextView txtDestination = visitorHolder.getTxtDestination();
        if(!UIUtil.isEmpty(visitorModel.getDestinationPlace())){
            txtDestination.setText(visitorModel.getDestinationPlace());
            txtDestination.setVisibility(View.VISIBLE);
        }else {
            txtDestination.setVisibility(View.GONE);
        }

        TextView txtInTime = visitorHolder.getTxtInTime();
        if(!UIUtil.isEmpty(visitorModel.getInTime())){
            txtInTime.setText(visitorModel.getInTime());
            txtInTime.setVisibility(View.VISIBLE);
        }else {
            txtInTime.setVisibility(View.GONE);
        }

        TextView txtOutTime = visitorHolder.getTxtOutTime();
        if(!UIUtil.isEmpty(visitorModel.getOutTime())){
            txtOutTime.setText(visitorModel.getOutTime());
            txtOutTime.setVisibility(View.VISIBLE);
        }else {
            txtOutTime.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return visitorModelArrayList.size();
    }

    public class VisitorHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imgVisitor;
        private ImageView imgSignature;
        private TextView txtVisitorName;
        private TextView txtPhoneNumber;
        private TextView txtVehicleNumber;
        private TextView txtFrom;
        private TextView txtDestination;
        private TextView txtInTime;
        private TextView txtOutTime;


        public VisitorHolder(View itemView) {
            super(itemView);
        }

        public ImageView getImgVisitor() {
            if(imgVisitor == null){
                imgVisitor = (ImageView) itemView.findViewById(R.id.imgVisitor);
                imgVisitor.setOnClickListener(imageClickListener);

            }
            return imgVisitor;
        }

        public ImageView getImgSignature() {
            if(imgSignature == null){
                imgSignature = (ImageView) itemView.findViewById(R.id.imgSignature);
                imgSignature.setOnClickListener(imageClickListener);
            }
            return imgSignature;
        }

        public TextView getTxtVisitorName() {
            if(txtVisitorName == null){
                txtVisitorName = (TextView) itemView.findViewById(R.id.txtVisitorName);
            }
            return txtVisitorName;
        }

        public TextView getTxtPhoneNumber() {
            if(txtPhoneNumber == null){
                txtPhoneNumber = (TextView) itemView.findViewById(R.id.txtPhoneNumber);
            }
            return txtPhoneNumber;
        }

        public TextView getTxtVehicleNumber() {
            if(txtVehicleNumber == null){
                txtVehicleNumber = (TextView) itemView.findViewById(R.id.txtVehicleNumber);
            }
            return txtVehicleNumber;
        }

        public TextView getTxtFrom() {
            if(txtFrom == null){
                txtFrom = (TextView) itemView.findViewById(R.id.txtFrom);
            }
            return txtFrom;
        }

        public TextView getTxtDestination() {
            if(txtDestination == null){
                txtDestination = (TextView) itemView.findViewById(R.id.txtDestination);
            }
            return txtDestination;
        }

        public TextView getTxtInTime() {
            if(txtInTime == null){
                txtInTime = (TextView) itemView.findViewById(R.id.txtInTime);
            }
            return txtInTime;
        }

        public TextView getTxtOutTime() {
            if(txtOutTime ==  null){
                txtOutTime = (TextView) itemView.findViewById(R.id.txtOutTime);
            }
            return txtOutTime;
        }

        @Override
        public void onClick(View v) {
            // do noting on click
        }
    }
}
