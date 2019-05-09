package com.ali.zafar.rssfeeds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> data;


    public FeedAdapter(Context context, int resource, List<FeedEntry> data) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
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


        FeedEntry currentData = data.get(position);
        String[] artistSongParts = currentData.getTitle().split("-");

        if (artistSongParts.length ==2) {
            viewHolder.tvArtist.setText("Artist: " + artistSongParts[1].trim());
            viewHolder.tvName.setText("Title: " + artistSongParts[0].trim());
        }else{
            viewHolder.tvArtist.setText(currentData.getTitle());
        }
        viewHolder.tvLink.setText("Apple Music Link:\n" + currentData.getLink());
        viewHolder.tvDate.setText("Publication Date: " + currentData.getPubdate().substring(0, currentData.getPubdate().length() - 15));
        viewHolder.tvSummary.setText("Genre: " + currentData.getCategory());

        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvLink;
        final TextView tvArtist;
        final TextView tvDate;
        final TextView tvSummary;

        ViewHolder(View v) {
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
            this.tvLink = v.findViewById(R.id.tvLink);
            this.tvDate = v.findViewById(R.id.tvDate);
        }

    }
}
