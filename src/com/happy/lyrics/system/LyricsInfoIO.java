package com.happy.lyrics.system;

import java.io.File;
import java.util.ArrayList;

import com.happy.lyrics.LyricsFileReader;
import com.happy.lyrics.LyricsFileWriter;
import com.happy.lyrics.formats.hrc.HrcLyricsFileReader;
import com.happy.lyrics.formats.hrc.HrcLyricsFileWriter;
import com.happy.lyrics.formats.ksc.KscLyricsFileReader;
import com.happy.lyrics.formats.ksc.KscLyricsFileWriter;
import com.happy.lyrics.utils.FileUtils;

/**
 * 
 * @author zhangliangming
 * 
 */
public class LyricsInfoIO {
	private static ArrayList<LyricsFileReader> readers;
	private static ArrayList<LyricsFileWriter> writers;

	static {
		readers = new ArrayList<LyricsFileReader>();
		readers.add(new KscLyricsFileReader());
		readers.add(new HrcLyricsFileReader());

		//
		writers = new ArrayList<LyricsFileWriter>();
		writers.add(new KscLyricsFileWriter());
		writers.add(new HrcLyricsFileWriter());
	}

	/**
	 * 获取歌词文件读取器
	 * 
	 * @param file
	 * @return
	 */
	public static LyricsFileReader getLyricsFileReader(File file) {
		return getLyricsFileReader(file.getName());
	}

	/**
	 * 获取歌词文件读取器
	 * 
	 * @param fileName
	 * @return
	 */
	public static LyricsFileReader getLyricsFileReader(String fileName) {
		String ext = FileUtils.getFileExt(fileName);
		for (LyricsFileReader lyricsFileReader : readers) {
			if (lyricsFileReader.isFileSupported(ext)) {
				return lyricsFileReader;
			}
		}
		return null;
	}

	/**
	 * 获取歌词保存器
	 * 
	 * @param file
	 * @return
	 */
	public static LyricsFileWriter getLyricsFileWriter(File file) {
		return getLyricsFileWriter(file.getName());
	}

	/**
	 * 获取歌词保存器
	 * 
	 * @param fileName
	 * @return
	 */
	public static LyricsFileWriter getLyricsFileWriter(String fileName) {
		String ext = FileUtils.getFileExt(fileName);
		for (LyricsFileWriter lyricsFileWriter : writers) {
			if (lyricsFileWriter.isFileSupported(ext)) {
				return lyricsFileWriter;
			}
		}
		return null;
	}
}
