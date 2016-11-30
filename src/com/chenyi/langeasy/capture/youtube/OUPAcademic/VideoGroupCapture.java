package com.chenyi.langeasy.capture.youtube.OUPAcademic;

import java.io.File;

import com.chenyi.langeasy.capture.youtube.ParseUtil;
import com.chenyi.langeasy.capture.youtube.PlaylistsParseUtil;
import com.chenyi.langeasy.capture.youtube.oxford.CheckDownloadRepeat;
import com.chenyi.langeasy.capture.youtube.tedxtalks.CaptionFetch;
import com.chenyi.langeasy.capture.youtube.tedxtalks.VideoCapture;

public class VideoGroupCapture {
	private static String channelName = "OxfordSBS";
	private static String dirPath;

	public static void main(String[] args) throws Exception {
		channelName = "princetonuniversity";
		channelName = "UCBerkeley";
		channelName = "UCLA";
		// channelName = "um";// University of Michigan
		// channelName = "PennState";// Penn State University
		// channelName = "nyu";// New York University
		// channelName = "MichiganStateU";//
		// channelName = "columbiauniversity";//

		// channelName = "UCtelevision";// 1
		// channelName = "UChicago";// 1
		// channelName = "UWTV";//type 1, University of Washington
		// channelName = "Duke";//type 1, University Duke

		// channelName = "YaleUniversity";//type1, compare with yale courses
		dirPath = "E:/langeasy/lucene/youtube/" + channelName + "/";
		String cpath = dirPath + "playlists.json";
		int choice = 1;
		choice = 2;
		if (choice == 1) {
			int type = 0;
			// type = 1;// 1 for "?shelf_id=0&view=1&sort=dd"
			PlaylistsParseUtil.handle(channelName, type);
			VideoCapture.handle(0, dirPath);
			ParseUtil.count(cpath);
			CheckDownloadRepeat.checkRepeat(dirPath);
		} else {
			download(dirPath);

			File cdir = new File(dirPath + "caption");
			System.out.println(dirPath);
			System.out.println("caption files downloaded count: " + cdir.list().length);

			// System.out.println("Would you like to open the caption directory? 1 or else");
			// Scanner sc = new Scanner(System.in);
			// int i = sc.nextInt();
			// if (i == 1) {
			//
			// }
		}
	}

	public static void download(String dirPath) throws Exception {
		int ci = 0;
		CaptionFetch.handle(ci, dirPath);
		CheckDownloadRepeat.check(dirPath);
	}

}
