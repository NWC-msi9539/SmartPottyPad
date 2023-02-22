package nwc.hardware.smartpottypad.datas;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

public class Display {
    private final String TAG = "app.Display";
    private static Display display;
    public static int width;
    public static int height;
    public static float width_Float;
    public static float height_Float;

    private Display(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        width_Float = width * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        height_Float = height * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

        Log.d(TAG, "Successfully Created Display Info to \'Width\' --> " + width);
        Log.d(TAG, "Successfully Created Display Info to \'Height\' --> " + height);
        Log.d(TAG, "Successfully Created Display Info to \'Width_Float\' --> " + width_Float);
        Log.d(TAG, "Successfully Created Display Info to \'Height_Float\' --> " + height_Float);
    }

    public static void createInfo(Context context){
        if(display == null){
            display = new Display(context);
        }
    }
}
