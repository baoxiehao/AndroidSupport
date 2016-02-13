package com.yekong.android.rss;

import com.yekong.android.util.Logger;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import rx.Observable;
import rx.Subscriber;

public class RssReader {

    private static final String TAG = "RssReader";

    public static Observable<RssHandler> parse(final String url) {
        return Observable.create(new Observable.OnSubscribe<RssHandler>() {
            @Override
            public void call(Subscriber<? super RssHandler> subscriber) {
                try {
                    final boolean usesUrlConn = false;
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    HttpURLConnection conn = null;
                    try {
                        // Creates a new RssHandler which will do all the parsing.
                        RssHandler handler = new RssHandler(url);
                        Logger.d(TAG, "parse url: " + url);
                        if (usesUrlConn) {
                            URL connUrl = new URL(url);
                            conn = (HttpURLConnection) connUrl.openConnection();
                            conn.setRequestProperty("Connection", "Keep-Alive");
                            conn.setRequestProperty("Charset", "UTF-8");
                            conn.setRequestProperty("User-Agent",
                                    "Mozilla/4.9 (compatible; MSIE 5.0; Windows NT; DigExt)");
                            // Pass SaxParser the RssHandler that was created.
                            saxParser.parse(conn.getInputStream(), handler);
                        } else {
                            saxParser.parse(url, handler);
                        }
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(handler);
                            subscriber.onCompleted();
                        }
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
//                        subscriber.onError(e);
//                        Logger.e(TAG, "parse error", e);
                        Logger.e(TAG, String.format("parse error for %s: %s", url, e.getMessage()));
                    }
                }
            }
        });
    }
}
