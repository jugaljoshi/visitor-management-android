package visitor.app.com.visitormanagement.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import visitor.app.com.visitormanagement.utils.Constants;

/**
 * Created by jugal on 21/7/16.
 */
public class WorkBookModel implements Parcelable{

    @SerializedName(Constants.WB_NAME)
    String wbName;

    @SerializedName(Constants.WB_ID)
    String wbId;

    @SerializedName(Constants.WB_IMG_URL)
    String wbImgUrl;
    public static final Parcelable.Creator<WorkBookModel> CREATOR = new Parcelable.Creator<WorkBookModel>() {
        @Override
        public WorkBookModel createFromParcel(Parcel source) {
            return new WorkBookModel(source);
        }

        @Override
        public WorkBookModel[] newArray(int size) {
            return new WorkBookModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public WorkBookModel(Parcel source) {
        this.wbName = source.readString();
        this.wbId = source.readString();
        this.wbImgUrl = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(wbName);
        dest.writeString(wbId);
        dest.writeString(wbImgUrl);
    }

    public String getWbName() {
        return wbName;
    }

    public String getWbImgUrl() {
        return wbImgUrl;
    }

    public String getWbId() {
        return wbId;
    }
}
