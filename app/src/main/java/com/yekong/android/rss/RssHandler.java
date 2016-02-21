package com.yekong.android.rss;

import com.yekong.android.util.DateUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class RssHandler extends DefaultHandler {

    private String url;
    private RssFeed feed;
    private List<RssEntry> entries;
    private RssEntry currEntry;
    private StringBuilder builder;

    public RssHandler(String url) {
        this.url = url;
    }

    public RssFeed getFeed() {
        return feed;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        entries = new ArrayList<RssEntry>();
        builder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (RssTag.isFeed(qName)) {
            feed = new RssFeed();
            feed.entries = entries;
        } else if (RssTag.isItem(qName)) {
            currEntry = new RssEntry();
        } else if (RssTag.isLink(qName)) {
            if (currEntry != null) {
                currEntry.setLink(RssTag.getAttrHref(attributes));
            } else if (feed != null) {
                feed.link = RssTag.getAttrHref(attributes);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (this.currEntry != null) {
            if (RssTag.isTitle(qName)) {
                currEntry.setTitle(builder.toString().trim());
            } else if (RssTag.isLink(qName) && currEntry.getLink() == null) {
                currEntry.setLink(builder.toString().trim());
            } else if (RssTag.isDesc(qName)) {
                currEntry.setDescription(builder.toString().trim());
            } else if (RssTag.isPubDate(qName)) {
                currEntry.setPubDate(builder.toString().trim());
            } else if (RssTag.isItem(qName)) {
                currEntry.setSource(feed.title);
                entries.add(currEntry);
            }
        } else if (this.feed != null) {
            if (RssTag.isTitle(qName)) {
                feed.title = builder.toString().trim();
            } else if (RssTag.isLink(qName) && feed.link == null) {
                feed.link = builder.toString().trim();
            } else if (RssTag.isDesc(qName)) {
                feed.description = builder.toString().trim();
            } else if (RssTag.isLastBuildDate(qName)) {
                feed.lastBuildDate = DateUtils.normalizeDate(builder.toString().trim());
            }
        }
        builder.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currEntry != null) {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        } else if (feed != null) {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }
    }
}
