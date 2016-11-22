package com.chenyi.langeasy.capture.podcast.yalecourses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class LectureCounter {
	private static String dirPath = "E:/langeasy/lucene/podcast/yale-courses/";

	static List<Map<String, String>> courseLst = new ArrayList<>();

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File sFile = new File(dirPath + "course-list.json");
		String content = IOUtils.toString(new FileInputStream(sFile), "utf-8");
		JSONArray courseArr = new JSONArray(content);
		int total = 0;
		int max = 0;
		for (int i = 0; i < courseArr.length(); i++) {
			JSONObject course = courseArr.getJSONObject(i);
			String count = course.getString("count");
			int icount = Integer.parseInt(count);
			total += icount;
			if (icount > max) {
				max = icount;
			}
		}
		System.out.println(total);
		System.out.println(max);
		if (total > -1) {
			// return;
		}
	}
}
