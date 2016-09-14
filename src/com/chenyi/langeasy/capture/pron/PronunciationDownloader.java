package com.chenyi.langeasy.capture.pron;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.chenyi.langeasy.capture.CaptureUtil;

public class PronunciationDownloader {
	private static CloseableHttpClient httpclient;

	public static void main(String[] args) throws FileNotFoundException, SQLException, IOException {
		httpclient = HttpClients.createDefault();

		Connection conn = CaptureUtil.getConnection();
		listWord(conn);
		CaptureUtil.closeConnection(conn);
	}

	private static void listWord(Connection conn) throws SQLException, FileNotFoundException, IOException {
		String sql = "SELECT mp3path, oggpath FROM vocabulary where oggpath is not null limit 10000";

		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<String> recordLst = new ArrayList<>();
		while (rs.next()) {
			String mp3path = rs.getString("mp3path");
			String oggpath = rs.getString("oggpath");
			recordLst.add(mp3path);
			recordLst.add(oggpath);// handle as mp3
		}
		rs.close();
		st.close();

		int count = 0;
		String prefix = "https://www.oxforddictionaries.com/media/english/";
		for (String mediaUrl : recordLst) {
			count++;
			System.out.println("find seq : " + count);
			System.out.println(mediaUrl);
			String localMediapath = mediaUrl.substring(prefix.length());
			System.out.println(localMediapath);
			String filepath = "e:/langeasy/pronunciation/" + localMediapath;// "ListenData/1688236492/2089837271.mp3";
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
			downloadMedia(mediaUrl, saveFile);
		}

	}

	private static void downloadMedia(String mediaUrl, File saveFile) throws ClientProtocolException, IOException {
		// RequestConfig requestConfig =
		// RequestConfig.custom().setConnectionRequestTimeout(1000).setConnectTimeout(1000)
		// .setSocketTimeout(1000).build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(1000).setSocketTimeout(3000).build();
		HttpGet httpget = new HttpGet(mediaUrl);
		httpget.setConfig(requestConfig);
		long startTime = System.currentTimeMillis();

		try {
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				System.out.println(mediaUrl + " download begin");
				InputStream inputStream = entity.getContent();
				OutputStream outputStream = new FileOutputStream(saveFile);
				IOUtils.copy(inputStream, outputStream);
				outputStream.close();
				System.out.println(mediaUrl + " download success");
				long endTime = System.currentTimeMillis();
				System.out.println("download time elapsed: " + (endTime - startTime));
			}
		} catch (SocketTimeoutException | ConnectTimeoutException | UnknownHostException ex) {
			ex.printStackTrace();
			downloadMedia(mediaUrl, saveFile);
		}
	}
}
