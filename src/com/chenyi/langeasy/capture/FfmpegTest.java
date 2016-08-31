package com.chenyi.langeasy.capture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FfmpegTest {
	public static String ffmpegPath = "E:/ffmpeg/ffmpeg-20160828-a37e6dd-win64-shared/bin/ffmpeg.exe";

	public static void main(String[] args) throws IOException {
		String inputFile = "e:/1035963017.mp3";
		String cmd = ffmpegPath + " -i " + inputFile + " -f segment -segment_time 60 -c copy e:/out%03d.mp3";
		System.out.println(cmd);
		// Runtime rt = Runtime.getRuntime();
		// Process pr = rt.exec(cmd);

		String[] segmentCmds = new String[] { ffmpegPath, "-i", inputFile, "-f", "segment", "-segment_time", "60", "-c",
				"copy", "e:/out%03d.mp3" };

		clip(inputFile, 20, 10, "e:/test5.mp3");
	}

	public static void clip(String inputFilePath, int startSeconds, int length, String outFileName) throws IOException {
		List<String> clipCmdLst = new ArrayList<>();
		clipCmdLst.add(ffmpegPath);
		clipCmdLst.add("-loglevel");
		clipCmdLst.add("warning");
		clipCmdLst.add("-i");
		clipCmdLst.add(inputFilePath);
		clipCmdLst.add("-ss");
		clipCmdLst.add("" + startSeconds);
		if (length != Integer.MAX_VALUE) {
			clipCmdLst.add("-t");
			clipCmdLst.add("" + length);
		}
		clipCmdLst.add(outFileName);

		ProcessBuilder builder = new ProcessBuilder(clipCmdLst);
		builder.redirectErrorStream(true);
		final Process process = builder.start();

		// Watch the process
		watch(process);
	}

	private static void watch(final Process process) {
		new Thread() {
			public void run() {
				BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = null;
				try {
					while ((line = input.readLine()) != null) {
						System.out.println(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
