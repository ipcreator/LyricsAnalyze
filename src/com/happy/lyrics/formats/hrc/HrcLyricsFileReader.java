package com.happy.lyrics.formats.hrc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.happy.lyrics.LyricsFileReader;
import com.happy.lyrics.model.LyricsInfo;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.model.LyricsTag;
import com.happy.lyrics.utils.CharUtils;
import com.happy.lyrics.utils.TimeUtils;

/**
 * hrc歌词解析器
 * 
 * @author zhangliangming
 * 
 */
public class HrcLyricsFileReader extends LyricsFileReader {
	/**
	 * 歌曲名 字符串
	 */
	private final static String LEGAL_SONGNAME_PREFIX = "haplayer.songName";
	/**
	 * 歌手名 字符串
	 */
	private final static String LEGAL_SINGERNAME_PREFIX = "haplayer.singer";
	/**
	 * 时间补偿值 字符串
	 */
	private final static String LEGAL_OFFSET_PREFIX = "haplayer.offset";
	/**
	 * 歌词Tag
	 */
	public final static String LEGAL_TAG_PREFIX = "haplayer.tag";
	/**
	 * 歌词 字符串
	 */
	public final static String LEGAL_LYRICS_LINE_PREFIX = "haplayer.lrc";

	public HrcLyricsFileReader() {
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
		lyricsIfno.setLyricsFileExt(getSupportFileExt());
		if (in != null) {
			// 获取歌词文件里面的所有内容，并对文本内容进行解压
			String lyricsTextStr = StringCompress.decompress(toByteArray(in),
					getDefaultCharset());
			// System.out.println(lyricsTextStr);
			String[] lyricsTexts = lyricsTextStr.split("\n");
			// 这里面key为该行歌词的开始时间，方便后面排序
			SortedMap<Integer, LyricsLineInfo> lyricsLineInfosTemp = new TreeMap<Integer, LyricsLineInfo>();
			Map<String, Object> lyricsTags = new HashMap<String, Object>();
			for (int i = 0; i < lyricsTexts.length; i++) {
				try {
					// 解析歌词
					parserLineInfos(lyricsLineInfosTemp, lyricsTags,
							lyricsTexts[i]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			in.close();
			// 重新封装
			TreeMap<Integer, LyricsLineInfo> lyricsLineInfos = new TreeMap<Integer, LyricsLineInfo>();
			int index = 0;
			Iterator<Integer> it = lyricsLineInfosTemp.keySet().iterator();
			while (it.hasNext()) {
				lyricsLineInfos
						.put(index++, lyricsLineInfosTemp.get(it.next()));
			}
			// 设置歌词的标签类
			lyricsIfno.setLyricsTags(lyricsTags);
			//
			lyricsIfno.setLyricsLineInfos(lyricsLineInfos);
		}
		return lyricsIfno;
	}

	/**
	 * 解析每行的歌词
	 * 
	 * @param lyricsLineInfos
	 * @param lyricsTags
	 * @param string
	 */
	private void parserLineInfos(
			SortedMap<Integer, LyricsLineInfo> lyricsLineInfos,
			Map<String, Object> lyricsTags, String lineInfo) {
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
			int left = LEGAL_LYRICS_LINE_PREFIX.length() + 1;
			int right = lineInfo.length();
			String[] lineComments = lineInfo.substring(left + 1, right - 3)
					.split("'\\s*,\\s*'", -1);
			// 歌词
			String lineLyricsStr = lineComments[1];
			List<String> lineLyricsList = getLyricsWords(lineLyricsStr);

			// 歌词分隔
			String[] lyricsWords = lineLyricsList
					.toArray(new String[lineLyricsList.size()]);

			// 获取当行歌词
			String lineLyrics = getLineLyrics(lineLyricsStr);

			// 时间标签
			String timeText = lineComments[0];
			int timeLeft = timeText.indexOf('<');
			int timeRight = timeText.length();
			timeText = timeText.substring(timeLeft + 1, timeRight - 1);
			String[] timeTexts = timeText.split("><");

			// 每个歌词的时间标签
			String wordsDisIntervalText = lineComments[2];
			String[] wordsDisIntervalTexts = wordsDisIntervalText.split(",");

			parserLineInfos(lyricsLineInfos, lyricsWords, lineLyrics,
					timeTexts, wordsDisIntervalTexts);
		}

	}

	/**
	 * 解析每行歌词的数据
	 * 
	 * @param lyricsLineInfos
	 * @param lyricsWords
	 *            歌词
	 * @param lineLyrics
	 *            该行歌词
	 * @param timeTexts
	 *            时间文本
	 * @param wordsDisIntervalTexts
	 */
	private void parserLineInfos(
			SortedMap<Integer, LyricsLineInfo> lyricsLineInfos,
			String[] lyricsWords, String lineLyrics, String[] timeTexts,
			String[] wordsDisIntervalTexts) {
		if (timeTexts.length == wordsDisIntervalTexts.length) {
			for (int i = 0; i < wordsDisIntervalTexts.length; i++) {

				LyricsLineInfo lyricsLineInfo = new LyricsLineInfo();

				// 每一行的开始时间和结束时间
				String timeTextStr = timeTexts[i];
				String[] timeTextCom = timeTextStr.split(",");

				String startTimeStr = timeTextCom[0];
				int startTime = TimeUtils.parseInteger(startTimeStr);

				String endTimeStr = timeTextCom[1];
				int endTime = TimeUtils.parseInteger(endTimeStr);

				lyricsLineInfo.setEndTime(endTime);
				lyricsLineInfo.setEndTimeStr(endTimeStr);
				lyricsLineInfo.setStartTimeStr(startTimeStr);
				lyricsLineInfo.setStartTime(startTime);

				//
				lyricsLineInfo.setLineLyrics(lineLyrics);
				lyricsLineInfo.setLyricsWords(lyricsWords);

				// 每一行歌词的每个时间
				String wordsDisIntervalStr = wordsDisIntervalTexts[i];
				List<String> wordsDisIntervalList = getWordsDisIntervalList(wordsDisIntervalStr);
				int wordsDisInterval[] = getWordsDisIntervalList(wordsDisIntervalList);
				lyricsLineInfo.setWordsDisInterval(wordsDisInterval);
				//
				lyricsLineInfos.put(startTime, lyricsLineInfo);
			}
		}
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
			case ':':
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
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	private int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		if (count > 2147483647L) {
			return -1;
		}
		return (int) count;
	}

	private long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[4096];
		long count = 0L;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	@Override
	public boolean isFileSupported(String ext) {
		return ext.equalsIgnoreCase("hrc");
	}

	@Override
	public String getSupportFileExt() {
		return "hrc";
	}

}
