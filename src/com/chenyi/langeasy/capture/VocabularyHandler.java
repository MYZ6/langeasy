package com.chenyi.langeasy.capture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class VocabularyHandler {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File courseFile = new File("E:\\vocabulary.txt");
		String sResult = IOUtils.toString(new FileInputStream(courseFile), "utf-8");
		// System.out.println(sResult);

		Pattern pattern = Pattern.compile("[a-zA-Z\\-]+\\b");
		Matcher matcher = pattern.matcher(sResult);
		int count = 0;
		while (matcher.find()) {
			String word = matcher.group(0);
			if ("n".equals(word)) {
				continue;
			}
			if ("v".equals(word)) {
				continue;
			}
			if ("vi".equals(word)) {
				continue;
			}
			if ("vt".equals(word)) {
				continue;
			}
			if ("adj".equals(word)) {
				continue;
			}
			if ("adv".equals(word)) {
				continue;
			}
			if ("pl".equals(word)) {// plural
				continue;
			}
			if ("List".equals(word)) {// plural
				continue;
			}
			System.out.println(word + "\t" + count);
			count++;
		}
	}
}
