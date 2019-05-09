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
        View view = layoutInflater.inflate(layoutResource, parent, false);
        TextView tvName = (TextView)view.findViewById(R.id.tvName);
        TextView tvArtist = (TextView)view.findViewById(R.id.tvArtist);
        TextView tvSummary = (TextView)view.findViewById(R.id.tvSummary);
        TextView tvLink = (TextView)view.findViewById(R.id.tvLink);
        TextView tvDate = (TextView)view.findViewById(R.id.tvDate);
        FeedEntry currentData = data.get(position);
        String[] artistSongParts = currentData.getTitle().split("-");

        if (artistSongParts.length ==2) {
            tvArtist.setText("Artist: "+artistSongParts[1].trim());
            tvName.setText("Song: "+artistSongParts[0].trim());
        }else{
            tvArtist.setText(currentData.getTitle());
        }
        tvLink.setText("Apple Music Link:\n" + currentData.getLink());
        tvDate.setText("Publication Date: " + currentData.getPubdate().substring(0, currentData.getPubdate().length()-15));
        tvSummary.setText("Genre: "+currentData.getCategory());

        return view;
    }
}
