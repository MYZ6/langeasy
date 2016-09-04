package com.chenyi.langeasy.capture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

		// clip(inputFile, 20 + "", 10 + "", "e:/test5.mp3");
		long startMillis = 114520, endMillis = 120280;

		String formatLength = format(endMillis - startMillis);
		if (endMillis == 0) {
			formatLength = "to_end";
		}

		clip(inputFile, format(startMillis), formatLength, "e:/test5.mp3");
	}

	/**
	 * Timestamps need to be in HH:MM:SS.xxx format for advanced precision (where xxx are milliseconds).
	 * 
	 * @param millis
	 * @return
	 */
	public static String format(long millis) {
		return String.format("%02d:%02d:%02d.%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1),
				millis % TimeUnit.SECONDS.toMillis(1));
	}

	public static void clip(String inputFilePath, String startSeconds, String length, String outFileName)
			throws IOException {
		List<String> clipCmdLst = new ArrayList<>();
		clipCmdLst.add(ffmpegPath);
		clipCmdLst.add("-loglevel");
		clipCmdLst.add("warning");
		clipCmdLst.add("-i");
		clipCmdLst.add(inputFilePath);
		clipCmdLst.add("-ss");
		clipCmdLst.add(startSeconds);
		if (!"to_end".equals(length)) {
			clipCmdLst.add("-t");
			clipCmdLst.add(length);
		}
		clipCmdLst.add(outFileName);

		ProcessBuilder builder = new ProcessBuilder(clipCmdLst);
		builder.redirectErrorStream(true);
		final Process process = builder.start();

		// Watch the process
		watch(process, inputFilePath);
	}

	private static void watch(final Process process, final String inputFilePath) {
		new Thread() {
			public void run() {
				System.out.println(inputFilePath + ", start time is : " + new Date());
				long start = System.currentTimeMillis();
				BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = null;
				try {
					while ((line = input.readLine()) != null) {
						System.out.println(line);
					}
					long end = System.currentTimeMillis();
					System.out.println(inputFilePath + ", consuming seconds : " + (end - start));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
