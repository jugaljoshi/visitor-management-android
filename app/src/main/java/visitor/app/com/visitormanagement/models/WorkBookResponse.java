package visitor.app.com.visitormanagement.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import visitor.app.com.visitormanagement.utils.Constants;

/**
 * Created by jugal on 24/7/16.
 */
public class WorkBookResponse implements Parcelable {

    @SerializedName(Constants.WB_TYPES)
    private ArrayList<WorkBookTypeModel> workBookTypeModelArrayList;

    @SerializedName(Constants.VISITOR_MANDATORY_FIELDS)
    private ArrayList<String> visitorMandatoryFields;

    public WorkBookResponse(Parcel source){
        boolean isWorkBookTypeModelNull = source.readByte() == (byte) 1;
        if(!isWorkBookTypeModelNull){
            this.workBookTypeModelArrayList = source.createTypedArrayList(WorkBookTypeModel.CREATOR);
        }

        boolean isVisitorMandatoryFieldsNull = source.readByte() == (byte) 1;
        if(!isVisitorMandatoryFieldsNull){
            this.visitorMandatoryFields = source.createStringArrayList();
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        boolean isWorkBookTypeModelNull = workBookTypeModelArrayList == null;
        dest.writeByte(isWorkBookTypeModelNull ? (byte) 1 : (byte) 0);
        if(!isWorkBookTypeModelNull){
            dest.writeTypedList(workBookTypeModelArrayList);
        }
        boolean isVisitorMandatoryFieldsNull = visitorMandatoryFields == null;
        dest.writeByte(isVisitorMandatoryFieldsNull ? (byte) 1 : (byte) 0);
        if (!isVisitorMandatoryFieldsNull) {
            dest.writeStringList(visitorMandatoryFields);
        }
    }


    public static final Parcelable.Creator<WorkBookResponse> CREATOR = new Parcelable.Creator<WorkBookResponse>(){

        @Override
        public WorkBookResponse createFromParcel(Parcel source) {
            return new WorkBookResponse(source);
        }

        @Override
        public WorkBookResponse[] newArray(int size) {
            return new WorkBookResponse[0];
        }
    };

    public ArrayList<WorkBookTypeModel> getWorkBookTypeModelArrayList() {
        return workBookTypeModelArrayList;
    }

    public ArrayList<String> getVisitorMandatoryFields() {
        return visitorMandatoryFields;
    }
}
