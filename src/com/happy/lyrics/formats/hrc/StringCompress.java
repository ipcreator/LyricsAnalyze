package com.happy.lyrics.formats.hrc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * 字符串解压和压缩
 * 
 * @author zhangliangming
 * 
 */
public class StringCompress {

	/**
	 * 压缩
	 * 
	 * @param text
	 * @param charset
	 * @return
	 */
	public static byte[] compress(String text, Charset charset) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			OutputStream out = new DeflaterOutputStream(baos);
			out.write(text.getBytes(charset));
			out.close();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return baos.toByteArray();
	}

	/**
	 * 解压
	 * 
	 * @param bytes
	 * @param charset
	 * @return
	 */
	public static String decompress(byte[] bytes, Charset charset) {
		InputStream in = new InflaterInputStream(
				new ByteArrayInputStream(bytes));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[8192];
			int len;
			while ((len = in.read(buffer)) > 0)
				baos.write(buffer, 0, len);
			return new String(baos.toByteArray(), charset);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
}