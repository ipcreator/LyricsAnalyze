package com.happy.lyrics.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * 歌词数据
 * 
 * @author zhangliangming
 * 
 */
public class LyricsInfo {
	/**
	 * 歌词格式
	 */
	private String lyricsFileExt;
	/**
	 * 所有的歌词行数据
	 */
	private TreeMap<Integer, LyricsLineInfo> lyricsLineInfos;
	/**
	 * 歌词标签
	 */
	private Map<String, Object> lyricsTags;

	public Map<String, Object> getLyricsTags() {
		return lyricsTags;
	}

	public void setLyricsTags(Map<String, Object> lyricsTags) {
		this.lyricsTags = lyricsTags;
	}

	public TreeMap<Integer, LyricsLineInfo> getLyricsLineInfos() {
		return lyricsLineInfos;
	}

	public void setLyricsLineInfos(
			TreeMap<Integer, LyricsLineInfo> lyricsLineInfos) {
		this.lyricsLineInfos = lyricsLineInfos;
	}

	public String getLyricsFileExt() {
		return lyricsFileExt;
	}

	public void setLyricsFileExt(String lyricsFileExt) {
		this.lyricsFileExt = lyricsFileExt;
	}

}
