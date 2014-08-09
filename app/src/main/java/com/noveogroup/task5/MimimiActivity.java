package com.noveogroup.task5;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MimimiActivity extends ListActivity implements AbsListView.OnScrollListener {

    private MimimiAdapter adapter;
    private List<Bitmap> bitmaps;
    private Bitmap placeholder;
    private Bitmap placeholderLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        placeholder = Bitmap.createBitmap(1, getResources().getDimensionPixelSize(R.dimen.image_height), conf);
        placeholderLoading = Bitmap.createBitmap(1, getResources().getDimensionPixelSize(R.dimen.image_height), conf);

        bitmaps = new ArrayList<Bitmap>();

        try {
            for (String path: getAssets().list("")) {
                if (path.endsWith(".jpg")) {
                    bitmaps.add(placeholder);
                }
            }
        } catch (Exception e) {
            Log.e("Mimimi", "Something strange: ", e);
        } catch (OutOfMemoryError err) {
            Log.e("Mimimi", "Houston, we have an out of…");
        }

        adapter = new MimimiAdapter(this, bitmaps);
        setListAdapter(adapter);

        getListView().setOnScrollListener(this);
    }

    private int calcMaxScale(int originHeight) {
        int scale = 1;
        int halfHeight = originHeight / 2;

        while ((halfHeight / scale) > getResources().getDimensionPixelSize(R.dimen.image_height)) {
            scale *= 2;
        }

        return scale;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            if (bitmaps.get(i).equals(placeholder)) {
                bitmaps.set(i, placeholderLoading);
                new DownloadFilesTask().execute(i);
            }
        }
    }

    private class DownloadFilesTask extends AsyncTask <Integer, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Integer... integers) {

            int from = integers[0];

            try {
                int i = 0;
                for (String path: getAssets().list("")) {
                    if (path.endsWith(".jpg")) {
                        if (i == from) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeStream(getAssets().open(path), null, options);

                            int scaleForLoad = calcMaxScale(options.outHeight);
                            float scaleForDraw = (float) getResources().getDimensionPixelSize(R.dimen.image_height) / (options.outHeight / scaleForLoad);

                            options = new BitmapFactory.Options();
                            options.inSampleSize = scaleForLoad;
                            Bitmap bm = BitmapFactory.decodeStream(getAssets().open(path), null, options);
                            bm = Bitmap.createScaledBitmap(bm, (int) (options.outWidth * scaleForDraw), (int) (options.outHeight * scaleForDraw), false);

                            bitmaps.set(i, bm);
                        }
                        i++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
