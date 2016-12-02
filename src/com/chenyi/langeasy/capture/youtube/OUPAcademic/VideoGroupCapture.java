package com.chenyi.langeasy.capture.youtube.OUPAcademic;

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
		channelName = "um";// University of Michigan
		channelName = "PennState";// Penn State University
		channelName = "nyu";// New York University
		channelName = "MichiganStateU";//
		channelName = "columbiauniversity";//

		channelName = "scishow";//
		channelName = "crashcourse";//
		channelName = "RosannaPansino";//
		channelName = "nucleusanimation";// type 1,
		channelName = "bozemanbiology";//
		channelName = "CancerQuest";//
		channelName = "tytuniversity";// ThinkTank
		channelName = "JohnsHopkins";//
		channelName = "01032010814";// Grant Thompson - "The King of Random"
		channelName = "itsokaytobesmart";//
		// channelName = "alltime10s";//
		// channelName = "theslowmoguys";//
		// channelName = "sxephil";//Philip DeFranco
		// channelName = "utaustintexas";//The University of Texas at Austin
		// channelName = "UCSanDiego";//
		// channelName = "ACSReactions";//
		// channelName = "minutephysics";//
		// channelName = "1veritasium";//
		// channelName = "physicswoman";//
		// channelName = "numberphile";//
		// channelName = "patrickJMT";//
		// channelName = "Vihart";//
		// channelName = "khanacademy";//
		// channelName = "TheYoungTurks";//
		// channelName = "DDTop20";//Planet Dolan
		// channelName = "BuzzFeedVideo";//
		// channelName = "mathantics";//
		// channelName = "FOXADHD";// Animation Domination High-Def

		/**
		 * chemistry, biology, physics, math, history, geography, phylosophy
		 */

		// channelName = "UCtelevision";// 1
		// channelName = "UChicago";// 1
		// channelName = "UWTV";// type 1, University of Washington
		// channelName = "Duke";// type 1, University Duke
		// channelName = "truTVnetwork";// type 1,
		// channelName = "emergencyawesome";// type 1,
		// channelName = "CrazyRussianHacker";// type 1,
		// channelName = "periodicvideos";// type 1,
		// channelName = "Bloomberg";// type 1,
		// channelName = "WatchMojo";// type 1,

		// channelName = "YaleUniversity";//type1, compare with yale courses

		// channelName = "UCYO_jab_esuFRV4b17AJtAw";// type 2 channel, 3Blue1Brown
		dirPath = "E:/langeasy/lucene/youtube/" + channelName + "/";
		String cpath = dirPath + "playlists.json";
		int choice = 1;
		// choice = 2;
		if (choice == 1) {
			int type = 0;
			type = 1;// 1 for "?shelf_id=0&view=1&sort=dd"
			// type = 2;// not "user" but "channel"
			PlaylistsParseUtil.handle(channelName, type);
			VideoCapture.handle(0, dirPath);
			ParseUtil.count(cpath);
			int zeroPtotal = CheckDownloadRepeat.checkRepeat(dirPath);
			if (zeroPtotal == 0) {
				download(dirPath);
			}
		} else {
			download(dirPath);

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
