package com.yekong.rss;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import rx.Observable;
import rx.Subscriber;

public class RssReader {
    public static Observable<RssHandler> parse(final String url) {
        return Observable.create(new Observable.OnSubscribe<RssHandler>() {
            @Override
            public void call(Subscriber<? super RssHandler> subscriber) {
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    // Creates a new RssHandler which will do all the parsing.
                    RssHandler handler = new RssHandler();
                    // Pass SaxParser the RssHandler that was created.
                    saxParser.parse(url, handler);
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
