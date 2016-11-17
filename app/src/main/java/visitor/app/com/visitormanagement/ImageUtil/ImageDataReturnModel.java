package visitor.app.com.visitormanagement.ImageUtil;

import android.graphics.Bitmap;

/**
 * Created by jugal on 9/10/16.
 */

public class ImageDataReturnModel {
    private String filePath;
    private Bitmap imageBitMap;

    public ImageDataReturnModel(String filePath, Bitmap imageBitMap) {
        this.filePath = filePath;
        this.imageBitMap = imageBitMap;
    }

    public String getFilePath() {
        return filePath;
    }

    public Bitmap getImageBitMap() {
        return imageBitMap;
    }
}
