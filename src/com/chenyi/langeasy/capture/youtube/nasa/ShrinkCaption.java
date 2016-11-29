package com.chenyi.langeasy.capture.youtube.nasa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;

import com.chenyi.langeasy.capture.youtube.ParseUtil;

public class ShrinkCaption {
	static List<File> handleLst = new ArrayList<>();
	private static int[] jobStatus;

	private static String dirPath = "E:/langeasy/lucene/youtube/nasa/";

	public static void main(String[] args) throws Exception {
		System.out.println("start time is : " + new Date());

		File dir = new File(dirPath + "caption");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -1);
		long btime = cal.getTimeInMillis();
		System.out.println(new Date(btime));
		for (File file : dir.listFiles()) {
			if (file.lastModified() < btime) {
				// System.out.println(new Date(file.lastModified()));
				// System.out.println(file.getName());
				handleLst.add(file);
			}
			// break;
		}

		int total = handleLst.size();
		System.out.println(total);
		int count = 20;
		step = total / count;
		if (total % step != 0) {
			count += 1;
		}
		System.out.println(step + "\t" + count);
		if (total > -1) {
			// return;
		}

		jobStatus = new int[count];
		int start = 0;
		int end = count;
		ShrinkCaption downloader = new ShrinkCaption();
		for (int i = start; i < end; i++) {
			Job job = downloader.new Job(i);
			jobStatus[i] = 0;
			job.start();
			if (i > 0) {
				// break;
			}
		}
		for (int i = 0; i < 200; i++) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean allFinished = true;
			for (int j = start; j < end; j++) {
				if (jobStatus[j] == 0) {
					allFinished = false;
					break;
				}
			}
			System.out.println(new JSONArray(jobStatus));
			if (allFinished) {
				break;
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
			if (end > handleLst.size()) {
				end = handleLst.size();
			}
			System.out.println(start + "\t" + end);

			List<File> subLst = handleLst.subList(start, end);
			int count = 0;
			for (File file : subLst) {
				count++;
				System.err.println("job" + jobIndex + " handle seq : " + count);
				try {
					ParseUtil.shrink(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (count > 1) {
					// break;
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			jobStatus[jobIndex] = 1;
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
