package com.happy.lyrics.formats.ksc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.happy.lyrics.LyricsFileReader;
import com.happy.lyrics.model.LyricsInfo;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.model.LyricsTag;
import com.happy.lyrics.utils.CharUtils;
import com.happy.lyrics.utils.TimeUtils;

/**
 * ksc歌词解析器
 * 
 * @author zhangliangming
 * 
 */
public class KscLyricsFileReader extends LyricsFileReader {
	/**
	 * 歌曲名 字符串
	 */
	private final static String LEGAL_SONGNAME_PREFIX = "karaoke.songname";
	/**
	 * 歌手名 字符串
	 */
	private final static String LEGAL_SINGERNAME_PREFIX = "karaoke.singer";
	/**
	 * 时间补偿值 字符串
	 */
	private final static String LEGAL_OFFSET_PREFIX = "karaoke.offset";
	/**
	 * 歌词 字符串
	 */
	public final static String LEGAL_LYRICS_LINE_PREFIX = "karaoke.add";

	/**
	 * 歌词Tag
	 */
	public final static String LEGAL_TAG_PREFIX = "karaoke.tag";

	public KscLyricsFileReader() {
		// 设置编码
		setDefaultCharset(Charset.forName("GB2312"));
	}

	@Override
	public LyricsInfo readFile(File file) throws Exception {
		if (file != null) {
			return readInputStream(new FileInputStream(file));
		}
		return null;
	}

	@Override
	public LyricsInfo readInputStream(InputStream in) throws Exception {
		LyricsInfo lyricsIfno = new LyricsInfo();
		if (in != null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					getDefaultCharset()));

			TreeMap<Integer, LyricsLineInfo> lyricsLineInfos = new TreeMap<Integer, LyricsLineInfo>();
			Map<String, Object> lyricsTags = new HashMap<String, Object>();
			int index = 0;
			String lineInfo = "";
			while ((lineInfo = br.readLine()) != null) {
				// 行读取，并解析每行歌词的内容
				LyricsLineInfo lyricsLineInfo = parserLineInfos(lyricsTags,
						lineInfo);
				if (lyricsLineInfo != null) {
					lyricsLineInfos.put(index, lyricsLineInfo);
					index++;
				}
			}
			in.close();

			// 设置歌词的标签类
			lyricsIfno.setLyricsTags(lyricsTags);
			//
			lyricsIfno.setLyricsLineInfos(lyricsLineInfos);
		}
		return lyricsIfno;
	}

	/**
	 * 解析每行的歌词内容
	 * 
	 * @param lyricsLineInfos
	 *            歌词列表
	 * @param lyricsTags
	 *            歌词标签
	 * @param lineInfo
	 *            行歌词内容
	 * @return
	 */
	private LyricsLineInfo parserLineInfos(Map<String, Object> lyricsTags,
			String lineInfo) {
		LyricsLineInfo lyricsLineInfo = null;
		if (lineInfo.startsWith(LEGAL_SONGNAME_PREFIX)) {
			String temp[] = lineInfo.split("\'");
			//
			lyricsTags.put(LyricsTag.TAG_SONGNAME, temp[1]);
		} else if (lineInfo.startsWith(LEGAL_SINGERNAME_PREFIX)) {
			String temp[] = lineInfo.split("\'");
			lyricsTags.put(LyricsTag.TAG_SINGER, temp[1]);
		} else if (lineInfo.startsWith(LEGAL_OFFSET_PREFIX)) {
			String temp[] = lineInfo.split("\'");
			lyricsTags.put(LyricsTag.TAG_OFFSET, temp[1]);
		} else if (lineInfo.startsWith(LEGAL_TAG_PREFIX)) {
			// 自定义标签
			String temp[] = lineInfo.split("\'")[1].split(":");
			lyricsTags.put(temp[0], temp[1]);
		} else if (lineInfo.startsWith(LEGAL_LYRICS_LINE_PREFIX)) {
			lyricsLineInfo = new LyricsLineInfo();

			int left = LEGAL_LYRICS_LINE_PREFIX.length() + 1;
			int right = lineInfo.length();
			String[] lineComments = lineInfo.substring(left + 1, right - 3)
					.split("'\\s*,|\\s*'", -1);
			// 开始时间
			String startTimeStr = lineComments[0];
			lyricsLineInfo.setStartTimeStr(startTimeStr);
			int startTime = TimeUtils.parseInteger(startTimeStr);
			lyricsLineInfo.setStartTime(startTime);

			// 结束时间
			String endTimeStr = lineComments[2];
			lyricsLineInfo.setEndTimeStr(endTimeStr);
			int endTime = TimeUtils.parseInteger(endTimeStr);
			lyricsLineInfo.setEndTime(endTime);

			// 歌词
			String lineLyricsStr = lineComments[4];
			List<String> lineLyricsList = getLyricsWords(lineLyricsStr);

			// 歌词分隔
			String[] lyricsWords = lineLyricsList
					.toArray(new String[lineLyricsList.size()]);
			lyricsLineInfo.setLyricsWords(lyricsWords);

			// 获取当行歌词
			String lineLyrics = getLineLyrics(lineLyricsStr);
			lyricsLineInfo.setLineLyrics(lineLyrics);

			// 获取每个歌词的时间
			List<String> wordsDisIntervalList = getWordsDisIntervalList(lineComments[6]);
			int wordsDisInterval[] = getWordsDisIntervalList(wordsDisIntervalList);
			lyricsLineInfo.setWordsDisInterval(wordsDisInterval);
		}
		return lyricsLineInfo;
	}

	/**
	 * 获取每个歌词的时间
	 * 
	 * @param wordsDisIntervalList
	 * @return
	 */
	private int[] getWordsDisIntervalList(List<String> wordsDisIntervalList) {
		int wordsDisInterval[] = new int[wordsDisIntervalList.size()];
		for (int i = 0; i < wordsDisIntervalList.size(); i++) {
			String wordDisIntervalStr = wordsDisIntervalList.get(i);
			if (isNumeric(wordDisIntervalStr)) {
				wordsDisInterval[i] = Integer.parseInt(wordDisIntervalStr);
			}
		}
		return wordsDisInterval;
	}

	/**
	 * 判断是否是数据字符串
	 * 
	 * @param checkStr
	 * @return
	 */
	private boolean isNumeric(String checkStr) {
		try {
			Integer.parseInt(checkStr);
			return true;
		} catch (NumberFormatException err) {
			return false;
		}
	}

	/**
	 * 获取每个歌词的时间
	 * 
	 * @param string
	 * @return
	 */
	private List<String> getWordsDisIntervalList(String wordsDisIntervalString) {
		List<String> wordsDisIntervalList = new ArrayList<String>();
		String temp = "";
		for (int i = 0; i < wordsDisIntervalString.length(); i++) {
			char c = wordsDisIntervalString.charAt(i);
			switch (c) {
			case ',':
				wordsDisIntervalList.add(temp);
				temp = "";
				break;
			default:
				// 判断是否是数字
				if (Character.isDigit(c)) {
					temp += String.valueOf(wordsDisIntervalString.charAt(i));
				}
				break;
			}
		}
		if (!temp.equals("")) {
			wordsDisIntervalList.add(temp);
		}
		return wordsDisIntervalList;
	}

	/**
	 * 获取当前行歌词，去掉中括号
	 * 
	 * @param lineLyricsStr
	 * @return
	 */
	private String getLineLyrics(String lineLyricsStr) {
		String temp = "";
		for (int i = 0; i < lineLyricsStr.length(); i++) {
			switch (lineLyricsStr.charAt(i)) {
			case '[':
				break;
			case ']':
				break;
			default:
				temp += String.valueOf(lineLyricsStr.charAt(i));
				break;
			}
		}
		return temp;
	}

	/**
	 * 分隔每个歌词
	 * 
	 * @param lineLyricsStr
	 * @return
	 */
	private List<String> getLyricsWords(String lineLyricsStr) {
		List<String> lineLyricsList = new ArrayList<String>();
		String temp = "";
		boolean isEnter = false;
		for (int i = 0; i < lineLyricsStr.length(); i++) {
			char c = lineLyricsStr.charAt(i);
			if (CharUtils.isChinese(c)
					|| (!CharUtils.isWord(c) && c != '[' && c != ']')) {
				if (isEnter) {
					temp += String.valueOf(lineLyricsStr.charAt(i));
				} else {
					lineLyricsList.add(String.valueOf(lineLyricsStr.charAt(i)));
				}
			} else if (c == '[') {
				isEnter = true;
			} else if (c == ']') {
				isEnter = false;
				lineLyricsList.add(temp);
				temp = "";
			} else {
				temp += String.valueOf(lineLyricsStr.charAt(i));
			}
		}
		return lineLyricsList;
	}

	@Override
	public boolean isFileSupported(String ext) {
		return ext.equalsIgnoreCase("ksc");
	}

	@Override
	public String getSupportFileExt() {
		return "ksc";
	}
}
