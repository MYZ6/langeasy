package com.chenyi.langeasy.capture.youtube.tedxtalks;

public class VideoGroupCapture {
	private static String dirPath = "E:/langeasy/lucene/youtube/TEDxTalks/";

	public static void main(String[] args) throws Exception {
		int clipCount = 41;
		for (int ci = 0; ci < clipCount; ci++) {
			// VideoCapture.handle(ci, dirPath + "playlists" + (ci + 1) + "/");
			CaptionFetch.handle(ci, dirPath + "playlists" + (ci + 1) + "/");
			// break;
		}
		int ci = 40;
		// VideoCapture.handle(ci, dirPath + "playlists" + (ci + 1) + "/");
	}

}
