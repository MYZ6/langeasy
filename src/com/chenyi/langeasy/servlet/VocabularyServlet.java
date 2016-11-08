package com.chenyi.langeasy.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chenyi.langeasy.capture.CaptureUtil;

/**
 * Servlet implementation class VocabularyServlet
 */
@WebServlet("/api")
public class VocabularyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VocabularyServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("t");
		Connection conn = CaptureUtil.getConnection();
		try {
			if ("list".equals(type)) {
				JSONArray arr = null;
				arr = Vocabulary.listWord(conn);
				outJS(response, arr.toString());
			} else if ("lista".equals(type)) {
				JSONArray arr = Vocabulary.listAword(conn);
				outJS(response, arr.toString());
			} else if ("word".equals(type)) {
				String wordid = request.getParameter("id");
				JSONObject word = null;
				word = Vocabulary.word(conn, Integer.parseInt(wordid));
				outJS(response, word.toString());
			} else if ("m".equals(type)) {
				String sentenceid = request.getParameter("id");
				String filepath = "e:/langeasy/sentence_normalize/" + sentenceid + ".mp3";
				outMp3(request, response, filepath);
			} else if ("p".equals(type)) {
				String path = request.getParameter("path");
				String filepath = "e:/langeasy/pronunciation-normalize/" + path;
				outMp3(request, response, filepath);
			} else if ("pass".equals(type)) {
				String wordid = request.getParameter("wordid");
				Vocabulary.pass(conn, Integer.parseInt(wordid));

				JSONObject result = new JSONObject();
				result.put("msg", "success");
				outJS(response, result.toString());
			} else if ("dB".equals(type)) {
				JSONArray arr = Sentence.listSentenceByDB();
				outJS(response, arr.toString());
			} else if ("listModifiedSentence".equals(type)) {
				JSONArray arr = Sentence.listModifiedSentence();
				outJS(response, arr.toString());
			} else if ("translate".equals(type)) {
				String word = request.getParameter("word");
				JSONObject result = new JSONObject();
				result.put("chinese", Vocabulary.translate(word));
				outJS(response, result.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	protected void outJS(HttpServletResponse response, String value) {
		response.setContentType("application/json; charset=UTF-8");
		try {
			PrintWriter out = response.getWriter();
			out.print(value);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void outMp3(HttpServletRequest request, HttpServletResponse response, String filepath) throws IOException {
		InputStream fis = new FileInputStream(filepath);
		String mimeType = request.getServletContext().getMimeType(filepath);
		response.setContentType(mimeType != null ? mimeType : "application/octet-stream");

		File file = new File(filepath);
		response.setContentLength((int) file.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

		ServletOutputStream sos = response.getOutputStream();
		byte[] bufferData = new byte[1024];
		int read = 0;
		while ((read = fis.read(bufferData)) != -1) {
			sos.write(bufferData, 0, read);
		}
		sos.flush();
		sos.close();
		fis.close();
		sos.close();
	}
}
