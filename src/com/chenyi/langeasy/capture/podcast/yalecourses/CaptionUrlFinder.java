package com.chenyi.langeasy.capture.podcast.yalecourses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

public class CaptionUrlFinder {
	private static String dirPath = "E:/langeasy/lucene/podcast/yale-courses/";

	static List<Map<String, String>> courseLst = new ArrayList<>();

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File sFile = new File(dirPath + "video.html");
		String content = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		// String url =
		// "https://www.youtube.com/watch?v=FGvWvsJcIEw&index=1&list=PLh9mgdi4rNeysmUGbzcgg-_zrFXNY3WrB";
		// System.out.println(url);
		// Document doc = CaptureUtil.timeoutRequest(url);
		// String content = doc.html();

		String ttsUrl = find(content);
		System.out.println(ttsUrl);
	}

	public static String find(String content) {
		String pattern = "'TTS_URL':\\s\"(.*)\",";

		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = r.matcher(content);
		String ttsUrl = null;
		if (m.find()) {
			// System.out.println("Found value: " + m.group(0));
			ttsUrl = m.group(1);
			ttsUrl = StringEscapeUtils.unescapeJava(ttsUrl);
			ttsUrl += "&lang=en&fmt=srv3&kind=asr";
		}
		return ttsUrl;
	}
}
