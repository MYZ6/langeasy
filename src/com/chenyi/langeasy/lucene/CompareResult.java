package com.chenyi.langeasy.lucene;

public class CompareResult {
	public static void main(String[] args) {
		String[] arr = new String[] {};
		String[] arr2 = new String[] { "abaft", "approbatory", "betroth", "bungler", "churlishness", "contumacious",
				"deliquesce", "extricable", "froward", "imprecate", "incommodious", "ligneous", "minatory",
				"nefariousness", "objurgate", "obtrude", "opprobrious", "preponderate", "recusant", "retroaction",
				"sapid", "sedulous", "traduce", "tumid", "unwonted" };
		String[] arr3 = new String[] { "abaft", "approbatory", "betroth", "bungler", "churlishness", "contumacious",
				"deliquesce", "extricable", "froward", "imprecate", "incommodious", "ligneous", "minatory",
				"nefariousness", "objurgate", "obtrude", "opprobrious", "preponderate", "recusant", "retroaction",
				"sedulous", "traduce", "tumid", "unwonted" };
		compare(arr2, arr3);
	}

	public static void compare(String[] arrMore, String[] arrLess) {
		int index = 0;
		for (String word : arrMore) {
			index++;
			boolean exist = false;
			for (String word2 : arrLess) {
				if (word2.equals(word)) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				System.out.println("new found index: " + index);
				System.out.println("new found: " + word);
			}

		}

	}
}
