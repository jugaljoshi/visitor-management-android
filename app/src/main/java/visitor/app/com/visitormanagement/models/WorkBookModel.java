package visitor.app.com.visitormanagement.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import visitor.app.com.visitormanagement.utils.Constants;

/**
 * Created by jugal on 21/7/16.
 */
public class WorkBookModel implements Parcelable{

    @SerializedName(Constants.WB_NAME)
    private String wbName;

    @SerializedName(Constants.WB_ID)
    private String wbId;

    @SerializedName(Constants.VISITOR_MANDATORY_FIELDS)
    private ArrayList<String> visitorMandatoryFields;

    @SerializedName(Constants.WB_IMG_URL)
    private String wbImgUrl;
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

    private int id;
    private static final int COLUMN_ID_INDEX = 0;
    private static final int COLUMN_WORK_BOOK_NAME = 1;
    private static final int COLUMN_WB_TYPE_ID = 2;
    private static final int COLUMN_VISITOR_MANDATORY_FIELDS = 3;
    public WorkBookModel(Cursor cursor) {
        this.id = cursor.getInt(COLUMN_ID_INDEX);
        this.wbName = cursor.getString(COLUMN_WORK_BOOK_NAME);
        this.wbId = cursor.getString(COLUMN_WB_TYPE_ID);
        String visitorMandatoryFieldsString = cursor.getString(COLUMN_VISITOR_MANDATORY_FIELDS);
        Gson gson = new Gson();
        this.visitorMandatoryFields = gson.fromJson(visitorMandatoryFieldsString, new TypeToken<ArrayList<String>>() {}.getType());
    }


    public int getId() {
        return id;
    }

    public WorkBookModel(Parcel source) {
        this.wbName = source.readString();
        this.wbId = source.readString();
        this.wbImgUrl = source.readString();

        boolean isMandatoryFieldsNull = source.readByte() == (byte) 0;
        if(!isMandatoryFieldsNull) {
            visitorMandatoryFields = new ArrayList<>();
            source.readStringList(visitorMandatoryFields);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(wbName);
        dest.writeString(wbId);
        dest.writeString(wbImgUrl);
        boolean isMandatoryFieldsNull = visitorMandatoryFields == null;
        dest.writeByte(isMandatoryFieldsNull ? (byte) 0 : (byte) 1);
        if(!isMandatoryFieldsNull){
            dest.writeStringList(visitorMandatoryFields);
        }

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

    public ArrayList<String> getVisitorMandatoryFields() {
        return visitorMandatoryFields;
    }
}
