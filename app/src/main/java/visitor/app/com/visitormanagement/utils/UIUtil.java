package visitor.app.com.visitormanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.activities.BaseActivity;
import visitor.app.com.visitormanagement.models.WorkBookModel;

/**
 * Created by jugal on 16/7/16.
 */
public class UIUtil {

    public static final int NONE = 0;
    public static final int CENTER_INSIDE = 1;
    public static final int CENTER_CROP = 2;
    public static final int ONLY_SCALE_DOWN = 3;
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE})
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE, CENTER_INSIDE, CENTER_CROP, ONLY_SCALE_DOWN})
    public @interface ImageScaleType {
    }

    public static void displayProductImage(@Nullable String baseImgUrl, @Nullable String productImgUrl,
                                           ImageView imgProduct, boolean skipMemoryCache) {
        if (productImgUrl != null) {
            String url;
            if (TextUtils.isEmpty(baseImgUrl) || productImgUrl.startsWith("http")) {
                url = productImgUrl;
            } else {
                url = baseImgUrl + productImgUrl;
            }
            UIUtil.displayAsyncImage(imgProduct, url, false,
                    R.drawable.loading_small, 0, 0, skipMemoryCache);
        } else {
            imgProduct.setImageResource(R.drawable.noimage);
        }
    }

    public static void displayAsyncImage(ImageView imageView, String url) {
        displayAsyncImage(imageView, url, false, R.drawable.loading_small);
    }

    public static void displayAsyncImage(ImageView imageView, @DrawableRes int drawableId) {
        displayAsyncImage(imageView, drawableId, false);
    }

    public static void displayAsyncImage(ImageView imageView, @DrawableRes int drawableId,
                                         boolean skipMemoryCache) {
        RequestCreator requestCreator = Picasso.with(imageView.getContext()).load(drawableId);
        if (skipMemoryCache) {
            requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
        }
        requestCreator.into(imageView);
    }

    public static void displayAsyncImage(ImageView imageView, String url, boolean animate,
                                         @DrawableRes int placeHolderDrawableId) {
        displayAsyncImage(imageView, url, animate, placeHolderDrawableId, 0, 0);
    }

    public static void displayAsyncImage(ImageView imageView, String url, boolean animate,
                                         @DrawableRes int placeHolderDrawableId,
                                         int targetImageWidth, int targetImageHeight) {
        displayAsyncImage(imageView, url, animate, placeHolderDrawableId, targetImageWidth,
                targetImageHeight, false);
    }

    public static void displayAsyncImage(ImageView imageView, String url, boolean animate,
                                         @DrawableRes int placeHolderDrawableId,
                                         int targetImageWidth, int targetImageHeight,
                                         boolean skipMemoryCache) {
        displayAsyncImage(imageView, url, animate, placeHolderDrawableId, targetImageWidth,
                targetImageHeight, skipMemoryCache, NONE, null);
    }

    public static void displayAsyncImage(ImageView imageView, String url, boolean animate,
                                         @DrawableRes int placeHolderDrawableId,
                                         int targetImageWidth, int targetImageHeight,
                                         boolean skipMemoryCache, @ImageScaleType int scaleType,
                                         Callback callback) {

        Picasso picasso = Picasso.with(imageView.getContext());
        RequestCreator requestCreator = picasso.load(url)
                .error(R.drawable.noimage);
        if (url == null) {
            requestCreator.into(imageView, callback);
            return;
        } else {
            picasso.cancelRequest(imageView);
        }

        if (placeHolderDrawableId > 0) {
            requestCreator.placeholder(placeHolderDrawableId);
        }
        if (skipMemoryCache) {
            requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
        }
        if (targetImageWidth > 0 && targetImageHeight > 0) {
            requestCreator.resize(targetImageWidth, targetImageHeight);
            Log.i(imageView.getContext().getClass().getSimpleName(),
                    "Loading image " + (skipMemoryCache ? "[NO_MEM_CACHE] " : "")
                            + "(" + targetImageWidth + "," + targetImageHeight + ") = " + url);
        } else {
            Log.i(imageView.getContext().getClass().getSimpleName(), "Loading image = " + url);
        }
        if (!animate) {
            requestCreator.noFade();
        }
        switch (scaleType) {
            case CENTER_INSIDE:
                requestCreator.centerInside();
                break;
            case CENTER_CROP:
                requestCreator.centerCrop();
                break;
            case ONLY_SCALE_DOWN:
                requestCreator.onlyScaleDown();
                break;
        }
        try {
            requestCreator.into(imageView, callback);
        } catch (OutOfMemoryError e) {
            System.gc();
        }
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void reportFormInputFieldError(TextInputLayout textInputLayout, String errMsg) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(errMsg);
    }

    public static void resetFormInputField(TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(false);
        textInputLayout.setError("");
    }

    public static boolean isAlphaString(String matchString) {
        return matchString.matches("[a-zA-Z]+( +[a-zA-Z]+)*");
    }

    public static boolean isEmpty(String str) {
        return str == null || TextUtils.isEmpty(str.trim());
    }

    public static void saveDataToPreference(Context context, HashMap<String, String> hashMap){
        if(hashMap == null) return;
        ApiAdapter.reset();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry)iterator.next();
            editor.putString(String.valueOf(pair.getKey()), String.valueOf(pair.getValue()));
        }
        editor.commit();
    }

    public static void clearDataFromPreference(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Constants.AUTH_TOKEN);
        editor.commit();
    }

    public static boolean isMandatoryParam(ArrayList<String> mandatoryParams, String fieldName){
        for(String mandatoryParam : mandatoryParams){
            if(mandatoryParam.trim().equals(fieldName)){
                return true;
            }
        }
        return false;
    }

}
