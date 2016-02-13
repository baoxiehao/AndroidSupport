package com.yekong.android.rss;

import android.content.Context;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by baoxiehao on 16/2/4.
 */
public class OpmlReader {
    public static Observable<OpmlHandler> parse(final Context context, final String assetFileName) {
        return Observable.create(new Observable.OnSubscribe<OpmlHandler>() {
            @Override
            public void call(Subscriber<? super OpmlHandler> subscriber) {
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    // Creates a new RssHandler which will do all the parsing.
                    OpmlHandler handler = new OpmlHandler();
                    // Pass SaxParser the RssHandler that was created.
                    saxParser.parse(context.getAssets().open(assetFileName), handler);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(handler);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }
}
