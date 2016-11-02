package com.chenyi.langeasy.capture.podcast.morningedition;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TranscriptShrink {
	static List<JSONObject> downloadLst = new ArrayList<>();

	private static String dirPath = "E:/langeasy/lucene/podcast/morningedition/";

	public static void main(String[] args) throws Exception {
		File transcriptDir = new File(dirPath + File.separator + "transcript");
		int count = 0;
		for (File file : transcriptDir.listFiles()) {
			count++;
			if (count > 20) {
				// return;
			}
			System.out.println(count + "/" + file.getName());
			parseStory(file);
		}
	}

	private static int step = 30;

	private static void parseStory(File origFile) throws Exception {
		File saveFile = new File(dirPath + File.separator + "transcript2" + File.separator + origFile.getName());
		if (saveFile.exists()) {
			return;
		}
		String sResult = IOUtils.toString(new FileInputStream(origFile), "utf-8");
		Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return;
		}
		Elements eles = doc.select(".storytext");
		if (eles.size() == 0) {
			System.err.println(origFile.getName() + " has no transcript");
			return;
		}
		Element transcriptEle = eles.get(0);
		FileUtils.writeStringToFile(saveFile, transcriptEle.outerHtml(), StandardCharsets.UTF_8);

	}
}
