package com.noveogroup.task5;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MimimiActivity extends ListActivity {

    private static final int MAX_HEIGHT = 300;

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

                    int scaleForLoad = calcMaxScale(options.outHeight);
                    float scaleForDraw = (float) MAX_HEIGHT / (options.outHeight / scaleForLoad);

                    options = new BitmapFactory.Options();
                    options.inSampleSize = scaleForLoad;
                    Bitmap bm = BitmapFactory.decodeStream(getAssets().open(path), null, options);
                    bm = Bitmap.createScaledBitmap(bm, (int) (options.outWidth * scaleForDraw), (int) (options.outHeight * scaleForDraw), false);

                    bitmaps.add(bm);
                }
            }
        } catch (Exception e) {
            Log.e("Mimimi", "Something strange: ", e);
        } catch (OutOfMemoryError err) {
            Log.e("Mimimi", "Houston, we have an out of…");
        }

        adapter = new MimimiAdapter(this, bitmaps);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.ninepatch_red).getConstantState())) {
                    view.setBackground(getResources().getDrawable(R.drawable.ninepatch));
                } else {
                    view.setBackground(getResources().getDrawable(R.drawable.ninepatch_red));
                }

            }
        });
    }

    private int calcMaxScale(int originHeight) {
        int scale = 1;
        int halfHeight = originHeight / 2;

        while ((halfHeight / scale) > MAX_HEIGHT) {
            scale *= 2;
        }

        return scale;
    }
}
