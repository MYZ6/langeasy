package com.chenyi.langeasy.gonewiththewind;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chenyi.langeasy.sqlite.SqliteHelper;

/**
 * Servlet implementation class VocabularyServlet
 */
@WebServlet("/gwtw")
public class VocabularyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VocabularyServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String type = request.getParameter("t");
		Connection conn = SqliteHelper.getConnection("goneWithTheWind");
		try {
			if ("list".equals(type)) {
				String wtype = request.getParameter("wtype");
				Integer limit = 200;
				if ("3".equals(wtype)) {
					limit = 20000;
				}
				JSONArray arr = Vocabulary.listWord(conn, wtype, limit);
				Integer total = Vocabulary.queryTotal(conn, wtype);
				JSONObject result = new JSONObject();
				result.put("rows", arr);
				result.put("total", total);
				result.put("msg", "success");
				outJS(response, result.toString());
			} else if ("pass".equals(type)) {
				String wordids = request.getParameter("wordids");
				Vocabulary.pass(conn, wordids);

				JSONObject result = new JSONObject();
				result.put("msg", "success");
				outJS(response, result.toString());
			} else if ("unknown".equals(type)) {
				String wordids = request.getParameter("wordids");
				Vocabulary.unknown(conn, wordids);

				JSONObject result = new JSONObject();
				result.put("msg", "success");
				outJS(response, result.toString());
			} else if ("translate".equals(type)) {
				String word = request.getParameter("word");
				outHTML(response, Vocabulary.translate(word).toString());
			} else if ("sentence".equals(type)) {
				String word = request.getParameter("word");
				outJS(response, Sentence.list(conn, word).toString());
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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

	protected void outHTML(HttpServletResponse response, String value) {
		response.setContentType("text/html; charset=UTF-8");
		try {
			PrintWriter out = response.getWriter();
			out.print(value);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
