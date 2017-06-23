package com.chenyi.langeasy.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Vocabulary {

	static JSONArray listWord(Connection conn) throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT id, word, pron from vocabulary limit 30";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer wordid = rs.getInt("id");
			String word = rs.getString("word");
			String pron = rs.getString("pron");
			JSONObject wordMap = new JSONObject();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			wordMap.put("pron", pron);
			arr.put(wordMap);
		}
		rs.close();
		st.close();

		return arr;
	}

	static JSONArray listAword(Connection conn) throws JSONException, SQLException, IOException {
		String sql = "SELECT a.wordid, a.word, v.pass from vocabulary_audio a inner join vocabulary v on v.id = a.wordid "
				+ "WHERE COALESCE(v.pass, 0) != 1 group by a.wordid limit 20000";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer wordid = rs.getInt("wordid");
			String word = rs.getString("word");
			JSONObject wordMap = new JSONObject();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			arr.put(wordMap);
		}
		rs.close();
		st.close();

		return arr;
	}

	static JSONArray listTest2(Connection conn) throws JSONException, SQLException, IOException {
		String sql = "SELECT a2.wordid, a2.word, COUNT( r.sid) AS favorite FROM vocabulary_audio a2 " +
				"LEFT JOIN queue_record r ON r.sid = a2.sentenceid WHERE a2.wordid IN " +
				"( SELECT a.wordid FROM vocabulary_audio a WHERE a.sentenceid IN " +
				"(314225, 484001, 12039, 396117, 602723, 414225, 622867, 635438, 10433, 420245, 199357, 541384, 402303, 596505, 495590, 174373, 479838, 458857, 3551, 342131, 253107, 640897, 574658, 207217, 351089, 346479, 603525, 569427, 368033, 400533, 18345, 501065, 617974, 461077, 194757, 370555, 586437, 235033, 406009, 591260, 445684, 443268, 286183, 497408, 424553, 640855, 408099, 256357, 367529, 371169, 643334, 235417, 457809, 441658, 235051, 627874, 471704, 200395, 294771, 591952, 628686, 286017, 215861, 552494, 634908, 627674, 408277, 331327, 547029, 542498, 229885, 571751, 637074, 351439, 359069, 197099, 286547, 504312, 419539, 279169, 320233, 642804, 423283, 596305, 180471, 160003, 416961, 416589, 404723, 619370, 430201, 278015, 504416, 631227, 628738, 380797, 600330, 587603, 586171, 398419, 12155, 322069, 154347, 398445, 331337, 576901, 278613, 205915, 267573, 641805, 202421, 399013, 171333, 628338, 153881, 441288, 504316, 641315, 619992, 637906, 330947, 154079, 417453)) " +
				"GROUP BY a2.wordid ORDER BY a2.word, a2.sentenceid";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer wordid = rs.getInt("wordid");
			String word = rs.getString("word");
			JSONObject wordMap = new JSONObject();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);

			Integer favorite = rs.getInt("favorite");
			if (favorite != 0) {
				wordMap.put("favorite", true);
			}
			arr.put(wordMap);
		}
		rs.close();
		st.close();

		return arr;
	}

	static JSONArray listTest(Connection conn) throws JSONException, SQLException, IOException {
		String sql = "SELECT a2.wordid, a2.word, COUNT( r.sid) AS favorite FROM vocabulary_audio a2 " +
				"INNER JOIN vocabulary v on v.id = a2.wordid and v.pass IS NULL " +
				"LEFT JOIN queue_record r ON r.sid = a2.sentenceid WHERE a2.wordid IN " +
				"( 414, 448, 500, 808, 1048, 1320, 520, 580, 800, 823, 836, 1030, 1040, 1198, 1206, 1237, 1293, 1312, 402, 527, 654, 672, 772, 860, 1050, 1062, 1119, 1122, 1145, 1200, 1225, 897, 377, 406, 412, 434, 461, 474, 530, 845, 927, 928, 989, 1106, 1137, 1169, 1184, 1261, 1265, 507, 370, 464, 510, 514, 724, 792, 903, 1021, 1202, 1238, 1274, 1289, 393, 482, 586, 613, 643, 764, 796, 801, 832, 924, 984, 1002, 1088, 1341, 407, 441, 478, 566, 779, 980, 998, 1243, 1297, 1316, 1331, 417, 450, 569, 637, 652, 878, 979, 1008, 1186, 656, 540, 676, 818, 829, 907, 1178, 1212, 1250, 1318, 421, 446, 544, 721, 781, 786, 844, 964, 1055, 1189, 454, 30, 44, 58, 85, 130, 203, 230, 232, 243, 269, 312, 343, 630, 632, 641, 688, 694, 709, 747, 768, 931, 977, 1114, 1148, 1167, 1201, 1219, 1248, 24, 27, 28, 81, 117, 153, 182, 267, 319, 326, 428, 496, 501, 597, 605, 662, 696, 708, 737, 797, 945, 970, 985, 988, 1014, 1161, 1185, 1254, 1337, 667, 8, 9, 25, 26, 33, 50, 52, 167, 336, 404, 422, 624, 858, 873, 888, 1061, 1071, 1159, 1183, 1235, 51, 170, 216, 247, 614, 617, 679, 848, 1018, 1197, 1260, 1308, 97, 181, 307, 363, 389, 476, 565, 575, 689, 809, 821, 933, 958, 1187, 1299, 1329, 82, 259, 287, 323, 342, 368, 384, 420, 577, 578, 600, 609, 626, 642, 843, 926, 1060, 1105, 374, 386, 644, 760, 1095, 1150, 795, 45, 49, 288, 542, 671, 1010, 1011, 1027, 89, 186, 211, 383, 504, 619, 1065, 1208, 1311, 65, 1054, 131, 401, 546, 562, 693, 1232, 1301, 291, 366, 678, 1334, 17, 22, 23, 86, 703, 100, 242, 280, 394, 659, 730, 1081, 143, 253, 515, 732, 1319, 159, 489, 558, 719, 814, 961, 328, 785, 1333, 67, 14, 56, 163, 219, 273, 480, 625, 771, 929, 987, 115, 759, 205, 568, 20, 991, 1328, 116, 184, 395, 599, 31, 126, 169, 19, 102, 122, 72, 133, 255) " +
				"GROUP BY a2.wordid ORDER BY a2.word, a2.sentenceid";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer wordid = rs.getInt("wordid");
			String word = rs.getString("word");
			JSONObject wordMap = new JSONObject();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);

			Integer favorite = rs.getInt("favorite");
			if (favorite != 0) {
				wordMap.put("favorite", true);
			}
			arr.put(wordMap);
		}
		rs.close();
		st.close();

		return arr;
	}

	static JSONArray listSentence(Connection conn) throws JSONException, SQLException, IOException {
		String sql = "SELECT a.word, a.sentenceid, a.sentence, c.play_count, a.chinese, "
				+ "b.bookid, b.bookname, b.booktype, c.courseid, c.name AS coursename  FROM vocabulary_audio a " +
				"INNER JOIN vocabulary v on v.id = a.wordid and v.pass IS NULL "
				+ "INNER JOIN sentence s ON s.id = a.sentenceid "
				+ "INNER JOIN course c ON c.courseid = s.courseid "
				+ "INNER JOIN book b ON b.bookid = c.bookid " +
				"LEFT JOIN (select sentenceid,sum(play_count) as play_count from play_record_count c1 group by sentenceid) c ON c.sentenceid = a.sentenceid "
				+ "WHERE a.sentenceid IN " +
				"(217317, 413259, 585683, 213233, 491449, 596495, 523965, 254389, 275867, 151985, 386795, 585711, 402067, 15547, 346415, 610496, 55051, 13459, 483447, 246735, 257211, 503262, 8417, 158741, 357565, 475275, 745, 595713, 546109, 458701, 611072, 380239, 444118, 472138, 426521, 361609, 585365, 153547, 220407, 635724, 9227, 157263, 12307, 597494, 341039, 153155, 8385, 213923, 363647, 495668, 559839, 30617, 37339, 254569, 256425, 300835, 435253, 223489, 366455, 380597, 533885, 154295, 160033, 260719, 330855, 422981, 442868, 444480, 201273, 387457, 533717, 407947, 500019, 534931, 557477, 152253, 401595, 552440, 598148, 21201, 58613, 228295, 282493, 353379, 364671, 301241, 357573, 459555, 77039, 444506, 521378, 587105, 8389, 120733, 156269, 183381, 463769, 528038, 567438, 580976, 244401, 444072, 584148, 585089, 180539, 198173, 341795, 392225, 508989, 548328, 15025, 181029, 239559, 248905, 265875, 290689, 519558, 134799, 139867, 158193, 174097, 284251, 349767, 499913, 505204, 517403, 532619, 599352, 628696, 64221, 139687, 195817, 259829, 303635, 354233, 383815, 397671, 418649, 28087, 43959, 105353, 158853, 166911, 198211, 224947, 246215, 246417, 249405, 273465, 380641, 481220, 483705, 5753, 20841, 109719, 184351, 233891, 246089, 343297, 383445, 417031, 480770, 595853, 17427, 60203, 73935, 147801, 217753, 232571, 304921, 315853, 437854, 438134, 515835, 527966, 644258, 9011, 9495, 126113, 166135, 185123, 231511, 315199, 392041, 392275, 393595, 443960, 472106, 533345, 571181, 595143, 27767, 33755, 117313, 286209, 380611, 381143, 392153, 408943, 416873, 423195, 507591, 631657, 1873, 12587, 15957, 18975, 27125, 42669, 48317, 116831, 229401, 230399, 250005, 344913, 604157, 605293, 63125, 86307, 89191, 194649, 215231, 245201, 367573, 380367, 444530, 522200, 522290, 574884, 638717, 20421, 174121, 180099, 185055, 217613, 234115, 292529, 308479, 308985, 341181, 342033, 363699, 393325, 406109, 444838, 498054, 574626, 597910, 611530, 13703, 15425, 18447, 83715, 195431, 361737, 371551, 528618, 578025, 578357, 581702, 9217, 51559, 74145, 80453, 160561, 185109, 185139, 279983, 304431, 368453, 370167, 437922, 438280, 477349, 487154, 585143, 596253, 604067, 11967, 12683, 101779, 154537, 156315, 194585, 196551, 224967, 246709, 294809, 300793, 308497, 410829, 418137, 444852, 456299, 534691, 540488, 577737, 613975, 642762, 4861, 6571, 9329, 25493, 119497, 154001, 183111, 199353, 221719, 232531, 285457, 351387, 368259, 381983, 396559, 437926, 529598, 5095, 5339, 6665, 8395, 8807, 12043, 71143, 95201, 138847, 164087, 198963, 211517, 282769, 287301, 296199, 340425, 340699, 340861, 377709, 392015, 397593, 443772, 499309, 507723, 508171, 517105, 579737, 583918, 594769, 4631, 6375, 8381, 130901, 156365, 161685, 238995, 308305, 351133, 359045, 405931, 429999, 534967, 541540, 544931, 601597, 603389, 639247, 6297, 12461, 12979, 82341, 172831, 217055, 222335, 345465, 386993, 396265, 434825, 489238, 493223, 573006, 599988, 618484, 631567, 644436, 2069, 14337, 14443, 89621, 178591, 185069, 191381, 199243, 199503, 204107, 230019, 246739, 250593, 294347, 354327, 387305, 464127, 475041, 479360, 515941, 529418, 585349, 4429, 9199, 9425, 10145, 16229, 17533, 55603, 57103, 360305, 380895, 404625, 534839, 8965, 13117, 152747, 180131, 208601, " +
				"277929, 399541, 509511, 513754, 528194, 558161, 604741, 637706, 642862, 4411, 4669, 7097, 153709, 174069, 249349, 276181, 304211, 305969, 316127, 368431, 439486, 516101, 583704, 611028, 4649, 17721, 20807, 156183, 157025, 174809, 180051, 201301, 387281, 435795, 590264, 627092, 5141, 14917, 20025, 175471, 179749, 205939, 223927, 227857, 278615, 301933, 357191, 437061, 504808, 551438, 565230, 586657, 620378, 3037, 20459, 158743, 212001, 227287, 293981, 393475, 458095, 581014, 581762, 600740, 620290, 631609, 4731, 10069, 157927, 219555, 367549, 375291, 401803, 432165, 535963, 557855, 595937, 622867, 641363, 8019, 19163, 217013, 342371, 375487, 394705, 503994, 540612, 632189, 636374, 641805, 10433, 305979, 342709, 367671, 410649, 457379, 516673, 618498, 189161, 301005, 322069,1757, 1821, 2109, 2117, 3281, 3509, 3551, 3553, 3693, 3811, 3939, 4143, 4175, 6377, 6391, 6479, 6621, 6743, 6947, 7275, 7749, 7937, 8053, 9197, 9239, 9397, 9457, 9541, 9925, 10689, 11463, 11493, 11541, 11665, 11965, 12039, 12089, 12411, 12685, 12883, 15593, 15621, 16755, 17067, 17463, 17567, 17905, 18345, 18651, 18761, 18901, 19943, 20701, 152489, 152581, 153413, 153687, 153787, 153881, 155303, 156197, 156577, 156627, 158051, 158401, 160003, 165621, 169795, 174373, 176029, 176105, 178577, 178603, 178657, 179121, 179181, 179211, 179295, 180077, 180471, 180731, 181259, 182529, 192005, 193901, 194115, 194517, 194733, 197077, 198257, 198785, 200059, 200395, 201465, 201827, 202421, 204879, 204945, 206897, 207257, 207669, 208305, 208577, 211077, 212237, 213775, 215239, 215861, 217537, 222137, 227911, 229885, 232287, 235033, 235417, 235449, 235455, 237163, 239943, 240787, 241079, 241293, 244449, 246151, 246287, 247771, 250039, 252011, 252023, 252053, 254281, 256315, 256357, 257089, 258921, 260073, 263949, 265877, 274687, 278015, 278613, 282571, 284739, 286017, 286105, 286547, 287399, 293405, 294127, 295199, 295207, 310223, 315103, 315445, 318873, 320233, 331337, 341433, 346315, 346461, 346479, 351089, 354055, 357607, 358831, 362017, 368033, 369755, 374221, 375155, 375515, 377305, 380797, 381863, 385845, 387119, 387139, 387485, 389301, 393383, 396117, 398293, 398419, 399853, 401623, 403057, 406697, 416961, 418199, 420245, 423791, 424553, 424705, 427291, 430167, 430201, 434629, 436591, 437441, 440018, 441658, 443268, 443524, 445684, 454749, 456441, 457065, 457809, 457819, 458207, 458857, 461077, 471152, 471704, 475123, 479370, 479838, 484851, 495590, 495778, 495844, 496754, 500975, 501065, 501645, 504312, 504316, 504416, 504474, 507533, 507655, 511472, 513980, 515929, 517329, 520488, 523957, 523971, 525413, 525661, 541294, 541384, 544741, 546667, 546741, 550866, 552494, 565674, 566364, 567330, 569195, 569427, 569907, 571001, 573112, 573336, 574104, 574170, 575036, 576901, 577439, 578953, 579755, 581642, 582522, 586437, 586673, 586895, 591260, 591564, 591952, 594561, 594563, 594857, 595285, 595459, 600330, 601567, 602723, 604579, 609998, 612558, 615373, 616603, 619968, 619992, 627034, 627674, 627874, 634908, 635236, 635438, 635488, 637074, 640849, 640897, 641741, 642816, 645078) " +
				"ORDER BY a.word, a.sentenceid";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer sentenceid = rs.getInt("sentenceid");
			Integer playCount = rs.getInt("play_count");
			String word = rs.getString("word");
			String sentence = rs.getString("sentence");
			String chinese = rs.getString("chinese");
			String bookid = rs.getString("bookid");
			String bookname = rs.getString("bookname");
			String booktype = rs.getString("booktype");
			String courseid = rs.getString("courseid");
			String coursename = rs.getString("coursename");

			JSONObject map = new JSONObject();
			map.put("word", word);
			map.put("sentenceid", sentenceid);
			map.put("playCount", playCount);
			map.put("sentence", sentence);
			map.put("chinese", chinese);
			map.put("bookid", bookid);
			map.put("bookname", bookname);
			map.put("booktype", booktype);
			map.put("courseid", courseid);
			map.put("coursename", coursename);
			arr.put(map);
		}
		rs.close();
		st.close();

		return arr;
	}

	static JSONArray listByBook(Connection conn, String bookid) throws SQLException {
		String sql = "SELECT a.wordid, a.word, v.pass from vocabulary_audio a inner join vocabulary v on v.id = a.wordid "
				+ "INNER JOIN langeasy.sentence s ON s.id = a.sentenceid "
				+ "INNER JOIN langeasy.course c ON c.courseid = s.courseid "
				+ "INNER JOIN langeasy.book b ON b.bookid = c.bookid "
				+ "WHERE b.bookid = ? and COALESCE(v.pass, 0) != 1 group by a.wordid limit 20000";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setString(1, bookid);
		ResultSet rs = st.executeQuery();
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer wordid = rs.getInt("wordid");
			String word = rs.getString("word");
			JSONObject wordMap = new JSONObject();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			arr.put(wordMap);
		}
		rs.close();
		st.close();

		return arr;
	}

	public static JSONObject book(Connection conn, String bookid) throws SQLException {
		String sql = "SELECT bookname, booktype from book where bookid = ?";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setString(1, bookid);
		ResultSet rs = st.executeQuery();
		JSONObject bookMap = new JSONObject();
		while (rs.next()) {
			String bookname = rs.getString("bookname");
			String booktype = rs.getString("booktype");
			bookMap.put("bookname", bookname);
			bookMap.put("booktype", booktype);
		}
		rs.close();
		st.close();

		bookMap.put("bookLst", listByBook(conn, bookid));
		return bookMap;
	}

	public static JSONObject word(Connection conn, Integer wordid) throws SQLException {
		String sql = "SELECT word, pron, mp3path, oggpath from vocabulary where id = ?";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, wordid);
		ResultSet rs = st.executeQuery();
		JSONObject wordMap = new JSONObject();
		String pronPrefix = "https://www.oxforddictionaries.com/media/english/";
		while (rs.next()) {
			String word = rs.getString("word");
			String pron = rs.getString("pron");
			String mp3path = rs.getString("mp3path");
			String oggpath = rs.getString("oggpath");
			oggpath = oggpath.substring(pronPrefix.length());
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			wordMap.put("pron", pron);
			wordMap.put("mp3path", mp3path);
			wordMap.put("oggpath", oggpath);
		}
		rs.close();
		st.close();

		wordMap.put("meaning", listMeaning(conn, wordid));
		wordMap.put("aexample", listAudioExample(conn, wordid));
		return wordMap;
	}

	public static JSONArray listMeaning(Connection conn, Integer wordid) throws SQLException {
		String sql = "SELECT id, type, meaning from meaning where wordid = ? order by weight";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, wordid);
		ResultSet rs = st.executeQuery();
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer meaningid = rs.getInt("id");
			String type = rs.getString("type");
			String meaning = rs.getString("meaning");

			JSONObject map = new JSONObject();
			map.put("meaningid", meaningid);
			map.put("type", type);
			map.put("meaning", meaning);
			arr.put(map);
		}
		rs.close();
		st.close();

		for (int i = 0; i < arr.length(); i++) {
			JSONObject item = arr.getJSONObject(i);
			item.put("example", listExample(conn, item.getInt("meaningid")));
		}

		return arr;
	}

	public static JSONArray listExample(Connection conn, Integer meaningid) throws SQLException {
		String sql = "SELECT id, sentence from example_sentence where meaningid = ? order by weight";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, meaningid);
		ResultSet rs = st.executeQuery();
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer exampleid = rs.getInt("id");
			String sentence = rs.getString("sentence");

			JSONObject map = new JSONObject();
			map.put("exampleid", exampleid);
			map.put("sentence", sentence);
			arr.put(map);
		}
		rs.close();
		st.close();

		return arr;
	}

	public static JSONArray listAudioExample(Connection conn, Integer wordid) throws SQLException {
		String sql = "SELECT a.sentenceid, a.sentence, c.play_count, r.sid as favorite, a.chinese, "
				+ "b.bookid, b.bookname, b.booktype, c.courseid, c.name AS coursename " + "from vocabulary_audio a "
				+ "LEFT JOIN (select sentenceid,sum(play_count) as play_count from play_record_count c1 group by sentenceid) c ON c.sentenceid = a.sentenceid "
				+ "LEFT JOIN queue_record r ON r.sid = a.sentenceid "
				+ "INNER JOIN sentence s ON s.id = a.sentenceid "
				+ "INNER JOIN course c ON c.courseid = s.courseid "
				+ "INNER JOIN book b ON b.bookid = c.bookid " + " where a.wordid = ?";
		System.out.println(sql);
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, wordid);
		ResultSet rs = st.executeQuery();
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer sentenceid = rs.getInt("sentenceid");
			Integer playCount = rs.getInt("play_count");
			String sentence = rs.getString("sentence");
			String chinese = rs.getString("chinese");
			String bookid = rs.getString("bookid");
			String bookname = rs.getString("bookname");
			String booktype = rs.getString("booktype");
			String courseid = rs.getString("courseid");
			String coursename = rs.getString("coursename");

			JSONObject map = new JSONObject();
			map.put("sentenceid", sentenceid);
			map.put("playCount", playCount);
			map.put("sentence", sentence);
			map.put("chinese", chinese);
			map.put("bookid", bookid);
			map.put("bookname", bookname);
			map.put("booktype", booktype);
			map.put("courseid", courseid);
			map.put("coursename", coursename);

			Integer favorite = rs.getInt("favorite");
			if (favorite != 0) {
				map.put("favorite", true);
			}

			arr.put(map);
		}
		rs.close();
		st.close();

		return arr;
	}

	public static int favorite(Connection conn, int sentenceId) throws SQLException {
		String usql = "INSERT INTO queue_record(sid) VALUES (?)";
		PreparedStatement updatePs = conn.prepareStatement(usql);

		updatePs.setInt(1, sentenceId);
		int result = updatePs.executeUpdate();
		updatePs.close();

		return result;
	}

	public static int pass(Connection conn, int wordid) throws SQLException {
		String usql = "update vocabulary set pass = 1, mtime = ? where id = ?";
		PreparedStatement updatePs = conn.prepareStatement(usql);

		updatePs.setTimestamp(1, new Timestamp(new Date().getTime()));
		updatePs.setInt(2, wordid);
		updatePs.addBatch();
		updatePs.executeBatch();
		updatePs.clearBatch();
		updatePs.close();

		return 0;
	}

	public static JSONObject translate(String word) throws SQLException, ClientProtocolException, IOException {
		// word = URLEncoder.encode(word, "UTF-8");
		String url = "http://fanyi.baidu.com/v2transapi?from=en&query=" + word
				+ "&simple_means_flag=3&to=zh&transtype=realtime";
		System.out.println(url);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:45.0) Gecko/20100101 Firefox/45.0");

		CloseableHttpResponse response = httpclient.execute(httpget);
		// Get hold of the response entity
		HttpEntity entity = response.getEntity();

		byte[] result = null;
		// If the response does not enclose an entity, there is no need
		// to bother about connection release
		if (entity != null) {
			InputStream instream = entity.getContent();
			result = IOUtils.toByteArray(instream);
			instream.close();
		}
		response.close();

		String sResult = new String(result, "utf-8");
		// sResult = StringEscapeUtils.unescapeJava(sResult);
		// System.out.println(sResult);
		JSONObject json = null;
		try {
			json = new JSONObject(sResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(json);
		if (json.get("dict_result") instanceof JSONArray) {
			return new JSONObject();
		}
		JSONObject dict = json.getJSONObject("dict_result");
		JSONObject rjson = new JSONObject();
		rjson.put("edict", dict.get("edict"));
		rjson.put("simple_means", dict.get("simple_means"));

		return rjson;
	}
}
