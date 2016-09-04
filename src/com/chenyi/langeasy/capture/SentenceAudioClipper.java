package com.chenyi.langeasy.capture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

public class SentenceAudioClipper {
	public static void main(String[] args) throws IOException, JSONException, SQLException {
		Connection conn = CaptureUtil.getConnection();
		System.out.println("start time is : " + new Date());
		listSentence(conn);
		CaptureUtil.closeConnection(conn);
	}

	private static List<Map<String, Object>> sentenceLst = new ArrayList<>();

	private static List<Map<String, String>> clipperLst;

	private static void listSentence(Connection conn)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT s.id, s.decodestarttime, s.endtime, c.mp3path FROM langeasy.vocabulary_audio r "
				+ "LEFT JOIN sentence s ON s.id = r.sentenceid "
				+ "LEFT JOIN langeasy.course c ON c.courseid = s.courseid GROUP BY r.sentenceid limit 5000";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			Integer sentenceid = rs.getInt("id");
			Integer starttime = rs.getInt("decodestarttime");
			Integer endtime = rs.getInt("endtime");
			String mp3path = rs.getString("mp3path");
			Map<String, Object> map = new HashMap<>();
			map.put("sentenceid", sentenceid);
			map.put("starttime", starttime);
			map.put("endtime", endtime);
			map.put("mp3path", mp3path);
			sentenceLst.add(map);
		}
		rs.close();
		st.close();

		int count = 0;
		clipperLst = new ArrayList<>();
		for (Map<String, Object> map : sentenceLst) {
			count++;
			System.err.println("find seq : " + count);
			Integer sentenceid = (Integer) map.get("sentenceid");
			String saveFilepath = "e:/langeasy/sentence/" + sentenceid + ".mp3";
			File saveFile = new File(saveFilepath);
			if (saveFile.exists()) {
				continue;
			}
			String dirpath = saveFile.getParent();
			File dir = new File(dirpath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			System.out.println(dirpath);

			String mp3path = (String) map.get("mp3path");
			String mp3FilePath = "e:/langeasy/" + mp3path;// "ListenData/1688236492/2089837271.mp3";

			long startMillis = (Integer) map.get("starttime"), endMillis = (Integer) map.get("endtime");
			String formatLength = FfmpegTest.format(endMillis - startMillis);
			if (endMillis == 0) {
				formatLength = "to_end";
			}

			Map<String, String> clipper = new HashMap<>();
			clipper.put("mp3FilePath", mp3FilePath);
			clipper.put("saveFilepath", saveFilepath);
			clipper.put("starttime", FfmpegTest.format(startMillis));
			clipper.put("length", formatLength);
			clipperLst.add(clipper);
			// FfmpegTest.clip(mp3FilePath, starttime, length, saveFilepath);
		}

		int total = clipperLst.size();// about 1800
		System.out.println(total);
		int step = 30;

		SentenceAudioClipper manager = new SentenceAudioClipper();
		for (int i = 0; i < 360; i++) {
			Job job = manager.new Job(i);
			job.start();
		}

	}

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			Connection conn = CaptureUtil.getConnection();

			int step = 10;
			int start = jobIndex * step;
			List<Map<String, String>> subLst = clipperLst.subList(start, start + step);
			int count = 0;
			for (Map<String, String> map : subLst) {
				count++;
				System.err.println("job" + jobIndex + " clip seq : " + count);
				try {
					FfmpegTest.clip(map.get("mp3FilePath"), map.get("starttime"), map.get("length"),
							map.get("saveFilepath"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			CaptureUtil.closeConnection(conn);
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
