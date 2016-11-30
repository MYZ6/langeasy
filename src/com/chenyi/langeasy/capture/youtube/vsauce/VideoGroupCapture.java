package com.chenyi.langeasy.capture.youtube.vsauce;

import com.chenyi.langeasy.capture.youtube.tedxtalks.CaptionFetch;

public class VideoGroupCapture {
	private static String dirPath = "E:/langeasy/lucene/youtube/Vsauce/";

	public static void main(String[] args) throws Exception {
		int ci = 0;
		// VideoCapture.handle(ci, dirPath);
		CaptionFetch.handle(ci, dirPath);
	}

}
