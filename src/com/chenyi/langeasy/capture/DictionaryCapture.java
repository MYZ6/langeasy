package com.chenyi.langeasy.capture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DictionaryCapture {
	public static void main(String[] args) throws IOException, JSONException, SQLException {
		String word = "abaft";
		String url = "https://www.oxforddictionaries.com/definition/english/" + word;
		// Document doc = Jsoup.connect(url).get();
		// System.out.println(doc.html());

		// File courseFile = new File("E:\\dictionary.html");
		// String sResult = IOUtils.toString(new FileInputStream(courseFile), "utf-8");
		// Document doc = Jsoup.parse(sResult);

		Connection conn = CaptureUtil.getConnection();
		listWord(conn);
		CaptureUtil.closeConnection(conn);
	}

	private static List<Map<String, String>> listWord(Connection conn)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT id, word from vocabulary where pron is null";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<Map<String, Object>> wordLst = new ArrayList<>();
		while (rs.next()) {
			Integer wordid = rs.getInt("id");
			String word = rs.getString("word");
			Map<String, Object> wordMap = new HashMap<>();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			wordLst.add(wordMap);
		}
		rs.close();
		st.close();

		for (Map<String, Object> map : wordLst) {
			Integer wordid = (Integer) map.get("wordid");
			String word = (String) map.get("word");
			handleWord(conn, wordid, word);
		}

		return null;
	}

	private static void handleWord(Connection conn, Integer wordid, String word)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String url = "https://www.oxforddictionaries.com/definition/english/" + word;
		Document doc = CaptureUtil.timeoutRequest(url);

		if (doc == null) {// not exist, like "approbatory", which is only one of "approbation"'s Derivatives
			updateWordMeaning(conn, wordid, null, null, "Derivatives");
			return;
		}
		Element firstWordItem = doc.select(".entryPageContent").get(0);// only parse the first word item

		Elements titleArr = doc.select(".pageTitle");
		System.out.println(titleArr.size());
		String title = titleArr.get(0).text();
		String titleHtml = titleArr.get(0).html();
		System.out.println(title);
		System.out.println(titleHtml);

		Elements iconAudio = doc.select(".icon-audio");
		System.out.println(iconAudio.size());
		String dataSrcMp3 = iconAudio.get(0).attr("data-src-mp3");
		String dataSrcOgg = iconAudio.get(0).attr("data-src-ogg");
		System.out.println(dataSrcMp3);
		System.out.println(dataSrcOgg);

		Elements headpron = doc.select(".headpron");
		System.out.println(headpron.size());
		String pron = headpron.get(0).text();
		int start = pron.indexOf("/");
		int end = pron.indexOf("/", start + 1);
		String finalPron = pron.substring(start, end + 1);
		System.out.println(start);
		System.out.println(end);
		System.out.println(finalPron);

		// int wordId = getWordId(conn, word);
		updateWordMeaning(conn, wordid, dataSrcMp3, dataSrcOgg, finalPron);

		Elements senseGroup = firstWordItem.select(".senseGroup");
		System.out.println(senseGroup.size());
		int weight = 0;
		for (Element sense : senseGroup) {
			String wordType = sense.select(".partOfSpeech").get(0).text();
			System.out.println(wordType);
			String definition = sense.select(".definition").get(0).text();
			System.out.println(definition);

			List<String> exampleLst = new ArrayList<>();
			Elements $example = sense.select(".exampleGroup");
			if ($example.size() > 0) {
				String example = $example.get(0).text();
				exampleLst.add(example);
				System.out.println(example);
			}
			int meaningId = addMeaning(conn, wordid, wordType, definition, weight);

			Elements moreSentenceLst = sense.select(".sentence");
			for (Element sentence : moreSentenceLst) {
				String stext = sentence.text();
				exampleLst.add(stext);
				System.out.println(stext);
			}
			addExample(conn, meaningId, exampleLst);
			weight++;// first parse then first show next time
		}
	}

	private static void updateWordMeaning(Connection conn, int wordId, String mp3path, String oggpath, String pron)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String usql = "update vocabulary set mp3path = ?, oggpath = ?, pron = ? where id = ?";
		PreparedStatement insPs = conn.prepareStatement(usql);
		insPs.setString(1, mp3path);
		insPs.setString(2, oggpath);
		insPs.setString(3, pron);
		insPs.setInt(4, wordId);
		insPs.addBatch();

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();
	}

	private static int getWordId(Connection conn, String word)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT id FROM vocabulary where word = '" + word + "'";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		Integer wordId = null;
		while (rs.next()) {
			wordId = rs.getInt("id");
		}
		rs.close();
		st.close();

		return wordId;
	}

	private static Integer addMeaning(Connection conn, Integer wordid, String type, String meaning, int weight)
			throws JSONException, SQLException {
		String insertSql = "INSERT INTO meaning (wordid, type, meaning, weight) VALUES (?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

		insPs.setInt(1, wordid);
		insPs.setString(2, type);
		insPs.setString(3, meaning);
		insPs.setInt(4, weight);
		insPs.addBatch();

		Integer id = null;
		try {
			insPs.executeBatch();
			ResultSet generatedKeys = insPs.getGeneratedKeys();
			if (generatedKeys.next()) {
				id = generatedKeys.getInt(1);
			}
		} catch (BatchUpdateException exception) {
			exception.printStackTrace();
			String message = exception.getMessage();
			System.out.println(message);
			if (message.indexOf("meaning_unique") > -1) {
				System.out.println(meaning + " is alreay exist.");
			} else {
				throw exception;
			}
		}
		insPs.clearBatch();
		insPs.close();

		return id;
	}

	/**
	 * add example sentence
	 * 
	 * @throws SQLException
	 */

	private static int addExample(Connection conn, int meaningId, List<String> exampleLst) throws SQLException {
		String insertSql = "INSERT INTO example_sentence (meaningid, sentence, weight) VALUES(?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (int i = 0; i < exampleLst.size(); i++) {
			String example = exampleLst.get(i);
			insPs.setInt(1, meaningId);
			insPs.setString(2, example);
			insPs.setInt(3, i);
			insPs.addBatch();
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}
}
