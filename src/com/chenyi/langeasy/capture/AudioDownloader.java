package com.chenyi.langeasy.capture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class AudioDownloader {
	private static CloseableHttpClient httpclient;

	public static void main(String[] args) throws FileNotFoundException, SQLException, IOException {
		httpclient = HttpClients.createDefault();
		listCourse(CaptureUtil.getConnection());
	}

	private static void listCourse(Connection conn) throws SQLException, FileNotFoundException, IOException {
		String sql = "SELECT mp3path FROM langeasy.course c WHERE c.courseid IN "
				+ "( SELECT s.courseid FROM langeasy.vocabulary_audio r "
				+ "INNER JOIN sentence s ON s.id = r.sentenceid GROUP BY s.courseid)" + "limit 500";

		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<String> recordLst = new ArrayList<>();
		while (rs.next()) {
			String mp3path = rs.getString("mp3path");
			recordLst.add(mp3path);
		}
		rs.close();
		st.close();

		int count = 0;
		for (String mp3path : recordLst) {
			count++;
			System.out.println("find seq : " + count);
			String filepath = "e:/langeasy/" + mp3path;// "ListenData/1688236492/2089837271.mp3";
			File saveFile = new File(filepath);
			if (saveFile.exists()) {
				continue;
			}
			String dirpath = saveFile.getParent();
			File dir = new File(dirpath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			System.out.println(dirpath);
			downloadMp3(mp3path, saveFile);
		}

	}

	private static void downloadMp3(String mp3path, File saveFile) throws ClientProtocolException, IOException {
		String url = "http://langeasy.com.cn/" + mp3path;// "ListenData/1688236492/2089837271.mp3";
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			long startTime = System.currentTimeMillis();
			System.out.println(mp3path + " download begin");
			InputStream inputStream = entity.getContent();
			OutputStream outputStream = new FileOutputStream(saveFile);
			IOUtils.copy(inputStream, outputStream);
			outputStream.close();
			System.out.println(mp3path + " download success");
			long endTime = System.currentTimeMillis();
			System.out.println("download time elapsed: " + (endTime - startTime));
		}
	}
}
