package com.happy.lyrics.test;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import com.happy.lyrics.LyricsFileReader;
import com.happy.lyrics.LyricsFileWriter;
import com.happy.lyrics.model.LyricsInfo;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.model.LyricsTag;
import com.happy.lyrics.system.LyricsInfoIO;

public class Test {

	public static void main(String[] args) throws Exception {
		File lyricsfile = new File("haplayer/lyrics/李健 - 美若黎明.krc");
		// File lyricsfile = new
		// File("haplayer/lyrics/蔡健雅 - Beautiful Love2.hrc");
		// File lyricsfile = new File("haplayer/lyrics/姚贝娜 - 可以不可以.hrc");

		 LyricsFileReader lyricsFileReader = LyricsInfoIO
		 .getLyricsFileReader(lyricsfile);
		 LyricsInfo lyricsIfno = lyricsFileReader.readFile(lyricsfile);
		
		 Map<String, Object> tags = lyricsIfno.getLyricsTags();
		 for (Map.Entry<String, Object> entry : tags.entrySet()) {
		 System.out.println(entry.getKey() + "=" + entry.getValue());
		 }
		 //
		 TreeMap<Integer, LyricsLineInfo> lyricsLineInfos = lyricsIfno
		 .getLyricsLineInfos();
		 for (int i = 0; i < lyricsLineInfos.size(); i++) {
		 LyricsLineInfo lyricsLineInfo = lyricsLineInfos.get(i);
		 System.out.println(lyricsLineInfo.getLineLyrics());
		 }
//
//		 lyricsIfno.getLyricsTags().put(LyricsTag.TAG_BY, "zhangliangming");
//		 LyricsFileWriter lyricsFileWriter = LyricsInfoIO
//		 .getLyricsFileWriter("haplayer/lyrics/李健 - 美若黎明2.krc");
//		 //
//		 lyricsFileWriter.writer(lyricsIfno,
//		 "haplayer/lyrics/李健 - 美若黎明2.krc");

		// System.out.println(ZipUtil.compress(lyricsFileWriter.parseLyricsInfo(lyricsIfno)));
	}

}
