package com.chenyi.langeasy.capture.youtube.tedxtalks;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class Backup {
	private static String dirPath = "E:/langeasy/lucene/youtube/TEDxTalks/";

	public static void main(String[] args) throws Exception {
		int clipCount = 41;

		for (int ci = 0; ci < clipCount; ci++) {
			String subDirPath = dirPath + "playlists" + (ci + 1) + "/";
			File file = new File(subDirPath + "playlists.json");
			String destPath = "E:/langeasy/lucene/bak/TEDxTalks2/" + "playlists" + (ci + 1) + ".json";
			FileUtils.copyFile(file, new File(destPath));
		}
	}
}
