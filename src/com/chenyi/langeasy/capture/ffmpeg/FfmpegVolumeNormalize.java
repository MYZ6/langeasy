package com.chenyi.langeasy.capture.ffmpeg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class FfmpegVolumeNormalize {
	public static String ffmpegPath = "E:/ffmpeg/ffmpeg-20160828-a37e6dd-win64-shared/bin/ffmpeg.exe";

	public static void main(String[] args) throws IOException {
		arr = new JSONArray();
		System.out.println("start time is : " + new Date());
		test();

		// normalize(1, 29.2, new File("E:/langeasy/sentence/7681.mp3"));
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// System.out.println(arr);
	}

	public static void test() throws IOException {
		int count = 0;

		FfmpegVolumeNormalize manager = new FfmpegVolumeNormalize();
		File bookFile = new File("E:\\dB.json");
		JSONArray arr = null;
		try {
			String sResult = IOUtils.toString(new FileInputStream(bookFile), "utf-8");
			arr = new JSONArray(sResult);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < arr.length(); i++) {
			JSONObject sentence = arr.getJSONObject(i);

			count++;
			if (count % 500 != 0) {
				// continue;
			}

			String filename = sentence.getString("sentence");
			double mean_volume2 = sentence.getDouble("mean_volume2");
			if (mean_volume2 > 26 || mean_volume2 < 24) {
				Job job = manager.new Job(count, mean_volume2, new File("E:/langeasy/sentence/" + filename));
				job.start();
			}

			System.out.println("find seq : " + count);
			// if (count > 200) {
			// break;
			// }
		}

	}

	public static void normalize(int jobIndex, double mean_volume2, File file) throws IOException {
		List<String> clipCmdLst = new ArrayList<>();
		clipCmdLst.add(ffmpegPath);
		clipCmdLst.add("-y");
		clipCmdLst.add("-i");
		clipCmdLst.add(file.getAbsolutePath());
		clipCmdLst.add("-af");

		double rate = 25 / 37.8;
		// rate = 37.8 / 25;
		// System.out.println(rate);
		if (rate > -2) {
			// return;
		}
		rate = 3;
		// clipCmdLst.add("volume=" + rate);
		double change = mean_volume2 - 25;
		clipCmdLst.add("volume=" + change + "dB");
		clipCmdLst.add("E:/langeasy/sentence_normalize/" + file.getName());
		// String result = "";
		// for (String param : clipCmdLst) {
		// result += param + " ";
		// }
		// System.out.println(result);

		ProcessBuilder builder = new ProcessBuilder(clipCmdLst);
		builder.redirectErrorStream(true);
		final Process process = builder.start();
		System.out.println("jobIndex : " + jobIndex);

		// Watch the process
		watch(process, file.getName());

	}

	public static JSONArray arr;

	private static void watch(final Process process, final String sentence) {
		new Thread() {
			public void run() {
				long start = System.currentTimeMillis();
				BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = null;
				String result = "";
				JSONObject json = new JSONObject();
				json.put("sentence", sentence);
				try {
					while ((line = input.readLine()) != null) {
						int mstart = line.indexOf("mean_volume");
						if (mstart > -1) {
							String sub = line.substring(mstart + 13);
							System.out.println(sub);
							json.put("mean_volume", sub);
							json.put("mean_volume2", sub.substring(1, sub.indexOf(" dB")));
						}
						int maxstart = line.indexOf("max_volume");
						if (maxstart > -1) {
							String sub = line.substring(maxstart + 12);
							// System.out.println(sub);
							json.put("max_volume", sub);
							json.put("max_volume2", sub.substring(1, sub.indexOf(" dB")));
						}
						// System.out.println(line);
						result += line;
					}
					long end = System.currentTimeMillis();
					// System.out.println(inputFilePath + ", consuming seconds : " + (end - start));
				} catch (IOException e) {
					e.printStackTrace();
				}
				arr.put(json);
				// System.out.println(result);
			}
		}.start();
	}

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;
		private File file;
		private double mean_volume2;

		Job(int jobIndex, double mean_volume2, File file) {
			this.jobIndex = jobIndex;
			this.file = file;
			this.mean_volume2 = mean_volume2;
		}

		public void run() {
			try {
				normalize(jobIndex, mean_volume2, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void start() {
			if (t == null) {
				t = new Thread(this, "job" + file.getName());
				t.start();
			}
		}

	}
}
