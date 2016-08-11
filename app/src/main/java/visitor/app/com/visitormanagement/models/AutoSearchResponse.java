package visitor.app.com.visitormanagement.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import visitor.app.com.visitormanagement.utils.Constants;

/**
 * Created by jugal on 8/8/16.
 */
public class AutoSearchResponse implements Parcelable {

    public static final Creator<AutoSearchResponse> CREATOR = new Creator<AutoSearchResponse>() {
        @Override
        public AutoSearchResponse createFromParcel(Parcel source) {
            return new AutoSearchResponse(source);
        }

        @Override
        public AutoSearchResponse[] newArray(int size) {
            return new AutoSearchResponse[size];
        }
    };

    @SerializedName(Constants.WB_ID)
    private String wbId;
    private String name;


    public AutoSearchResponse(Parcel source) {
        wbId = source.readString();
        name = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(wbId);
        dest.writeString(name);
    }

    public String getWbId() {
        return wbId;
    }

    public String getName() {
        return name;
    }
}
