package visitor.app.com.visitormanagement.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import visitor.app.com.visitormanagement.utils.Constants;

/**
 * Created by jugal on 23/7/16.
 */
public class WorkBookTypeModel implements Parcelable{

    @Override
    public int describeContents() {
        return 0;
    }

    @SerializedName(Constants.WB_TYPE)
    private String wbType;

    @SerializedName(Constants.WB_TYPE_ID)
    private String wbTypeId;

    public WorkBookTypeModel(Parcel source) {
        this.wbType = source.readString();
        this.wbTypeId = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(wbType);
        dest.writeString(wbTypeId);
    }


    public String getWbType() {
        return wbType;
    }

    public String getWbTypeId() {
        return wbTypeId;
    }


    public static final Parcelable.Creator<WorkBookTypeModel> CREATOR = new Parcelable.Creator<WorkBookTypeModel>() {
        @Override
        public WorkBookTypeModel createFromParcel(Parcel source) {
            return new WorkBookTypeModel(source);
        }

        @Override
        public WorkBookTypeModel[] newArray(int size) {
            return new WorkBookTypeModel[size];
        }
    };

}
