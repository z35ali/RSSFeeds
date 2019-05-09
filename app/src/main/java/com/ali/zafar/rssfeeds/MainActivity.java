package com.ali.zafar.rssfeeds;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listData = (ListView) findViewById(R.id.xmlListView);
        Log.d(TAG, "onCreate: starting new AsyncTask");
        DownloadData downloadData = new DownloadData();
        downloadData.execute("https://rss.itunes.apple.com/api/v1/ca/apple-music/top-songs/all/50/explicit.rss");
        Log.d(TAG, "onCreate: done");
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "OnPostExecute: parameter is " + s);
            ParseData parseData = new ParseData();
            parseData.parse(s);

            //ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(MainActivity.this, R.layout.list_item, parseData.getData());
            //listData.setAdapter(arrayAdapter);
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseData.getData());
            listData.setAdapter(feedAdapter);

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

        private String downloadXML(String urlPath){
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
