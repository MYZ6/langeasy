package com.chenyi.langeasy.capture.podcast.serial;

public class Test {
	public static void main(String[] args) {
		String link = "/season-two/11/present-for-duty";
		link = "http://genius.com/Serial-podcast-episode-12-what-we-know-annotated";
		// String filename = link.substring(1).replace("/", "_");
		String filename = "season-one_" + link.substring(33, link.lastIndexOf("-annotated"));
		System.out.println(filename);
	}
}
