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
    private boolean music = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listData = (ListView) findViewById(R.id.xmlListView);

        downloadUrl("https://www.cbc.ca/cmlink/rss-topstories");
        setTitle("Top Stories");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String feedUrl;

        switch(id){
            case R.id.menuWorld:
                setTitle("World News");
                feedUrl = "https://www.cbc.ca/cmlink/rss-world";
                music = false;
                break;
            case R.id.menuTopNews:
                setTitle("Top Stories");
                feedUrl = "https://www.cbc.ca/cmlink/rss-topstories";
                music = false;
                break;
            case R.id.menuCanada:
                setTitle("Canada News");
                feedUrl = "https://www.cbc.ca/cmlink/rss-canada";
                music = false;
                break;
            case R.id.menuPolitics:
                setTitle("Politics News");
                feedUrl = "https://www.cbc.ca/cmlink/rss-politics";
                music = false;
                break;
            case R.id.menuBusiness:
                setTitle("Business News");
                feedUrl = "https://www.cbc.ca/cmlink/rss-business";
                music = false;
                break;
            case R.id.menuHealth:
                setTitle("Health News");
                feedUrl = "https://www.cbc.ca/cmlink/rss-health";
                music = false;
                break;
            case R.id.menuArts:
                setTitle("Arts and Entertainment News");
                feedUrl = "https://www.cbc.ca/cmlink/rss-arts";
                music = false;
                break;
            case R.id.menuTech:
                setTitle("Technology and Science News");
                feedUrl = "https://www.cbc.ca/cmlink/rss-technology";
                music = false;
                break;
            case R.id.menuSoon:
                setTitle("Coming Soon");
                feedUrl = "https://rss.itunes.apple.com/api/v1/ca/apple-music/coming-soon/all/100/explicit.rss";
                music = true;
                break;
            case R.id.menuTopSongs:
                setTitle("Top Songs");
                feedUrl = "https://rss.itunes.apple.com/api/v1/ca/apple-music/top-songs/all/100/explicit.rss";
                music = true;
                break;
            case R.id.menuAlbums:
                setTitle("Top Albums");
                feedUrl = "https://rss.itunes.apple.com/api/v1/ca/apple-music/top-albums/all/100/explicit.rss";
                music = true;
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

            if (music == true) {
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
}
