package com.yekong.android.rss;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class OpmlHandler extends DefaultHandler {

    public static final String OUTLINE = "outline";
    public static final String TEXT = "text";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String XML_URL = "xmlUrl";
    public static final String HTML_URL = "htmlUrl";

    private List<OpmlEntry> entries;
    private OpmlEntry currEntry;
    private StringBuilder builder;

    public OpmlHandler() {
    }

    public List<OpmlEntry> getEntries() {
        return entries;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        entries = new ArrayList<>();
        builder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (OUTLINE.equals(qName)) {
            currEntry = new OpmlEntry();
            currEntry.setText(attributes.getValue(TEXT));
            currEntry.setTitle(attributes.getValue(TITLE));
            currEntry.setType(attributes.getValue(TYPE));
            currEntry.setXmlUrl(attributes.getValue(XML_URL));
            currEntry.setHtmlUrl(attributes.getValue(HTML_URL));
            entries.add(currEntry);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        builder.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currEntry != null) {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }
    }
}
