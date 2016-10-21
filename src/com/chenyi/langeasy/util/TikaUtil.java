package com.chenyi.langeasy.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaUtil {
	public static void main(String[] args) throws FileNotFoundException, IOException, SAXException, TikaException {
		Parser parser = new AutoDetectParser(); // Should auto-detect!
		ContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();

		try (InputStream stream = new FileInputStream("e:/test33.mp3")) {
			parser.parse(stream, handler, metadata, new ParseContext());
		}

		out(metadata, Metadata.CONTENT_TYPE);
		out(metadata, Metadata.AUTHOR);
		out(metadata, TikaCoreProperties.TITLE);
		out(metadata, TikaCoreProperties.CREATOR);
		out(metadata, XMPDM.COMPOSER);
		out(metadata, XMPDM.GENRE);
		out(metadata, XMPDM.LOG_COMMENT);
		out(metadata, XMPDM.TRACK_NUMBER);
		out(metadata, XMPDM.DISC_NUMBER);
		out(metadata, XMPDM.AUDIO_SAMPLE_RATE);
		out(metadata, XMPDM.AUDIO_CHANNEL_TYPE);
		out(metadata, XMPDM.AUDIO_COMPRESSOR);
		out(metadata, XMPDM.DURATION);

		// Check core properties
		// assertEquals("audio/mpeg", metadata.get(Metadata.CONTENT_TYPE));
		// assertEquals("Test Title", metadata.get(TikaCoreProperties.TITLE));
		// assertEquals("Test Artist",
		// metadata.get(TikaCoreProperties.CREATOR));
		// assertEquals("Test Artist", metadata.get(Metadata.AUTHOR));
	}

	public static String getDuration(String file) {
		Parser parser = new AutoDetectParser(); // Should auto-detect!
		ContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();

		try (InputStream stream = new FileInputStream(file)) {
			parser.parse(stream, handler, metadata, new ParseContext());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
		return metadata.get(XMPDM.DURATION);
	}

	private static void out(Metadata metadata, Property prop) {
		System.out.println(prop.getName() + ": " + metadata.get(prop));
	}

	private static void out(Metadata metadata, String prop) {
		System.out.println(prop + ": " + metadata.get(prop));
	}
}
