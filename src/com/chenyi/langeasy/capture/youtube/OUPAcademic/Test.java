package com.chenyi.langeasy.capture.youtube.OUPAcademic;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Test {
	private static String dirPath = "E:/langeasy/lucene/youtube/OUPAcademic/";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String cpath = dirPath + "playlists.json";
		// ParseUtil.count(cpath);
		// Runtime.getRuntime().exec("explorer.exe /select," + dirPath);
		Runtime.getRuntime().exec("cmd /c start " + dirPath + "caption");
	}
}
