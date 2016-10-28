package com.chenyi.langeasy.capture.podcast.tal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class ThisAmericanLifeTranscript {
	static List<Integer> downloadLst = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		for (int i = 1; i < 600; i++) {
			downloadLst.add(i);
		}

		step = 30;
		ThisAmericanLifeTranscript downloader = new ThisAmericanLifeTranscript();
		for (int i = 0; i < 20; i++) {
			Job job = downloader.new Job(i);
			job.start();
			if (i > 2) {
				// return;
			}
		}
	}

	private static int step = 30;

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			int start = jobIndex * step;
			int end = start + step;
			if (end > downloadLst.size()) {
				end = downloadLst.size();
			}
			System.out.println(start + "\t" + end);

			List<Integer> subLst = downloadLst.subList(start, end);
			int count = 0;
			for (Integer id : subLst) {
				count++;
				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					parseEpisode(httpclient, id);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
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

	private static void parseEpisode(CloseableHttpClient httpclient, Integer episodeIndex) throws Exception {
		System.out.println(episodeIndex + ", start time is : " + new Date());
		long start = System.currentTimeMillis();

		String dirPath = "e:/langeasy/podcast/thisamericanlife/transcript";
		File saveFile = new File(dirPath + File.separator + episodeIndex + ".html");
		if (saveFile.exists()) {
			return;
		}

		String url = "http://www.thisamericanlife.org/radio-archives/episode/" + episodeIndex + "/transcript";
		HttpGet httpget = new HttpGet(url);

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			long startTime = System.currentTimeMillis();
			System.out.println(episodeIndex + " download begin");
			InputStream inputStream = entity.getContent();
			OutputStream outputStream = new FileOutputStream(saveFile);
			IOUtils.copy(inputStream, outputStream);
			outputStream.close();
			System.out.println(episodeIndex + " download success");
			long endTime = System.currentTimeMillis();
			System.out.println("download time elapsed: " + (endTime - startTime));
		}
		long end = System.currentTimeMillis();
		System.out.println(episodeIndex + ", consuming time : " + (end - start));

	}
}
