package com.chenyi.langeasy.capture.youtube.latenight;

import com.chenyi.langeasy.capture.youtube.tedxtalks.CaptionFetch;
import com.chenyi.langeasy.capture.youtube.tedxtalks.VideoCapture;

public class VideoGroupCapture {
	private static String dirPath = "E:/langeasy/lucene/youtube/latenight/";

	public static void main(String[] args) throws Exception {
		int ci = 0;
		VideoCapture.handle(ci, dirPath);
		CaptionFetch.handle(ci, dirPath);
	}

}
