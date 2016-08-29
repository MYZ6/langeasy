package com.chenyi.langeasy.capture;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlCapture {
	public static void main(String[] args) throws IOException, JSONException, SQLException {
		String word = "maritime";
		String url = "http://www.dictionary.com/browse/" + word;
		Document doc = Jsoup.connect(url).get();
		System.out.println(doc.html());
	}
}
