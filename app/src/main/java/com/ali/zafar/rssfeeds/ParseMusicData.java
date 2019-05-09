package com.ali.zafar.rssfeeds;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseMusicData {
    private static final String TAG = "ParseMusicData";
    private ArrayList<MusicItem> data;

    public ParseMusicData() {
        this.data = new ArrayList<>();
    }

    public ArrayList<MusicItem> getData() {
        return data;
    }

    public boolean parse(String xmlData){
        boolean status = true;
        MusicItem currentRecord = null;
        boolean inEntry = false;
        String textValue = "";

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //Log.d(TAG, "parse: Starting tag for " + tagName);
                      if ("item".equalsIgnoreCase(tagName)){
                          inEntry = true;
                          currentRecord = new MusicItem();
                      }
                      break;

                      case XmlPullParser.TEXT:
                          textValue = xpp.getText();
                          break;
                    case XmlPullParser.END_TAG:
                       // Log.d(TAG, "parse: Ending tag for "+ tagName);
                        if(inEntry){
                            if("item".equalsIgnoreCase(tagName)){
                                data.add(currentRecord);
                                inEntry = false;
                            }else if ("title".equalsIgnoreCase(tagName)){
                                currentRecord.setTitle(textValue);
                            }else if ("link".equalsIgnoreCase(tagName)){
                                currentRecord.setLink(textValue);
                            }else if ("guid".equalsIgnoreCase(tagName)){
                                currentRecord.setGuid(textValue);
                            }else if ("pubdate".equalsIgnoreCase(tagName)){
                                currentRecord.setPubDate(textValue);
                            }else if ("description".equalsIgnoreCase(tagName)){
                                currentRecord.setDescription(textValue);
                            }else if ("category".equalsIgnoreCase(tagName) && !textValue.equals("Music")) {
                                currentRecord.setCategory(textValue);
                            }
                        }
                        break;
                    default:
                        // DO nothing

                }
                eventType = xpp.next();
            }
            /*for (MusicItem data: data){
                Log.d(TAG, "*******************");
                Log.d(TAG, data.toString());
            } */
        }catch(Exception e ){
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
