package com.cts.datafeedloader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cts.datafeedloader.vo.Rows;

/**
 * Created by Vijay on 09/12/15.
 *
 * Adapter Class which configs and returns the View for ListView
 */
public class CustomListAdapter extends ArrayAdapter<Rows> {
    private Activity myContext;
    private Rows[] data;

    static class ViewHolder {
        TextView feedTitleView;
        TextView feedDescriptionView;
        ImageView feedImageView;
        String imageURL;
        Bitmap imageBitMap;
    }


    public CustomListAdapter(Context context, int layoutResourceId, ArrayList<Rows> objects) {
        super(context, layoutResourceId, objects);
        myContext = (Activity) context;
        data = objects.toArray(new Rows[objects.size()]);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = myContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.feedImageView = (ImageView) convertView
                    .findViewById(R.id.feedImage);
            viewHolder.feedTitleView = (TextView) convertView
                    .findViewById(R.id.feedTitle);
            viewHolder.feedDescriptionView = (TextView) convertView
                    .findViewById(R.id.feedDescription);
            viewHolder.imageURL = (data[position].imageHref != null?data[position].imageHref:"");
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (data[position].imageHref == null || data[position].imageHref.equals("")) {
            //don't add any image src. 
           // viewHolder.feedImageView.setImageResource(R.drawable.No_Image_Available);       
        	viewHolder.feedImageView.setVisibility(View.GONE);
            Log.d("FeedListAdapter","Image URL is null or empty");
        }else {
        	if(Build.VERSION.SDK_INT >= 11)
        		new DownloadImageAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, viewHolder);
        	else
        		new DownloadImageAsyncTask().execute(viewHolder);
        }

        viewHolder.feedTitleView.setText(data[position].title);
        viewHolder.feedDescriptionView.setText(data[position].description);
        return convertView;
    }

    private class DownloadImageAsyncTask extends AsyncTask<ViewHolder, Void, ViewHolder> {

        @Override
        protected ViewHolder doInBackground(ViewHolder... params) {
            //load image directly
            ViewHolder viewHolder = params[0];
            try {
                URL imageURL = new URL(viewHolder.imageURL);
                viewHolder.imageBitMap = BitmapFactory.decodeStream(imageURL.openStream());
            } catch (IOException e) {
                Log.e("error", "Downloading Image Failed" + e.getStackTrace());
                viewHolder.imageBitMap = null;
            }
            return viewHolder;
        }

        @Override
        protected void onPostExecute(ViewHolder result) {
            if (result.imageBitMap == null) {
            	//don't add any image
            	// result.feedImageView.setImageResource(R.drawable.No_Image_Available);
            	result.feedImageView.setVisibility(View.GONE);
            	Log.d("FeedListAdapter","Downloading Image Failed");
            } else {
                result.feedImageView.setImageBitmap(result.imageBitMap);
            }
        }
    }

    }
