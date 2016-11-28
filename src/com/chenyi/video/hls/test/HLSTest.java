package com.chenyi.video.hls.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

public class HLSTest {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String dpath = "e:/webserver/nginx/test/screen/";
		String fpath = dpath + "screen.m3u8";
		File file = new File(fpath);
		long mtime = file.lastModified();
		String sdate = DateFormatUtils.format(new Date(mtime), "yyyy-MM-dd HH:mm:ss.SSS");
		System.out.println(sdate);

		String sResult = IOUtils.toString(new FileInputStream(file), "utf-8");
		System.out.println(sResult);
		int index1 = sResult.indexOf("EXTINF");
		System.out.println(index1);
		int index2 = sResult.lastIndexOf("EXTINF");
		System.out.println(index2);
		int index3 = StringUtils.lastOrdinalIndexOf(sResult, "EXTINF", 1);

		String newResult = sResult.substring(0, index1) + sResult.substring(index3);
		System.out.println(newResult);

		int a = sResult.lastIndexOf("screen");
		int b = sResult.lastIndexOf(".ts");
		String lastTs = sResult.substring(a + 6, b);
		int ilts = Integer.parseInt(lastTs);
		System.out.println(sResult.subSequence(a + 6, b));
		File dir = new File(dpath);
		for (File ts : dir.listFiles()) {
			String filename = ts.getName();
			if (filename.indexOf("ts") > 0) {
				String seq = filename.substring(6, filename.length() - 3);
				int iseq = Integer.parseInt(seq);
				// System.out.println(seq);
				if (ilts > 7 && iseq < ilts - 7) {
					// System.out.println("lskfjlksd");
					ts.delete();
				} else if (iseq > ilts) {
					ts.delete();
				}

			}
		}

		// FileUtils.writeStringToFile(file, newResult, StandardCharsets.UTF_8);
	}
}
