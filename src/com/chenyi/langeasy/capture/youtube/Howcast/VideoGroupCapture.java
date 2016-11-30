package com.chenyi.langeasy.capture.youtube.Howcast;

import java.util.HashSet;
import java.util.Set;

public class VideoGroupCapture {
	private static String dirPath = "E:/langeasy/lucene/youtube/Howcast/";

	public static void main(String[] args) throws Exception {
		int clipCount = 10;
		Set<String> vidLst = new HashSet<>();
		for (int ci = 0; ci < clipCount; ci++) {
			// VideoCapture.handle(ci, dirPath + "playlists" + (ci + 1) + "/");
			CaptionFetch.handle(vidLst, ci, dirPath + "playlists" + (ci + 1) + "/");
			// break;
		}
		int ci = 1;
		// VideoCapture.handle(ci, dirPath + "playlists" + (ci + 1) + "/");
		// CaptionFetch.handle(ci, dirPath + "playlists" + (ci + 1) + "/");
	}

}
