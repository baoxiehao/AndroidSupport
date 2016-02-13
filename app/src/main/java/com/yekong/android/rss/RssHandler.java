package com.yekong.android.rss;

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

    public String getUrl() {
        return url;
    }

    public List<RssEntry> getEntries() {
        return entries;
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
            this.feed = new RssFeed();
        } else if (RssTag.isItem(qName)) {
            this.currEntry = new RssEntry();
        } else if (RssTag.isLink(qName)) {
            if (currEntry != null) {
                currEntry.setLink(RssTag.getAttrHref(attributes));
            } else if (feed != null) {
                feed.setLink(RssTag.getAttrHref(attributes));
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
                currEntry.setSource(feed.getTitle());
                entries.add(currEntry);
            }
        } else if (this.feed != null) {
            if (RssTag.isTitle(qName)) {
                feed.setTitle(builder.toString().trim());
            } else if (RssTag.isLink(qName) && feed.getLink() == null) {
                feed.setLink(builder.toString().trim());
            } else if (RssTag.isDesc(qName)) {
                feed.setDescription(builder.toString().trim());
            } else if (RssTag.isLastDate(qName)) {
                feed.setLastBuildDate(builder.toString().trim());
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
