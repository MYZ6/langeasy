package com.chenyi.langeasy.capture.youtube;

import java.io.File;
import java.io.FileInputStream;
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

public class ParseUtil {
	private static String dirPath = "E:/langeasy/lucene/youtube/nasa/";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println("start time is : " + new Date());

		String cpath = dirPath + "playlists.html";
		// playlists(cpath);
		// cpath = dirPath + "playlists.json";
		// count(cpath);
		// int total = 1;
		// if (total > 0) {
		// return;
		// }
		String plink = "https://www.youtube.com/playlist?list=PL2aBZuCeDwlT56jTrxQ3FExn-dtchIwsZ";
		List<Map<String, String>> videoLst = playlist(plink);

		File sFile = new File(dirPath + "video-list.json");
		FileUtils.writeStringToFile(sFile, new JSONArray(videoLst).toString(3), StandardCharsets.UTF_8);
		System.out.println("end time is : " + new Date());
	}

	public static void playlists(String cpath) throws FileNotFoundException, IOException {
		List<Map<String, String>> collectionLst = new ArrayList<>();
		// File file = new File("e:/browse_ajax");
		// File file = new
		// File("e:/langeasy/lucene/podcast/yale-courses/browse_ajax_p");
		File file = new File(cpath);
		String content = IOUtils.toString(new FileInputStream(file), "utf-8");
		// String content = new JSONObject(sjson).getString("content_html");
		// System.out.println(content);
		int total = 1;
		if (total > 0) {
			// return;
		}
		Document doc = Jsoup.parse(content);

		if (doc == null) {
			return;
		}
		Elements eleArr = doc.select(".channels-content-item");

		for (Element ele : eleArr) {
			try {
				Map<String, String> map = new HashMap<>();
				Element hele = ele.select(".yt-uix-tile-link").get(0);
				String link = hele.attr("href");
				String name = hele.text();
				String count = ele.select(".formatted-video-count-label b").get(0).text();

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
	}

	public static void count(String cpath) throws FileNotFoundException, IOException {
		File sFile = new File(cpath);
		String content = IOUtils.toString(new FileInputStream(sFile), "utf-8");
		JSONArray courseArr = new JSONArray(content);
		int total = 0;
		int max = 0;
		for (int i = 0; i < courseArr.length(); i++) {
			JSONObject course = courseArr.getJSONObject(i);
			String count = course.getString("count");
			count = count.replaceAll(",", "");
			int icount = Integer.parseInt(count);
			total += icount;
			if (icount > max) {
				max = icount;
			}
		}
		System.out.println(total);
		System.out.println(max);
		if (total > -1) {
			// return;
		}
	}

	public static List<Map<String, String>> playlist(String plink) throws FileNotFoundException, IOException {
		// File file = new File("e:/browse_ajax");
		Document doc = CaptureUtil.timeoutRequest(plink);

		// File htmlFile = new File(dirPath + "playlist.html");
		// String sResult = IOUtils.toString(new FileInputStream(htmlFile), "utf-8");
		// Document doc = Jsoup.parse(sResult);
		if (doc == null) {
			return null;
		}
		// System.out.println(doc.html());
		Elements eleArr = doc.select(".pl-video");

		List<Map<String, String>> videoLst = new ArrayList<>();
		for (Element ele : eleArr) {
			try {
				Map<String, String> map = new HashMap<>();
				Element hele = ele.select(".pl-video-title-link").get(0);
				String link = hele.attr("href");
				String name = hele.text();

				Elements ownerEles = ele.select(".pl-video-owner a");
				if (ownerEles.size() == 0) {
					continue;
				}
				Element ownerEle = ownerEles.get(0);
				String olink = ownerEle.attr("href");
				String oname = ownerEle.text();

				String duration = ele.select(".pl-video-time").get(0).text();

				map.put("name", name);
				map.put("link", link);
				map.put("oname", oname);
				map.put("olink", olink);
				map.put("duration", duration);

				videoLst.add(map);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(ele);
				System.exit(0);
			}
		}
		int total = videoLst.size();
		System.out.println(total);
		System.out.println(videoLst.get(total - 1));
		// System.out.println(new JSONArray(videoLst).toString(3));

		Elements moreBtn = doc.select(".load-more-button");
		if (moreBtn.size() > 0) {
			String moreUrl = moreBtn.get(0).attr("data-uix-load-more-href");
			// System.out.println(moreUrl);

			int depth = 0;
			if (depth > -1) {
				// return;
			}
			CloseableHttpClient httpclient = HttpClients.createDefault();
			browse_ajax(videoLst, httpclient, moreUrl, depth + 1);
			httpclient.close();
		}

		return videoLst;
	}

	public static void browse_ajax(List<Map<String, String>> videoLst, CloseableHttpClient httpclient, String moreUrl,
			int depth) throws FileNotFoundException, IOException {
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
			Elements eleArr = doc.select(".pl-video");

			for (Element ele : eleArr) {
				try {
					Map<String, String> map = new HashMap<>();
					Element hele = ele.select(".pl-video-title-link").get(0);
					String link = hele.attr("href");
					String name = hele.text();

					Elements ownerEles = ele.select(".pl-video-owner a");
					if (ownerEles.size() == 0) {
						continue;
					}
					Element ownerEle = ownerEles.get(0);
					String olink = ownerEle.attr("href");
					String oname = ownerEle.text();

					String duration = ele.select(".pl-video-time").get(0).text();

					map.put("name", name);
					map.put("link", link);
					map.put("oname", oname);
					map.put("olink", olink);
					map.put("duration", duration);

					videoLst.add(map);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(ele);
					System.exit(0);
				}
			}

			int total = videoLst.size();
			System.out.println(total);
			System.out.println(videoLst.get(total - 1));
			// System.out.println(new JSONArray(videoLst).toString(3));

			Document moreDoc = Jsoup.parse(more);

			if (moreDoc == null) {
				return;
			}

			Elements moreBtn = moreDoc.select(".load-more-button");
			if (moreBtn.size() > 0) {
				String nextMoreUrl = moreBtn.get(0).attr("data-uix-load-more-href");
				// System.out.println(nextMoreUrl);
				if (depth < 300) {
					browse_ajax(videoLst, httpclient, nextMoreUrl, depth + 1);
				}
			}
		}

	}
}
