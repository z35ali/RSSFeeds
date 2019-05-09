package com.ali.zafar.rssfeeds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsFeedAdapter extends ArrayAdapter {
    private static final String TAG = "NewsFeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<NewsItem> news;


    public NewsFeedAdapter(Context context, int resource, List<NewsItem> news) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.news = news;
    }

    @Override
    public int getCount() {
        return news.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView== null){
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        NewsItem currentNews = news.get(position);

        viewHolder.tvTitleNews.setText(currentNews.getTitle());
        viewHolder.tvCategoryNews.setText(currentNews.getCategory());
        viewHolder.tvDescriptionNews.setText(removeHtmlTags(currentNews.getDescription()));
        viewHolder.tvAuthorNews.setText(currentNews.getAuthor());
        viewHolder.tvDateNews.setText(currentNews.getPubDate());
        viewHolder.tvLinkNews.setText(currentNews.getLink());

        String[] musicImageParts = currentNews.getDescription().split("'");

        // Image link from internet
        new DownloadImageFromInternet(viewHolder.tvImageNews)
                .execute(musicImageParts[1]);



        return convertView;
    }

    private class ViewHolder {
        final TextView tvTitleNews;
        final TextView tvLinkNews;
        final TextView tvDateNews;
        final TextView tvAuthorNews;
        final TextView tvCategoryNews;
        final TextView tvDescriptionNews;

        final ImageView tvImageNews;

        ViewHolder(View v) {
            this.tvTitleNews = v.findViewById(R.id.tvTitleNews);
            this.tvLinkNews = v.findViewById(R.id.tvLinkNews);
            this.tvDateNews = v.findViewById(R.id.tvDateNews);
            this.tvCategoryNews = v.findViewById(R.id.tvCategoryNews);
            this.tvDescriptionNews = v.findViewById(R.id.tvDescriptionNews);
            this.tvAuthorNews = v.findViewById(R.id.tvAuthorNews);
            this.tvImageNews = v.findViewById(R.id.imageNews);
        }

    }

    public String removeHtmlTags(String inStr) {
        int index=0;
        int index2=0;
        while(index!=-1)
        {
            index = inStr.indexOf("<");
            index2 = inStr.indexOf(">", index);
            if(index!=-1 && index2!=-1){
                inStr = inStr.substring(0, index).concat(inStr.substring(index2+1, inStr.length()));
            }
        }
        return inStr.trim();
    }





    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
