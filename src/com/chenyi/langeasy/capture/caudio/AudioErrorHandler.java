package com.chenyi.langeasy.capture.caudio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AudioErrorHandler {

	public static void main(String[] args) throws IOException {
		System.out.println("start time is : " + new Date());
		test();
		// testCourse();
	}

	public static void test() throws IOException {
		File dir = new File("E:/langeasy/sentence");
		int count = 0;
		List<String> sentenceLst = new ArrayList<String>();
		for (File file : dir.listFiles()) {
			count++;
			// if (file.getName().indexOf("412125") > -1) {
			// System.out.println(file.getName() + "\t" + file.length());
			// }
			if (count % 500 != 0) {
				// continue;
			}
			long length = file.length();// in bytes
			if (length / 1000 < 20) {// less than 20kb
				String filename = file.getName();
				System.out.println(filename + "\t" + length);
				sentenceLst.add(filename.substring(0, filename.length() - 4));
			}
		}
		System.out.println(sentenceLst);
		System.out.println(sentenceLst.size());

	}

	public static void testCourse() throws IOException {
		int count = 0;
		String[] fileArr = new String[] { "ListenData/538418611/88595807.mp3", "ListenData/538418611/1789362336.mp3",
				"ListenData/269296427/1684575709.mp3", "ListenData/1404568722/1415012900.mp3",
				"ListenData/977915444/2054856991.mp3", "ListenData/538418611/216561888.mp3",
				"ListenData/100279329/1230981771.mp3", "ListenData/538418611/198037860.mp3",
				"ListenData/100279329/1423463868.mp3", "ListenData/351571393/1388937632.mp3",
				"ListenData/1472489706/901528436.mp3", "ListenData/349559752/1864016057.mp3",
				"ListenData/349559752/1919249301.mp3", "ListenData/1239256970/2012163489.mp3",
				"ListenData/538418611/1235380786.mp3", "ListenData/1694965458/1154810428.mp3",
				"ListenData/349559752/2036962820.mp3", "ListenData/1694965458/376064581.mp3",
				"ListenData/279257204/1423498389.mp3", "ListenData/538418611/1600531794.mp3",
				"ListenData/1983179718/680991519.mp3", "ListenData/538418611/608250557.mp3",
				"ListenData/134132630/592709876.mp3", "ListenData/915900259/1952231911.mp3",
				"ListenData/270963162/1379157890.mp3", "ListenData/1694965458/1630553259.mp3" };
		String prefix = "E:/langeasy/";
		for (String filepath : fileArr) {
			count++;
			File file = new File(prefix + filepath);
			long length = file.length();
			String filename = file.getName();
			// if (length / 1000 < 100) {
			System.out.println(filename + "\t" + length / 1000);
			System.out.println("\"" + filepath + "\",");
			// }
		}

	}
}
