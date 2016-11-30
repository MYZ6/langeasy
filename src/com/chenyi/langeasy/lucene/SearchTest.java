/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chenyi.langeasy.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONArray;

/** Simple command-line based search demo. */
public class SearchTest {

	/** Simple command-line based search demo. */
	public static void main(String[] args) throws Exception {
		search();
	}

	public static void search() throws Exception {
		String index = "F:/Personal/ws_indigo/lucene/index";
		String field = "contents";

		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();

		QueryParser parser = new QueryParser(field, analyzer);
		String[] wordLst = new String[] { "abaft", "abase", "abnegation", "abominate", "abridge", "adjure", "adroit",
				"adulterate", "aggrandize", "allegory", "amalgam", "amalgamate", "amiable", "amortize", "anachronism",
				"apposite", "approbatory", "archetype", "arrogate", "artifice", "aseptic", "asperity", "assay",
				"attenuate", "baneful", "bauble", "behoove", "bemuse", "besmirch", "betroth", "boor", "bumptious",
				"bungler", "burgeon", "canard", "castigate", "catharsis", "cavil", "chaffing", "chary", "choleric",
				"chortle", "churlishness", "circumlocution", "circumlocutory", "cloture", "coda", "coffer", "cogitate",
				"cognate", "cognizant", "comeliness", "commiserate", "complaisance", "conjoin", "connotative",
				"contumacious", "contusion", "conviviality", "convoke", "corpulence", "curmudgeon", "dally",
				"dauntless", "decadence", "decisiveness", "delineate", "deliquesce", "denigrate", "deprecate",
				"depredation", "descant", "despoil", "determinate", "dichotomy", "digress", "dilettante",
				"disapprobation", "disheartened", "disputatious", "dissemble", "dissonant", "distention",
				"divestiture", "ebullience", "ecclesiastic", "edify", "educe", "efface", "effervescence", "effluvium",
				"effrontery", "effusive", "egress", "elaboration", "ellipsis", "embarkation", "enamored", "encomium",
				"encumber", "enervate", "enfeeble", "epicure", "epigram", "equinox", "equivocations", "estimable",
				"euphony", "evanescent", "exculpate", "exigent", "extemporize", "extricable", "exultation", "fatuous",
				"fecund", "fervid", "fetter", "froward", "fulsome", "fustian", "garrulous", "germane", "gerrymander",
				"gibber", "hackneyed", "harangue", "heretic", "homeostasis", "idiosyncrasy", "impale", "impenitent",
				"imperturbable", "impiety", "impolitic", "imprecate", "improvident", "impugn", "imputation",
				"inchoate", "incommodious", "incorporeal", "indigence", "indolent", "indubitably", "infamy",
				"ingenuous", "inimical", "instigate", "insubordinate", "intercede", "inveterate", "iota",
				"irreproachable", "kith", "knavery", "lambaste", "lambent", "latency", "levity", "licentious",
				"ligneous", "litigate", "lugubrious", "macerate", "maculate", "malediction", "malinger", "maudlin",
				"mendacious", "mesmerize", "minatory", "miscreant", "miser", "mollify", "morose", "munificent",
				"nefariousness", "nettle", "noisome", "obdurate", "obeisance", "obfuscate", "objurgate", "obstinate",
				"obtrude", "obviate", "odium", "omniscient", "opalescent", "opprobrious", "ossify", "ostensible",
				"ostracize", "palindrome", "palliate", "pallor", "panegyric", "parsimonious", "peccadillo",
				"pedagogue", "pellucid", "penitent", "perdition", "perfidious", "perquisite", "phlegmatic", "pinioned",
				"plenary", "polemicist", "portend", "prate", "prefatory", "preponderate", "prescience", "prevaricate",
				"promontory", "propinquity", "propitiate", "proselytize", "provident", "qualm", "quiescence",
				"raconteur", "ramification", "recondite", "recumbent", "recusant", "redolent", "refurbish", "relegate",
				"remonstrate", "renascence", "repast", "reprehend", "reprobate", "reproof", "repudiate", "retroaction",
				"revile", "rivet", "roseate", "ruffian", "sagacious", "salutatory", "sapid", "saturnine", "savant",
				"scanty", "sedulous", "servile", "slovenly", "soliloquy", "solubility", "spelunker", "splenetic",
				"suffuse", "sunder", "suppliant", "surreptitious", "torpid", "traduce", "tranquillity",
				"transmutation", "transpire", "troth", "truncate", "tumid", "turpitude", "unfeigned", "unwonted",
				"usury", "winsome", "zealot", "zephyr" };
		List<String> unmatchList = new ArrayList<>();
		for (String word : wordLst) {
			boolean result = search(searcher, parser, word);
			if (!result) {
				unmatchList.add(word);
			}
		}
		System.out.println("findWTotal: " + findWTotal);
		System.out.println("findSTotal: " + findSTotal);
		System.out.println(new JSONArray(unmatchList).toString(3));
		System.out.println(unmatchList.size());
		System.out.println();

		// boolean result = search(searcher, parser, "abc");
		reader.close();
	}

	static int findWTotal = 0;
	static int findSTotal = 0;

	public static boolean search(IndexSearcher searcher, QueryParser parser, String word) throws ParseException,
			IOException {
		Query query = parser.parse(word);
		System.out.println("Searching for: " + query.toString("contents"));

		TopDocs results = searcher.search(query, 3000);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		int start = 0;
		int end = numTotalHits;// Math.min(numTotalHits, hitsPerPage);
		boolean result = false;
		if (numTotalHits > 0) {
			findWTotal++;
			result = true;
		}
		for (int i = start; i < end; i++) {
			findSTotal++;
			Document doc = searcher.doc(hits[i].doc);
			String path = doc.get("path");
			if (path != null) {
				System.out.println((i + 1) + ". " + path);
				String title = doc.get("title");
				if (title != null) {
					System.out.println("   Title: " + doc.get("title"));
				}
			} else {
				System.out.println((i + 1) + ". " + "No path for this document");
			}
		}
		return result;
	}
}
