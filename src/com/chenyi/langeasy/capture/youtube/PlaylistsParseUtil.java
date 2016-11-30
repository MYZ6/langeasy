package com.chenyi.langeasy.capture.youtube;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chenyi.langeasy.capture.CaptureUtil;

public class PlaylistsParseUtil {
	private static String channelName = "OUPAcademic";
	private static String dirPath;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		dirPath = "E:/langeasy/lucene/youtube/" + channelName + "/";
		System.out.println("start time is : " + new Date());

		String cpath = dirPath + "playlists.html";
		String clink = "https://www.youtube.com/user/" + channelName + "/playlists";
		// clink += "?shelf_id=0&view=1&sort=dd";
		// clink = "https://www.youtube.com/user/latenight/playlists?shelf_id=0&view=1&sort=dd";
		List<Map<String, String>> collectionLst = playlists(clink);

		File sFile = new File(dirPath + "playlists.json");
		FileUtils.writeStringToFile(sFile, new JSONArray(collectionLst).toString(3), StandardCharsets.UTF_8);
		System.out.println("end time is : " + new Date());
	}

	public static void handle(String channelName, int type) throws FileNotFoundException, IOException {
		dirPath = "E:/langeasy/lucene/youtube/" + channelName + "/";
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdir();
		} else {
			System.err.println(channelName + " already handled!");
			System.exit(1);
		}
		System.out.println("start time is : " + new Date());

		String cpath = dirPath + "playlists.html";
		String clink = "https://www.youtube.com/user/" + channelName + "/playlists";
		if (type == 1) {
			clink += "?shelf_id=0&view=1&sort=dd";
		}
		// clink = "https://www.youtube.com/user/latenight/playlists?shelf_id=0&view=1&sort=dd";
		List<Map<String, String>> collectionLst = playlists(clink);

		File sFile = new File(dirPath + "playlists.json");
		FileUtils.writeStringToFile(sFile, new JSONArray(collectionLst).toString(3), StandardCharsets.UTF_8);
		System.out.println("end time is : " + new Date());
	}

	public static List<Map<String, String>> playlists(String clink) throws FileNotFoundException, IOException {
		// File file = new File(cpath);
		// String content = IOUtils.toString(new FileInputStream(file), "utf-8");
		// Document doc = Jsoup.parse(content);

		Document doc = CaptureUtil.timeoutRequest(clink);

		if (doc == null) {
			return null;
		}
		List<Map<String, String>> collectionLst = new ArrayList<>();
		Elements eleArr = doc.select(".channels-content-item");

		for (Element ele : eleArr) {
			try {
				Map<String, String> map = new HashMap<>();
				Element hele = ele.select(".yt-uix-tile-link").get(0);
				String link = hele.attr("href");
				String name = hele.text();
				Elements countEles = ele.select(".formatted-video-count-label b");
				if (countEles.size() == 0) {
					continue;
				}
				String count = countEles.get(0).text();

				map.put("name", name);
				map.put("link", link);
				map.put("count", count);

				collectionLst.add(map);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(ele);
				System.exit(0);
			}
		}
		System.out.println(new JSONArray(collectionLst).toString(3));
		int total = collectionLst.size();
		System.out.println(total);
		if (total == 0) {
			System.err.println(clink);
		} else {
			System.out.println(collectionLst.get(total - 1));
		}
		// System.out.println(new JSONArray(collectionLst).toString(3));

		Elements moreBtn = doc.select(".load-more-button");
		if (moreBtn.size() > 0) {
			String moreUrl = moreBtn.get(0).attr("data-uix-load-more-href");
			// System.out.println(moreUrl);

			int depth = 0;
			if (depth > -1) {
				// return;
			}
			CloseableHttpClient httpclient = HttpClients.createDefault();
			browse_ajax(collectionLst, httpclient, moreUrl, depth + 1);
			httpclient.close();
		}

		return collectionLst;
	}

	public static void browse_ajax(List<Map<String, String>> collectionLst, CloseableHttpClient httpclient,
			String moreUrl, int depth) throws FileNotFoundException, IOException {
		String url = "https://www.youtube.com" + moreUrl;
		System.out.println("depth: " + depth);
		System.out.println(url);
		HttpGet httpget = new HttpGet(url);

		// RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(1000).setSocketTimeout(3000).build();

		RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH)
				.setExpectContinueEnabled(true).setStaleConnectionCheckEnabled(true)
				.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
				.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();

		HttpGet httpGet = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).setSocketTimeout(5000)
				.setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();
		httpGet.setConfig(requestConfig);

		HttpResponse response = CaptureUtil.timeoutRequest(httpclient, httpget, 500, 2);
		if (response == null) {
			return;
		}
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream inputStream = entity.getContent();

			String sjson = IOUtils.toString(inputStream, "utf-8");
			// important, release httpclient connections
			inputStream.close();
			JSONObject json = new JSONObject(sjson);
			String content = json.getString("content_html");
			String more = json.getString("load_more_widget_html");
			Document doc = Jsoup.parse("<table>" + content + "</table>");

			if (doc == null) {
				return;
			}
			// System.out.println(doc.html());
			Elements eleArr = doc.select(".channels-content-item");

			for (Element ele : eleArr) {
				try {
					Map<String, String> map = new HashMap<>();
					Element hele = ele.select(".yt-uix-tile-link").get(0);
					String link = hele.attr("href");
					String name = hele.text();

					Elements countEles = ele.select(".formatted-video-count-label b");
					if (countEles.size() == 0) {
						continue;
					}
					String count = countEles.get(0).text();

					map.put("name", name);
					map.put("link", link);
					map.put("count", count);

					collectionLst.add(map);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(ele);
					System.exit(0);
				}
			}

			int total = collectionLst.size();
			System.out.println(total);
			System.out.println(collectionLst.get(total - 1));
			// System.out.println(new JSONArray(collectionLst).toString(3));

			Document moreDoc = Jsoup.parse(more);

			if (moreDoc == null) {
				return;
			}

			Elements moreBtn = moreDoc.select(".load-more-button");
			if (moreBtn.size() > 0) {
				String nextMoreUrl = moreBtn.get(0).attr("data-uix-load-more-href");
				// System.out.println(nextMoreUrl);
				if (depth < 4000) {
					browse_ajax(collectionLst, httpclient, nextMoreUrl, depth + 1);
				}
			}
		}

	}

}
