package com.chenyi.langeasy.capture.podcast.yalecourses;

import org.json.JSONArray;

public class CaptionUrlCompare {
	public static void main(String[] args) {
		String str1 = "https://www.youtube.com/api/timedtext?signature=9917F1616F6C80DA07BC721DFD38B2493889FBD4.BEAC079CC0A2B21DB3F05B19DF7C9D657650AD3F&sparams=asr_langs%2Ccaps%2Cv%2Cexpire&key=yttt1&caps=asr&hl=en_US&expire=1479830730&v=FGvWvsJcIEw&asr_langs=es%2Cru%2Cit%2Cnl%2Cpt%2Cde%2Cko%2Cja%2Cen%2Cfr&kind=asr&lang=en&fmt=srv3";
		String str2 = "https://www.youtube.com/api/timedtext?expire=1479831024&asr_langs=pt%2Cnl%2Cfr%2Ces%2Cit%2Cen%2Cja%2Cde%2Cko%2Cru&v=FGvWvsJcIEw&key=yttt1&signature=3C0F11822327394CDBCDBB382E46B053BFE8705B.7E4B07530C4F59B469A8404A37676D2D16ACD971&sparams=asr_langs%2Ccaps%2Cv%2Cexpire&caps=asr&hl=en_US&lang=en&fmt=srv3";
		String[] arr1 = str1.split("&");
		String[] arr2 = str2.split("&");
		System.out.println(arr1.length);
		System.out.println(arr2.length);
		System.out.println(new JSONArray(arr1).toString(3));
		System.out.println(new JSONArray(arr2).toString(3));
	}
}
