package visitor.app.com.visitormanagement.ImageUtil;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import visitor.app.com.visitormanagement.interfaces.ImageUtilAware;

/**
 * Created by jugal on 1/9/14.
 */
public class ImageUtil {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static String storeBitMapToFile(Bitmap bitmap, String fileName){
        try{
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
            File dir = new File(file_path);
            if(!dir.exists())
                dir.mkdirs();
            //File file = new File(dir, "sketchpad" + pad.t_id + ".png");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File file = new File(dir, "IMG_" + timeStamp + ".jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            return file.toString();
        }catch (Exception e){
            return null;
        }
    }
    public static Bitmap getBitmap(String pathOfInputImage, ImageUtilAware imageUtilAware) {
        try {
            int inWidth = 0;
            int inHeight = 0;

            InputStream in = new FileInputStream(pathOfInputImage);

            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;

            int dstHeight = inWidth;
            int dstWidth = inHeight;
            int imageSizeInMB = (dstHeight * dstWidth) / (1024 * 1024);
            if (imageSizeInMB > 18) {
                imageUtilAware.showErrorDialog("Please upload image size less than 6MB");
                return null;
            }
            while (imageSizeInMB > 1) { // previously 1
                dstHeight -= 10;
                dstWidth -= 10;
                imageSizeInMB = (dstHeight * dstWidth) / (1024 * 1024);
            }

            // decode full image pre-resized
            in = new FileInputStream(pathOfInputImage);
            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)

            // calculate dstWidth and dstHeight

            options.inSampleSize = Math.max(inWidth / dstWidth, inHeight / dstHeight);
            // decode full image
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            //int imageSizeInMB = roughBitmap.getByteCount()/(1024*1024);

            // calc exact destination size
            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, dstWidth, dstHeight);
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);

            // resize bitmap
            Bitmap resizedBitmap;
            try {
                resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]),
                        (int) (roughBitmap.getHeight() * values[4]), true);
            } catch (OutOfMemoryError e) {
                //ImageLoader.getInstance().clearDiskCache();
                //ImageLoader.getInstance().clearMemoryCache();
                resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]),
                        (int) (roughBitmap.getHeight() * values[4]), true);
            }
            return resizedBitmap;
        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
        } catch (OutOfMemoryError e) {
            //ImageLoader.getInstance().clearDiskCache();
            //ImageLoader.getInstance().clearMemoryCache();
            imageUtilAware.showErrorDialog("Please upload image size less than 6MB");
            return null;
        }

        return null;
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("MainActivity.error", "Directory not created");
        }
        return file;
    }


    public static Bitmap getImageBitmap(String pathOfInputImage) {
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = new FileInputStream(pathOfInputImage);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap bitmap = null;
            in = new FileInputStream(pathOfInputImage);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                bitmap = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) x,
                        (int) y, true);
                bitmap.recycle();
                bitmap = scaledBitmap;

                System.gc();
            } else {
                bitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("bitmap size - width: ", bitmap.getWidth() + ", height: " +
                    bitmap.getHeight());
            return bitmap;
        } catch (IOException e) {
            Log.e(e.getMessage(), e.toString());
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    /*
    public static void insertToDB(String prescriptionId, ArrayList<byte[]> arrayListByteArray){
        for(int i=0; i<arrayListByteArray.size(); i++){
            byte[] imageBytes = arrayListByteArray.get(i);
            int chunkNumber = 0, startPointer = 0, offset = 500000;
            int maxChunks = (int)Math.ceil((float)imageBytes.length/(float)offset);
            int imageSequence = i+1;
            insertBase64StingToDataBase(imageBytes, offset, imageSequence, chunkNumber, startPointer, maxChunks==0 ? 1 : maxChunks, prescriptionId);
        }
    }


    private static void insertBase64StingToDataBase(byte[] imageByte, int offset, int imageSequence, int chunkNumber,
                                             int startPointer, int maxChunks, String prescriptionId){

        byte[] outputBytes;
        if(imageByte.length -  startPointer<= offset) {
            outputBytes = new byte[offset];
            System.arraycopy(imageByte, startPointer, outputBytes, 0, imageByte.length -  startPointer);
            insertToDataBase(outputBytes, imageSequence, maxChunks, chunkNumber, prescriptionId);
            chunkNumber++;
            return;
        }

        outputBytes = new byte[offset];
        System.arraycopy(imageByte, startPointer, outputBytes, 0, offset);
        insertToDataBase(outputBytes, imageSequence,  maxChunks, chunkNumber,prescriptionId);
        chunkNumber++;
        insertBase64StingToDataBase(imageByte, offset, imageSequence++, chunkNumber, startPointer+offset, maxChunks, prescriptionId);

    }

    private static void insertToDataBase(byte[] outputBytes, int imageSequence, int maxChunkSize,int chunkNumber,
                                  String prescriptionId){
        PrescriptionImageAdapter prescriptionImageAdapter = null;
        try {
            String base64EncodedChunkedImage = Base64.encodeToString(outputBytes, Base64.DEFAULT);
            prescriptionImageAdapter = new PrescriptionImageAdapter(activity);
            prescriptionImageAdapter.insert(prescriptionId, String.valueOf(chunkNumber), String.valueOf(maxChunkSize),
                    base64EncodedChunkedImage, String.valueOf(imageSequence));
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (prescriptionImageAdapter != null)
                prescriptionImageAdapter.close();
        }
    }
    */
}
