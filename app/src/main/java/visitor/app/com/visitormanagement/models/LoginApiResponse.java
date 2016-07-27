package visitor.app.com.visitormanagement.models;

import com.google.gson.annotations.SerializedName;

import visitor.app.com.visitormanagement.utils.Constants;

/**
 * Created by jugal on 16/7/16.
 */
public class LoginApiResponse {

    @SerializedName(Constants.AUTH_TOKEN)
    public String bbToken;
}
