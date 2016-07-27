package visitor.app.com.visitormanagement.models;

import visitor.app.com.visitormanagement.utils.Constants;
import com.google.gson.annotations.SerializedName;
/**
 * Created by jugal on 16/7/16.
 */
public class ApiResponse<T> extends BaseApiResponse {

    @SerializedName(Constants.RESPONSE)
    public T apiResponseContent;
}
