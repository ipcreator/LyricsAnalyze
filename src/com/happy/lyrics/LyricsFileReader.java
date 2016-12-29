package com.happy.lyrics;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.happy.lyrics.model.LyricsInfo;

/**
 * 歌词文件读取器
 * 
 * @author zhangliangming
 * 
 */
public abstract class LyricsFileReader {
	/**
	 * 默认编码
	 */
	protected Charset defaultCharset = Charset.forName("iso8859-1");

	/**
	 * 读取歌词文件
	 * 
	 * @param file
	 * @return
	 */
	public abstract LyricsInfo readFile(File file) throws Exception;

	/**
	 * 读取歌词文件
	 * 
	 * @param in
	 * @return
	 */
	public abstract LyricsInfo readInputStream(InputStream in) throws Exception;

	/**
	 * 支持文件格式
	 * 
	 * @param ext
	 *            文件后缀名
	 * @return
	 */
	public abstract boolean isFileSupported(String ext);

	/**
	 * 获取支持的文件后缀名
	 * 
	 * @return
	 */
	public abstract String getSupportFileExt();

	public void setDefaultCharset(Charset charset) {
		defaultCharset = charset;
	}

	public Charset getDefaultCharset() {
		return defaultCharset;
	}
}
