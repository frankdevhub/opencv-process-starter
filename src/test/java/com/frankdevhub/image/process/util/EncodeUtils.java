package com.frankdevhub.image.process.util;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @Author:frankdevhub@gmail.com</br>
 * 
 * @CreateDate:2020年8月4日 下午9:58:38</br>
 * @Version: 1.0</br>
 * @Type:GetEncode.java</br>
 * @github:https://github.com/frankdevhub</br>
 * @blog:www.frankdevhub.site</br>
 *
 */

// 编码解码工具类
public class EncodeUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(EncodeUtils.class);

	public static String transcode(String str, String sourceCharset, String targetCharset) {
		if (str == null)
			return null;
		String retStr = str;
		byte b[];
		try {
			b = str.getBytes(sourceCharset);
			for (int i = 0; i < b.length; i++) {
				byte b1 = b[i];
				if (b1 == 63)
					break;
				else if (b1 > 0)
					continue;
				else if (b1 < 0) {
					retStr = new String(b, targetCharset);
					break;
				}
			}
			b = retStr.getBytes();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			LOGGER.debug(String.format("restStr = %s", retStr));
		}
		return retStr;
	}

	// 将字符串先按ISO8859-1解码，再按UTF-8编码
	public static String transcode(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		return transcode(str, "ISO8859-1", "UTF-8");
	}
}
