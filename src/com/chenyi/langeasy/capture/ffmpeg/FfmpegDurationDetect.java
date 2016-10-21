package com.chenyi.langeasy.capture.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.chenyi.langeasy.capture.CaptureUtil;

public class FfmpegDurationDetect {
	public static String ffmpegPath = "E:/ffmpeg/ffmpeg-20160828-a37e6dd-win64-shared/bin/ffmpeg.exe";
	public static String ffprobePath = "E:/ffmpeg/ffmpeg-20160828-a37e6dd-win64-shared/bin/ffprobe.exe";

	public static void main(String[] args) throws IOException {
		arr = new JSONArray();
		System.out.println("start time is : " + new Date());
		try {
			list();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// detect(1, new File("E:/langeasy/sentence/415907.mp3"));
		// detect(1, new File("E:/langeasy/number/333.mp3"));
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// System.out.println(arr);
	}

	private static List<Map<String, String>> downloadLst;
	private static List<Map<String, String>> recordLst;
	private static int step = 30;

	public static void list() throws IOException, SQLException {
		Connection conn = CaptureUtil.getConnection();
		String condition = "  AND c.courseid IN ('20140313234040100000005','20151201122924100000218','20130109094550100000681','20140501214935100000017') ";
		String sql = "SELECT courseid, mp3path, min_duration FROM langeasy.course c WHERE 1=1" + condition
				+ "limit 5000";
		// System.out.println(sql);

		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		recordLst = new ArrayList<>();
		while (rs.next()) {
			Map<String, String> record = new HashMap<>();
			String courseid = rs.getString("courseid");
			String mp3path = rs.getString("mp3path");
			String minDuration = rs.getInt("min_duration") + "";
			record.put("courseid", courseid);
			record.put("mp3path", mp3path);
			record.put("minDuration", minDuration);
			recordLst.add(record);
		}
		rs.close();
		st.close();
		conn.close();

		int count = 0;

		downloadLst = new ArrayList<Map<String, String>>();

		step = 4;
		FfmpegDurationDetect jobManager = new FfmpegDurationDetect();
		for (int i = 0; i < 1; i++) {
			Job job = jobManager.new Job(i);
			job.start();
			// break;
		}

		for (int i = 0; i < 2; i++) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int dtotal = downloadLst.size();// about 1800
			System.out.println("dtotal: " + dtotal);
			System.out.println(new JSONArray(downloadLst));
		}

	}

	public static void detect(Map<String, String> record, String filepath) throws IOException {
		List<String> clipCmdLst = new ArrayList<>();
		clipCmdLst.add(ffprobePath);
		clipCmdLst.add("-show_entries");
		clipCmdLst.add("format=duration");
		clipCmdLst.add(filepath);
		// System.out.println(clipCmdLst);

		ProcessBuilder builder = new ProcessBuilder(clipCmdLst);
		builder.redirectErrorStream(true);
		final Process process = builder.start();
		// System.out.println("courseid : " + courseid);

		// Watch the process
		watch(process, record);

	}

	public static JSONArray arr;

	private static void watch(final Process process, final Map<String, String> record) {
		new Thread() {
			public void run() {
				long start = System.currentTimeMillis();
				BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = null;
				String result = "";
				// json.put("courseid", courseid);
				try {
					double duration = 0;
					while ((line = input.readLine()) != null) {
						int mstart = line.indexOf("duration=");
						if (mstart > -1) {
							String sub = line.substring(mstart);
							System.out.println(sub);
							String[] arr = sub.split("=");
							duration = Double.parseDouble(arr[1]);

							String courseid = record.get("courseid");
							String minDuration = record.get("minDuration");
							double d2 = Double.parseDouble(minDuration) / 1000;
							System.out.println(d2);
							if (duration < d2) {
								record.put("duration", duration + "");
								downloadLst.add(record);
							}
						}

						// System.out.println(line);
						result += line;
					}
					if (duration == 0) {
						downloadLst.add(record);
					}
					long end = System.currentTimeMillis();
					// System.out.println(inputFilePath +
					// ", consuming seconds : " + (end - start));
				} catch (IOException e) {
					e.printStackTrace();
				}
				// System.out.println(result);
			}
		}.start();
	}

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			int start = jobIndex * step;
			int end = start + step;
			if (end > recordLst.size()) {
				end = recordLst.size();
			}
			List<Map<String, String>> subLst = recordLst.subList(start, end);
			int count = 0;
			for (Map<String, String> record : subLst) {
				String courseid = record.get("courseid");
				String mp3path = record.get("mp3path");
				String minDuration = record.get("minDuration");

				count++;
				System.err.println("job" + jobIndex + " check seq : " + count);
				String filepath = "e:/langeasy/" + mp3path;// "ListenData/1688236492/2089837271.mp3";
				// String duration = TikaUtil.getDuration(filepath);

				try {
					// System.out.println(filepath);
					detect(record, filepath);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			System.out.println("job" + jobIndex + " last time is : " + new Date());
		}

		public void start() {
			System.out.println("Starting job " + jobIndex);
			if (t == null) {
				t = new Thread(this, "job" + jobIndex);
				t.start();
			}
		}

	}
}
