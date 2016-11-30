package com.chenyi.langeasy.capture.youtube.spotlight;

import com.chenyi.langeasy.capture.youtube.tedxtalks.CaptionFetch;

public class VideoGroupCapture {
	private static String dirPath = "E:/langeasy/lucene/youtube/spotlight/";

	public static void main(String[] args) throws Exception {
		int clipCount = 10;
		for (int ci = 0; ci < clipCount; ci++) {
			// VideoCapture.handle(ci, dirPath + "playlists" + (ci + 1) + "/");
			CaptionFetch.handle(ci, dirPath + "playlists" + (ci + 1) + "/");
			// break;
		}
		int ci = 1;
		// VideoCapture.handle(ci, dirPath + "playlists" + (ci + 1) + "/");
		// CaptionFetch.handle(ci, dirPath + "playlists" + (ci + 1) + "/");
	}

}
