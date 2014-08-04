package com.noveogroup.task5;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MimimiActivity extends ListActivity {

    private static final int MAX_HEIGHT = 480;

    private MimimiAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        List<Bitmap> bitmaps = new ArrayList<Bitmap>();

        try {
            for (String path: getAssets().list("")) {
                if (path.endsWith(".jpg")) {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeStream(getAssets().open(path), null, options);
                    int originWidth = options.outWidth;
                    int originHeight = options.outHeight;
                    int scale = Math.round((float)MAX_HEIGHT / (float)originHeight);

                    options = new BitmapFactory.Options();
                    options.inSampleSize = scale;
                    bitmaps.add(BitmapFactory.decodeStream(getAssets().open(path), null, options));
                }
            }
        } catch (Exception e) {
            Log.e("Mimimi", "Something strange: ", e);
        } catch (OutOfMemoryError err) {
            Log.e("Mimimi", "Houston, we have an out of…");
        }

        adapter = new MimimiAdapter(this, bitmaps);

        setListAdapter(adapter);
    }
}
