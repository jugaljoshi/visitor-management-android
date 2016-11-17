package visitor.app.com.visitormanagement.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import visitor.app.com.visitormanagement.utils.Constants;

/**
 * Created by jugal on 22/7/16.
 */
public class VisitorModel implements Parcelable {

    public static final Parcelable.Creator<VisitorModel> CREATOR = new Parcelable.Creator<VisitorModel>() {
        @Override
        public VisitorModel createFromParcel(Parcel source) {
            return new VisitorModel(source);
        }

        @Override
        public VisitorModel[] newArray(int size) {
            return new VisitorModel[size];
        }
    };

    @SerializedName(Constants.V_PHOTO)
    private String visitorImg;
    @SerializedName(Constants.NAME)
    private String name;
    @SerializedName(Constants.MOBILE_NO)
    private String mobileNumber;
    @SerializedName(Constants.FROM_PLACE)
    private String fromPlace;
    @SerializedName(Constants.DESTINATION_PLACE)
    private String destinationPlace;
    @SerializedName(Constants.IN_TIME)
    private String inTime;
    @SerializedName(Constants.OUT_TIME)
    private String outTime;
    @SerializedName(Constants.VEHICLE_NO)
    private String vehicleNo;
    @SerializedName(Constants.V_SIGNATURE_PHOTO)
    private String visitorSignUrl;

    public VisitorModel(Parcel source) {
        this.visitorImg = source.readString();
        this.name = source.readString();
        this.mobileNumber = source.readString();
        this.fromPlace = source.readString();
        this.destinationPlace = source.readString();
        this.inTime = source.readString();
        this.outTime = source.readString();
        this.vehicleNo = source.readString();
        this.visitorSignUrl = source.readString();
    }

    private String wbId;
    public VisitorModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(visitorImg);
        dest.writeString(name);
        dest.writeString(mobileNumber);
        dest.writeString(fromPlace);
        dest.writeString(destinationPlace);
        dest.writeString(inTime);
        dest.writeString(outTime);
        dest.writeString(vehicleNo);
        dest.writeString(visitorSignUrl);
    }

    public String getVisitorImg() {
        return visitorImg;
    }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public String getDestinationPlace() {
        return destinationPlace;
    }

    public String getInTime() {
        return inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public String getVisitorSignUrl() {
        return visitorSignUrl;
    }

    public void setVisitorImg(String visitorImg) {
        this.visitorImg = visitorImg;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public void setDestinationPlace(String destinationPlace) {
        this.destinationPlace = destinationPlace;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public void setVisitorSignUrl(String visitorSignUrl) {
        this.visitorSignUrl = visitorSignUrl;
    }

    public String getWbId() {
        return wbId;
    }

    public void setWbId(String wbId) {
        this.wbId = wbId;
    }
}
