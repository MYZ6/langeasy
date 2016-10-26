package com.chenyi.langeasy.capture.caudio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class AudioDownloader2 {

	private static List<Map<String, String>> downloadLst;

	public static void main(String[] args) throws FileNotFoundException, SQLException, IOException {
		System.out.println("start time is : " + new Date());
		listCourse();
	}

	private static int step = 30;

	private static void listCourse() throws SQLException, FileNotFoundException, IOException {
		String[] fileArr = new String[] { "ListenData/975161239/973892423.mp3", "ListenData/1694965458/546226968.mp3",
				"ListenData/975161239/1594034406.mp3", "ListenData/538418611/1508282133.mp3",
				"ListenData/1404568722/1234304315.mp3", "ListenData/100279329/1972299063.mp3",
				"ListenData/1614172264/1284398478.mp3", "ListenData/1694965458/955086907.mp3",
				"ListenData/1639397225/1881330683.mp3", "ListenData/915900259/416383692.mp3",
				"ListenData/269296427/1501724190.mp3", "ListenData/975161239/2022006926.mp3",
				"ListenData/1639397225/2132144591.mp3", "ListenData/1639397225/1568007235.mp3",
				"ListenData/1639397225/149449912.mp3", "ListenData/975161239/2006823738.mp3",
				"ListenData/1694965458/1700384570.mp3", "ListenData/915900259/1283812332.mp3",
				"ListenData/1639397225/2035886054.mp3", "ListenData/1639397225/921380867.mp3",
				"ListenData/1639397225/1293250087.mp3", "ListenData/1404568722/202891174.mp3",
				"ListenData/269296427/1566881921.mp3", "ListenData/269296427/251237486.mp3",
				"ListenData/269296427/892280622.mp3", "ListenData/1639397225/1277063366.mp3",
				"ListenData/269296427/1608546110.mp3", "ListenData/975161239/1355351857.mp3",
				"ListenData/538418611/1289992980.mp3", "ListenData/270963162/1788762000.mp3",
				"ListenData/269296427/99450649.mp3", "ListenData/269296427/873014972.mp3",
				"ListenData/1404568722/1728565991.mp3", "ListenData/1639397225/1906471668.mp3",
				"ListenData/1639397225/544405748.mp3", "ListenData/1639397225/243753033.mp3",
				"ListenData/975161239/1334933673.mp3", "ListenData/1639397225/866118122.mp3",
				"ListenData/975161239/902963353.mp3", "ListenData/538418611/435270771.mp3",
				"ListenData/1639397225/435253481.mp3", "ListenData/538418611/696360708.mp3",
				"ListenData/975161239/2067926269.mp3", "ListenData/566837170/2080403266.mp3",
				"ListenData/566837170/1409475073.mp3", "ListenData/566837170/1715838906.mp3",
				"ListenData/566837170/1015560536.mp3", "ListenData/1639397225/582093946.mp3",
				"ListenData/678144306/735834301.mp3", "ListenData/538418611/531537211.mp3",
				"ListenData/538418611/1694306083.mp3", "ListenData/1614172264/470198465.mp3",
				"ListenData/538418611/67025780.mp3", "ListenData/538418611/1797504759.mp3",
				"ListenData/351571393/360294857.mp3", "ListenData/351571393/1285362284.mp3",
				"ListenData/856365664/1486044527.mp3", "ListenData/856365664/1772554675.mp3",
				"ListenData/678144306/295898653.mp3", "ListenData/134132630/1395611123.mp3",
				"ListenData/134132630/761938788.mp3", "ListenData/134132630/853425962.mp3",
				"ListenData/134132630/1581208336.mp3", "ListenData/134132630/893671949.mp3",
				"ListenData/349559752/1919249301.mp3", "ListenData/1361359286/208200553.mp3",
				"ListenData/678144306/40542289.mp3", "ListenData/678144306/1110707317.mp3",
				"ListenData/686014651/1986969033.mp3", "ListenData/686014651/1181442422.mp3",
				"ListenData/686014651/1640037284.mp3", "ListenData/686014651/54606657.mp3",
				"ListenData/686014651/396221915.mp3", "ListenData/686014651/1860628019.mp3",
				"ListenData/678144306/1221299498.mp3", "ListenData/678144306/1686957162.mp3",
				"ListenData/349559752/2036962820.mp3", "ListenData/686014651/1017140636.mp3",
				"ListenData/686014651/184794485.mp3", "ListenData/686014651/1248524632.mp3",
				"ListenData/686014651/591567677.mp3", "ListenData/686014651/105434011.mp3",
				"ListenData/686014651/226056648.mp3", "ListenData/538418611/325450934.mp3",
				"ListenData/279257204/1275472909.mp3", "ListenData/279257204/1200045357.mp3",
				"ListenData/279257204/1214703058.mp3", "ListenData/279257204/160010456.mp3",
				"ListenData/279257204/2091904530.mp3", "ListenData/279257204/382616665.mp3",
				"ListenData/686014651/287419977.mp3", "ListenData/686014651/1714731746.mp3",
				"ListenData/686014651/1519080275.mp3", "ListenData/686014651/720030741.mp3",
				"ListenData/686014651/816402310.mp3", "ListenData/686014651/1613647675.mp3",
				"ListenData/686014651/728874755.mp3", "ListenData/686014651/111013078.mp3",
				"ListenData/538418611/917813100.mp3", "ListenData/686014651/1243820043.mp3",
				"ListenData/686014651/1478349903.mp3", "ListenData/686014651/1019825734.mp3",
				"ListenData/686014651/148970437.mp3", "ListenData/686014651/1715741050.mp3",
				"ListenData/686014651/1690765956.mp3", "ListenData/686014651/103714641.mp3",
				"ListenData/686014651/1398870792.mp3", "ListenData/1110678245/13958616.mp3",
				"ListenData/1110678245/1489634316.mp3", "ListenData/1110678245/2096557822.mp3",
				"ListenData/1110678245/1317885368.mp3", "ListenData/1110678245/466304437.mp3",
				"ListenData/1694965458/2049318059.mp3", "ListenData/975161239/467881096.mp3",
				"ListenData/2130112943/1919382179.mp3", "ListenData/2130112943/175632975.mp3",
				"ListenData/2130112943/1091515512.mp3", "ListenData/2130112943/1972654064.mp3",
				"ListenData/2130112943/1312279440.mp3", "ListenData/2130112943/1400467382.mp3",
				"ListenData/2130112943/724516917.mp3", "ListenData/2130112943/832583499.mp3",
				"ListenData/2130112943/416458672.mp3", "ListenData/2130112943/384363107.mp3",
				"ListenData/862934329/1690765694.mp3", "ListenData/538418611/189667656.mp3",
				"ListenData/279257204/1900076561.mp3", "ListenData/279257204/295832386.mp3",
				"ListenData/279257204/663897049.mp3", "ListenData/279257204/273261461.mp3",
				"ListenData/279257204/409955812.mp3", "ListenData/279257204/1590305229.mp3",
				"ListenData/279257204/1179855630.mp3", "ListenData/279257204/1552543147.mp3",
				"ListenData/279257204/1831823298.mp3", "ListenData/279257204/413151068.mp3",
				"ListenData/279257204/1291425787.mp3", "ListenData/279257204/591119664.mp3",
				"ListenData/678144306/1658759009.mp3", "ListenData/538418611/280201525.mp3",
				"ListenData/1613328532/729486058.mp3", "ListenData/1613328532/1599961439.mp3",
				"ListenData/1613328532/212297090.mp3", "ListenData/1613328532/1500688567.mp3",
				"ListenData/1613328532/1700702506.mp3", "ListenData/183129084/2076557914.mp3",
				"ListenData/183129084/882871400.mp3", "ListenData/183129084/226195619.mp3",
				"ListenData/183129084/1405749497.mp3", "ListenData/183129084/1123094231.mp3",
				"ListenData/183129084/895874167.mp3", "ListenData/183129084/1621713493.mp3",
				"ListenData/183129084/1937789252.mp3", "ListenData/1613328532/1739139280.mp3",
				"ListenData/1613328532/1778407959.mp3", "ListenData/1613328532/1592169615.mp3",
				"ListenData/1613328532/356781175.mp3", "ListenData/183129084/101786113.mp3",
				"ListenData/183129084/898341635.mp3", "ListenData/538418611/1244306994.mp3",
				"ListenData/2130112943/100789977.mp3", "ListenData/2130112943/7519922.mp3",
				"ListenData/2130112943/754831737.mp3", "ListenData/2130112943/1470715458.mp3",
				"ListenData/2130112943/51577294.mp3", "ListenData/183129084/512869662.mp3",
				"ListenData/183129084/1240586468.mp3", "ListenData/183129084/70671683.mp3",
				"ListenData/183129084/2100435025.mp3", "ListenData/183129084/326031878.mp3",
				"ListenData/183129084/151035968.mp3", "ListenData/183129084/435580572.mp3",
				"ListenData/1110678245/1507783203.mp3", "ListenData/1110678245/1071443122.mp3",
				"ListenData/1110678245/161489818.mp3", "ListenData/1110678245/1504963077.mp3",
				"ListenData/1110678245/816486039.mp3", "ListenData/1110678245/111972104.mp3",
				"ListenData/1110678245/81039504.mp3", "ListenData/1110678245/377035851.mp3",
				"ListenData/183129084/1370119476.mp3", "ListenData/183129084/104197197.mp3",
				"ListenData/183129084/236369511.mp3", "ListenData/1879533758/1360712073.mp3",
				"ListenData/1110678245/1406979025.mp3", "ListenData/1110678245/1034204715.mp3",
				"ListenData/1110678245/870744359.mp3", "ListenData/1110678245/616192615.mp3",
				"ListenData/1802837353/614524874.mp3", "ListenData/134132630/895002905.mp3",
				"ListenData/134132630/1371847849.mp3", "ListenData/134132630/592709876.mp3",
				"ListenData/134132630/787590631.mp3", "ListenData/134132630/2146133974.mp3",
				"ListenData/685765767/1517858370.mp3", "ListenData/134132630/510025707.mp3",
				"ListenData/134132630/1194535447.mp3", "ListenData/134132630/981953056.mp3",
				"ListenData/134132630/455501339.mp3", "ListenData/134132630/2128287281.mp3",
				"ListenData/1694965458/105494278.mp3", "ListenData/1802837353/923636375.mp3",
				"ListenData/1212166050/1602455635.mp3", "ListenData/1212166050/1649861104.mp3",
				"ListenData/1212166050/1877607405.mp3", "ListenData/1212166050/101819087.mp3",
				"ListenData/1212166050/1600853159.mp3", "ListenData/1212166050/1891966736.mp3",
				"ListenData/1212166050/1138075418.mp3", "ListenData/1212166050/1936148709.mp3",
				"ListenData/1212166050/1778530223.mp3", "ListenData/1212166050/1263313527.mp3",
				"ListenData/1212166050/1276378173.mp3", "ListenData/1212166050/2106688249.mp3",
				"ListenData/39666258/1562602111.mp3", "ListenData/39666258/1649385037.mp3",
				"ListenData/39666258/1016808352.mp3", "ListenData/39666258/477384569.mp3",
				"ListenData/39666258/1908589758.mp3", "ListenData/39666258/1482244687.mp3",
				"ListenData/39666258/758625399.mp3", "ListenData/39666258/381419869.mp3",
				"ListenData/39666258/1009165157.mp3", "ListenData/39666258/543675814.mp3",
				"ListenData/39666258/528090952.mp3", "ListenData/39666258/689632156.mp3",
				"ListenData/538418611/1026813880.mp3", "ListenData/538418611/1331820708.mp3",
				"ListenData/1694965458/585803168.mp3", "ListenData/1694965458/1115617515.mp3",
				"ListenData/1694965458/2036302679.mp3", "ListenData/1694965458/1630553259.mp3" };
		int count = 0;
		downloadLst = new ArrayList<Map<String, String>>();
		for (String mp3path : fileArr) {
			count++;
			System.out.println("find seq : " + count);
			String filepath = "e:/langeasy/" + mp3path;// "ListenData/1688236492/2089837271.mp3";
			File saveFile = new File(filepath);
			if (saveFile.exists()) {
				// continue;
			}
			String dirpath = saveFile.getParent();
			File dir = new File(dirpath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			System.out.println(dirpath);

			Map<String, String> map = new HashMap<String, String>();
			map.put("mp3path", mp3path);
			map.put("saveFilePath", filepath);
			downloadLst.add(map);
			// downloadMp3(mp3path, saveFile);
		}
		int total = downloadLst.size();// about 1800
		System.out.println(total);
		if (total > -1) {
			// return;
		}

		step = 10;
		AudioDownloader2 downloader = new AudioDownloader2();
		for (int i = 0; i < 24; i++) {
			Job job = downloader.new Job(i);
			job.start();
		}

	}

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			int start = jobIndex * step;
			int end = start + step;
			if (end > downloadLst.size()) {
				end = downloadLst.size();
			}
			System.out.println(start + "\t" + end);

			List<Map<String, String>> subLst = downloadLst.subList(start, end);
			int count = 0;
			for (Map<String, String> map : subLst) {
				count++;
				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					downloadMp3(map.get("mp3path"), map.get("saveFilePath"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void start() {
			System.out.println("Starting job " + jobIndex);
			if (t == null) {
				t = new Thread(this, "job" + jobIndex);
				t.start();
			}
		}

	}

	private static void downloadMp3(String mp3path, String saveFilePath) throws ClientProtocolException, IOException {
		System.out.println(saveFilePath + ", start time is : " + new Date());
		long start = System.currentTimeMillis();

		File saveFile = new File(saveFilePath);

		String url = "http://langeasy.com.cn/" + mp3path;// "ListenData/1688236492/2089837271.mp3";
		HttpGet httpget = new HttpGet(url);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			long startTime = System.currentTimeMillis();
			System.out.println(mp3path + " download begin");
			InputStream inputStream = entity.getContent();
			OutputStream outputStream = new FileOutputStream(saveFile);
			IOUtils.copy(inputStream, outputStream);
			outputStream.close();
			System.out.println(mp3path + " download success");
			long endTime = System.currentTimeMillis();
			System.out.println("download time elapsed: " + (endTime - startTime));
		}
		httpclient.close();
		long end = System.currentTimeMillis();
		System.out.println(saveFilePath + ", consuming seconds : " + (end - start));

	}
}
