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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing. Run
 * it with no command-line arguments for usage information.
 */
public class IndexFiles {

	public static void main(String[] args) throws Exception {
		System.out.println("start time is: " + new Date());
		List<String> dpathList = new ArrayList<>();
		// dpathList.add(pathRender("StanfordUniversity"));
		// dpathList.add(pathRender("Harvard"));
		// dpathList.add(pathRender("MIT"));
		// dpathList.add(pathRender("CambridgeUniversity"));
		// dpathList.add(pathRender("oxford"));
		// dpathList.add(pathRender("OUPAcademic"));
		// dpathList.add(pathRender("OxfordSBS"));
		// dpathList.add(pathRender("princetonuniversity"));
		// dpathList.add(pathRender("UCBerkeley"));
		// dpathList.add(pathRender("UCLA"));
		// dpathList.add(pathRender("um"));
		// dpathList.add(pathRender("PennState"));
		// dpathList.add(pathRender("nyu"));
		// dpathList.add(pathRender("MichiganStateU"));
		// dpathList.add(pathRender("columbiauniversity"));
		// dpathList.add(pathRender("scishow"));
		// dpathList.add(pathRender("UCtelevision"));
		// dpathList.add(pathRender("UChicago"));
		// dpathList.add(pathRender("UWTV"));
		// dpathList.add(pathRender("Duke"));
		for (String dpath : dpathList) {
			index(dpath);
		}
		SearchTest.search();
		System.out.println("end time is: " + new Date());
	}

	private static String pathRender(String channelName) {
		return "E:/langeasy/lucene/youtube/" + channelName + "/caption";
	}

	/** Index all text files under a directory. */
	public static void index(String docsPath) {
		// String indexPath = "index";
		String indexPath = "F:/Personal/ws_indigo/lucene/index";
		// String docsPath = null;
		// String docsPath = "E:/langeasy/lucene/podcast";
		// boolean create = true;
		// String docsPath = "E:/langeasy/lucene/tv";
		// String docsPath = "E:/langeasy/lucene/srt";
		// String docsPath = "E:/langeasy/lucene/podcast/freshair/transcript";
		// String docsPath = "E:/langeasy/lucene/podcast/money/transcript";
		// String docsPath =
		// "E:/langeasy/lucene/podcast/allthingsconsidered/transcript";
		// String docsPath =
		// "E:/langeasy/lucene/podcast/morningedition/transcript";
		// String docsPath =
		// "E:/langeasy/lucene/podcast/wait-wait-dont-tell-me/transcript";
		// String docsPath =
		// "E:/langeasy/lucene/podcast/invisibilia/transcript";
		// String docsPath =
		// "E:/langeasy/lucene/podcast/hidden-brain/transcript";
		// String docsPath =
		// "E:/langeasy/lucene/podcast/weekend-edition-saturday/transcript";
		// String docsPath =
		// "E:/langeasy/lucene/podcast/weekend-edition-sunday/transcript";
		// String docsPath = "E:/langeasy/lucene/podcast/politics/transcript";
		// String docsPath =
		// "E:/langeasy/lucene/podcast/ask-me-another/transcript";
		// String docsPath = "E:/langeasy/lucene/podcast/ted-talks/transcript";
		// String docsPath =
		// "E:/langeasy/lucene/podcast/freakonomics/transcript";
		// String docsPath = "E:/langeasy/lucene/podcast/serial/transcript";
		// String docsPath = "E:/langeasy/lucene/podcast/ted-ed/transcript";
		// String docsPath = "E:/langeasy/lucene/podcast/yale-courses/transcript";
		// String docsPath = "E:/langeasy/lucene/youtube/nasa/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/movieclipsTRAILERS/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/Vsauce/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/vice/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/DiscoveryNetworks/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/collegehumor/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/AnimalPlanetTV/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/AsapSCIENCE/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/latenight/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/rhettandlink2/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/TheEllenShow/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/zoella280390/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/cnn-breaking-news/caption";
		// String docsPath = "E:/langeasy/lucene/youtube/TEDxTalks";
		// String docsPath = "E:/langeasy/lucene/youtube/spotlight";
		// String docsPath = "E:/langeasy/lucene/youtube/Howcast";
		boolean create = false;

		final Path docDir = Paths.get(docsPath);
		if (!Files.isReadable(docDir)) {
			System.out.println("Document directory '" + docDir.toAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);

			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);

			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here. This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			//
			// writer.forceMerge(1);

			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	/**
	 * Indexes the given file using the given writer, or if a directory is
	 * given, recurses over files and directories found under the given
	 * directory.
	 * 
	 * NOTE: This method indexes one document per input file. This is slow. For
	 * good throughput, put multiple documents into your input file(s). An
	 * example of this is in the benchmark module, which can create "line doc"
	 * files, one document per line, using the <a href=
	 * "../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
	 * >WriteLineDocTask</a>.
	 * 
	 * @param writer
	 *            Writer to the index where the given file/dir info will be
	 *            stored
	 * @param path
	 *            The file to index, or the directory to recurse into to find
	 *            files to index
	 * @throws IOException
	 *             If there is a low-level I/O error
	 */
	static void indexDocs(final IndexWriter writer, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
					} catch (IOException ignore) {
						// don't index files that can't be read.
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}

	static int count = 0;

	/** Indexes a single document */
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			count++;
			if (count % 500 == 499) {
				System.out.println(count + "/" + new Date());
			}
			// make a new, empty document
			Document doc = new Document();

			// Add the path of the file as a field named "path". Use a
			// field that is indexed (i.e. searchable), but don't tokenize
			// the field into separate words and don't index term frequency
			// or positional information:
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);

			// Add the last modified date of the file a field named "modified".
			// Use a LongField that is indexed (i.e. efficiently filterable with
			// NumericRangeFilter). This indexes to milli-second resolution,
			// which
			// is often too fine. You could instead create a number based on
			// year/month/day/hour/minutes/seconds, down the resolution you
			// require.
			// For example the long value 2011021714 would mean
			// February 17, 2011, 2-3 PM.
			doc.add(new LongField("modified", lastModified, Field.Store.NO));

			// Add the contents of the file to a field named "contents". Specify
			// a Reader,
			// so that the text of the file is tokenized and indexed, but not
			// stored.
			// Note that FileReader expects the file to be in UTF-8 encoding.
			// If that's not the case searching for special characters will
			// fail.
			doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				// New index, so we just add the document (no old document can
				// be there):
				System.out.println("adding " + file);
				writer.addDocument(doc);
			} else {
				// Existing index (an old copy of this document may have been
				// indexed) so
				// we use updateDocument instead to replace the old one matching
				// the exact
				// path, if present:
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}
}
