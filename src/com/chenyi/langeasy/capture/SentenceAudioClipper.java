package com.chenyi.langeasy.capture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

public class SentenceAudioClipper {
	public static void main(String[] args) throws IOException, JSONException, SQLException {
		listSentence(CaptureUtil.getConnection());

	}

	private static List<Map<String, Object>> sentenceLst = new ArrayList<>();

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
		for (Map<String, Object> map : sentenceLst) {
			count++;
			System.err.println("find seq : " + count);
			Integer sentenceid = (Integer) map.get("sentenceid");
			String saveFilepath = "e:/langeasy/sentence" + sentenceid + ".mp3";
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

			Integer starttime = (Integer) map.get("starttime") / 1000;
			Integer endtime = (Integer) map.get("endtime") / 1000;
			Integer length = endtime - starttime;
			if (endtime == 0) {
				length = Integer.MAX_VALUE;
			}
			FfmpegTest.clip(mp3FilePath, starttime, length, saveFilepath);
		}

	}

}
