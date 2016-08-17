package com.sommer.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Common {
	public static String readConfigFile(String path){
		BufferedReader br = null;
		String content = "";
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(path));

			while ((sCurrentLine = br.readLine()) != null) {
				content += sCurrentLine + "\n";
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return content;
	}
}
