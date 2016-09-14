package com.chenyi.langeasy.capture.pron;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.chenyi.langeasy.capture.ffmpeg.FfmpegVolumeNormalize;

public class VolumeNormalize {
	private static String ffmpegPath = "E:/ffmpeg/ffmpeg-20160828-a37e6dd-win64-shared/bin/ffmpeg.exe";

	private static String operateDir = "E:/langeasy/pronunciation-ogg";

	public static void main(String[] args) throws IOException {
		detectArr = new JSONArray();
		System.out.println("start time is : " + new Date());
		list();

		// normalize(1, 29.2, new File("E:/langeasy/sentence/7681.mp3"));
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// System.out.println(arr);
	}

	static VolumeNormalize manager;

	public static void list() throws IOException {
		File dir = new File(operateDir);
		int count = 0;

		manager = new VolumeNormalize();
		for (File file : dir.listFiles()) {
			count++;
			if (count % 500 != 0) {
				// continue;
			}

			DetectJob job = manager.new DetectJob(count, file);
			job.start();

			System.out.println("find seq : " + count);
			// if (count > 200) {
			//			break;
			// }
		}
		for (int i = 0; i < 50; i++) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("time is : " + new Date());
			System.out.println(detectArr);
		}

	}

	public static JSONArray detectArr;

	public static void detect(int jobIndex, File file) throws IOException {
		List<String> clipCmdLst = new ArrayList<>();
		clipCmdLst.add(ffmpegPath);
		clipCmdLst.add("-i");
		clipCmdLst.add(file.getAbsolutePath());
		clipCmdLst.add("-af");
		clipCmdLst.add("volumedetect");
		clipCmdLst.add("-f");
		clipCmdLst.add("null");
		clipCmdLst.add("/dev/null");
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

	private static void watch(final Process process, final String filename) {
		new Thread() {
			public void run() {
				long start = System.currentTimeMillis();
				BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = null;
				String result = "";
				JSONObject json = new JSONObject();
				json.put("sentence", filename);
				try {
					while ((line = input.readLine()) != null) {
						int mstart = line.indexOf("mean_volume");
						if (mstart > -1) {
							String sub = line.substring(mstart + 13);
							System.out.println(sub);
							json.put("mean_volume", sub);
							String mean_volume2 = sub.substring(1, sub.indexOf(" dB"));
							json.put("mean_volume2", mean_volume2);

							NormalizeJob job = manager.new NormalizeJob(0, Double.parseDouble(mean_volume2),
									new File(operateDir + File.separator + filename));
							job.start();
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
				detectArr.put(json);
				// System.out.println(result);
			}
		}.start();
	}

	class DetectJob implements Runnable {
		private Thread t;
		private int jobIndex;
		private File file;

		DetectJob(int jobIndex, File file) {
			this.jobIndex = jobIndex;
			this.file = file;
		}

		public void run() {
			try {
				detect(jobIndex, file);
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

	class NormalizeJob implements Runnable {
		private Thread t;
		private int jobIndex;
		private File srcFile;
		private double mean_volume2;

		NormalizeJob(int jobIndex, double mean_volume2, File file) {
			this.jobIndex = jobIndex;
			this.srcFile = file;
			this.mean_volume2 = mean_volume2;
		}

		public void run() {
			try {
				String destFilePath = "E:/langeasy/pronunciation-normalize/" + srcFile.getName();
				FfmpegVolumeNormalize.normalize(jobIndex, mean_volume2, srcFile, destFilePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void start() {
			if (t == null) {
				t = new Thread(this, "job" + srcFile.getName());
				t.start();
			}
		}

	}
}
