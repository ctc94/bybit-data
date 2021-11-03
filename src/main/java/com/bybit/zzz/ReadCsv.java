package com.bybit.zzz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;

public class ReadCsv {
	private static final Logger log = LoggerFactory.getLogger(ReadCsv.class);
	static String columnList[] = null;

	public List<Map<String, String>> getData(File file) {
		// 반환용 리스트
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
		BufferedReader br = null;
		try {
			br = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
			// Charset.forName("UTF-8");
			String line = "";
			int i = 0;
			while ((line = br.readLine()) != null) {
				
				line = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, line);
				String array[] = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, line).split(",");
				if (i++ == 0) {// 첫째행은 컬럼명을 가져온다.
					columnList = array;
					continue;
				} else {// 나머니 데이터는 키값을 매핑시킨 맵데이터로 넣는다.
					Map<String, String> m = new HashMap<String, String>();
					int j = 0;
					for (String v : array) {
						m.put(columnList[j++], v);
					}
					ret.add(m);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.info(ret.get(0).toString());
		return ret;
	}
}
