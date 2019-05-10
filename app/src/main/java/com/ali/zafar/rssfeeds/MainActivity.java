package com.ali.zafar.rssfeeds;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listData;
    private boolean musicState;
    private String feedUrl;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listData = findViewById(R.id.xmlListView);

        // If device is rotated and has a savedInstance restore
        if (savedInstanceState != null){
            feedUrl = savedInstanceState.getString("feedUrl");
            title = savedInstanceState.getString("title");
            musicState = savedInstanceState.getBoolean("musicState");
            downloadUrl(feedUrl);
        }else{

            // Otherwise load default news feed
            feedUrl = "https://www.cbc.ca/cmlink/rss-topstories";
            title = "Top Stories";
            musicState = false;
            downloadUrl(feedUrl);
        }

        setTitle(title);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Switch cases to call appropriate RSS feed for each menu item
        switch(id){
            case R.id.menuWorld:
                title = "World News";
                setTitle("World News");
                feedUrl = "https://www.cbc.ca/cmlink/rss-world";
                setTitle(title);
                musicState = false;
                break;
            case R.id.menuTopNews:
                title = "Top Stories";
                feedUrl = "https://www.cbc.ca/cmlink/rss-topstories";
                setTitle(title);
                musicState = false;
                break;
            case R.id.menuCanada:
                title = "Canada News";
                feedUrl = "https://www.cbc.ca/cmlink/rss-canada";
                setTitle(title);
                musicState = false;
                break;
            case R.id.menuPolitics:
                title = "Politics News";
                feedUrl = "https://www.cbc.ca/cmlink/rss-politics";
                setTitle(title);
                musicState = false;
                break;
            case R.id.menuBusiness:
                title = "Business News";
                feedUrl = "https://www.cbc.ca/cmlink/rss-business";
                setTitle(title);
                musicState = false;
                break;
            case R.id.menuHealth:
                title = "Health News";
                feedUrl = "https://www.cbc.ca/cmlink/rss-health";
                setTitle(title);
                musicState = false;
                break;
            case R.id.menuArts:
                title = "Arts and Entertainment News";
                feedUrl = "https://www.cbc.ca/cmlink/rss-arts";
                setTitle(title);
                musicState = false;
                break;
            case R.id.menuTech:
                title = "Technology and Science News";
                feedUrl = "https://www.cbc.ca/cmlink/rss-technology";
                setTitle(title);
                musicState = false;
                break;
            case R.id.menuSoon:
                title = "Coming Soon";
                feedUrl = "https://rss.itunes.apple.com/api/v1/ca/apple-music/coming-soon/all/25/explicit.rss";
                setTitle(title);
                musicState = true;
                break;
            case R.id.menuTopSongs:
                title = "Top Songs";
                feedUrl = "https://rss.itunes.apple.com/api/v1/ca/apple-music/top-songs/all/25/explicit.rss";
                setTitle(title);
                musicState = true;
                break;
            case R.id.menuAlbums:
                title = "Top Albums";
                feedUrl = "https://rss.itunes.apple.com/api/v1/ca/apple-music/top-albums/all/25/explicit.rss";
                setTitle(title);
                musicState = true;
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        downloadUrl(feedUrl);
        return true;
    }

    public void downloadUrl(String url){
        //Log.d(TAG, "onCreate: starting new AsyncTask");
        DownloadData downloadData = new DownloadData();
        downloadData.execute(url);
        //Log.d(TAG, "onCreate: done");
    }

    public class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.d(TAG, "OnPostExecute: parameter is " + s);

            if (musicState == true) {
                ParseMusicData parseData = new ParseMusicData();
                parseData.parse(s);

                //ArrayAdapter<MusicItem> arrayAdapter = new ArrayAdapter<MusicItem>(MainActivity.this, R.layout.list_item, parseData.getData());
                //listData.setAdapter(arrayAdapter);
                MusicFeedAdapter feedAdapter = new MusicFeedAdapter(MainActivity.this, R.layout.musiclistrecord, parseData.getData());
                listData.setAdapter(feedAdapter);
            }else{
                ParseNewsData parseData = new ParseNewsData();
                parseData.parse(s);

                //ArrayAdapter<NewsItem> arrayAdapter = new ArrayAdapter<NewsItem>(NewsActivity.this, R.layout.list_item, parseData.getData());
                //listData.setAdapter(arrayAdapter);
                NewsFeedAdapter feedAdapter = new NewsFeedAdapter(MainActivity.this, R.layout.newslistrecord, parseData.getData());
                listData.setAdapter(feedAdapter);

            }

        }


        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed == null){
                Log.e(TAG, "doInBackground: Error Downloading");
            }
            return rssFeed;
        }

        public String downloadXML(String urlPath){
            StringBuilder xmlResult = new StringBuilder();

            try{
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: THe response code was " + response);
                //InputStream inputStream = connection.getInputStream();
                //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                //BufferedReader reader = new BufferedReader(inputStreamReader);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;

                // Read 500 characters at a time
                char[] inputBuffer = new char[500];

                // Loop keeps running until end of input stream is reached
                // Only append to xmlResult if there are characters to be read
                while(true){
                    charsRead = reader.read(inputBuffer);
                    if (charsRead < 0){
                        break;
                    }
                    if (charsRead > 0){
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();
                return xmlResult.toString();
            }catch(MalformedURLException e){
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch(IOException e){
                Log.e(TAG, "downloadXML: IO Exception Reading Data: " + e.getLocalizedMessage());
            } catch(SecurityException e){
                Log.e(TAG, "downloadXML: Security Exception. Needs Permission?");
                // e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("feedUrl", feedUrl);
        outState.putString("title", title);
        outState.putBoolean("musicState", musicState);
    }
}
