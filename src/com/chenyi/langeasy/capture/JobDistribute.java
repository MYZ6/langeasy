package com.chenyi.langeasy.capture;

import java.sql.Connection;
import java.sql.Statement;

public class JobDistribute {

	public static void main(String[] args) throws Exception {
		Connection conn = CaptureUtil.getConnection();

		for (int i = 4; i < 45; i++) {
			combineAll(conn, i);
		}

		CaptureUtil.closeConnection(conn);
	}

	private static void createSubSentece(Connection conn, int jobIndex) throws Exception {
		String sql = "create table sentence" + jobIndex + " like sentence";
		Statement st = conn.createStatement();
		int rs = st.executeUpdate(sql);
		System.out.println(rs);
	}

	private static void createJob(Connection conn, int jobIndex) throws Exception {
		String sql = "create table job" + jobIndex + " like job2";
		Statement st = conn.createStatement();
		int rs = st.executeUpdate(sql);
		System.out.println(rs);
	}

	private static void loadData(Connection conn, int jobIndex) throws Exception {
		int startSeq = 400 + (jobIndex - 4) * 50;
		String sql = "INSERT INTO job" + jobIndex
				+ " SELECT seq, courseid, coursename, bookname, booktype FROM job_all j " + "WHERE j.seq > " + startSeq
				+ " AND j.seq <= " + (startSeq + 50);
		System.out.println(sql);
		if (jobIndex > 0) {
			// return;
		}
		Statement st = conn.createStatement();
		int rs = st.executeUpdate(sql);
		System.out.println(rs);
	}

	private static void combineAll(Connection conn, int jobIndex) throws Exception {
		String sql = "INSERT INTO sentence ( courseid, type, dataindex, text, decodestarttime, endtime ) "
				+ "SELECT courseid, type, dataindex, text, decodestarttime, endtime FROM sentence" + jobIndex;
		System.out.println(sql);
		if (jobIndex > 0) {
			// return;
		}
		Statement st = conn.createStatement();
		int rs = st.executeUpdate(sql);
		System.out.println(rs);
	}
}
