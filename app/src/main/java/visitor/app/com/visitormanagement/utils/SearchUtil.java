package visitor.app.com.visitormanagement.utils;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import visitor.app.com.visitormanagement.interfaces.ApiService;
import visitor.app.com.visitormanagement.models.ApiResponse;
import visitor.app.com.visitormanagement.models.AutoSearchResponse;

/**
 * Created by jugal on 8/8/16.
 */
public class SearchUtil {

    public static Cursor searchQueryCall(String query, Context context) {
        if (TextUtils.isEmpty(query.trim()) || (query.trim().length() < 2)) return null;
        // If not present in local db or is older than a day

        // Get the results by querying server
        ArrayList<AutoSearchResponse> autoSearchResponse = null;
        if (DataUtil.isInternetAvailable(context)) {
            ApiService bigBasketApiService = ApiAdapter.getApiService(context);
            try {
                Call<ApiResponse<ArrayList<AutoSearchResponse>>> call = bigBasketApiService.getSearchTerms(query);
                Response<ApiResponse<ArrayList<AutoSearchResponse>>> response = call.execute();
                if (response.isSuccessful()) {
                    ApiResponse<ArrayList<AutoSearchResponse>> autoSearchApiResponse = response.body();
                    switch (autoSearchApiResponse.status) {
                        case 0:
                            autoSearchResponse = autoSearchApiResponse.apiResponseContent;
                            break;
                    }
                }
            } catch (IOException e) {
                // Fail silently
            }
        }

        MatrixCursor matrixCursor = null;
        if (autoSearchResponse != null && autoSearchResponse.size() > 0) {
            matrixCursor = getMatrixCursor(autoSearchResponse);
        }
        if (matrixCursor == null) {
            // When no products found so that top-searches can be shown
            matrixCursor = instantiateMatrixCursor();
        }
        return matrixCursor;
    }

    private static MatrixCursor getMatrixCursor(ArrayList<AutoSearchResponse> autoSearchResponse) {
        MatrixCursor matrixCursor = instantiateMatrixCursor();
        //last two column are for left and right icon for a row
        int startVal = 0;
        for (AutoSearchResponse searchResponse: autoSearchResponse) {
            matrixCursor.addRow(new String[]{String.valueOf(startVal), searchResponse.getName(),
                    searchResponse.getWbId()});
            startVal++;
        }
        return matrixCursor;
    }

    private static MatrixCursor instantiateMatrixCursor() {
        return new MatrixCursor(new String[]{BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA});
    }

}
