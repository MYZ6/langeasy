package com.chenyi.langeasy.capture.youtube.spotlight;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CheckDownloadRepeat {
	public static void main(String[] args) throws IOException {
		int clipCount = 10;
		String dirPath = "E:/langeasy/lucene/youtube/spotlight/";

		Set<String> vidLst = new HashSet<>();
		int total = 0;
		for (int ci = 0; ci < clipCount; ci++) {
			String subDirPath = dirPath + "playlists" + (ci + 1) + "/caption/";
			File subDir = new File(subDirPath);
			String[] fnameArr = subDir.list();
			vidLst.addAll(Arrays.asList(fnameArr));
			total += fnameArr.length;
		}
		System.out.println(total);
		System.out.println(vidLst.size());
	}
}
