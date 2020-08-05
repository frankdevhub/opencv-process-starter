package com.zcunsoft.image.process.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcunsoft.image.process.constants.TestCaseConstants;

import junit.framework.Assert;

/**
 *
 * @Author:frankdevhub@gmail.com</br>
 * 
 * @CreateDate:2020年8月4日 下午10:08:51</br>
 * @Version: 1.0</br>
 * @Type:ImageDownloader.java</br>
 * @github:https://github.com/frankdevhub</br>
 * @blog:www.frankdevhub.site</br>
 *
 */

public class ImageDownloader {

	private final Logger LOGGER = LoggerFactory.getLogger(ImageDownloader.class);

	public static boolean downloadImageResource(String fileUrl, String savePath) {
		try {
			Assert.assertNotNull(fileUrl, "cannot find fileUrl");
			Assert.assertNotNull(savePath, "cannot find savePath");

			URL url = new URL(fileUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			DataInputStream in = new DataInputStream(connection.getInputStream());

			DataOutputStream out = new DataOutputStream(new FileOutputStream(savePath));
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			out.close();
			in.close();
			connection.disconnect();
			return true;
		} catch (Exception e) {
			System.out.println(e + fileUrl + savePath);
			return false;
		}
	}

	@Test
	public void testDownloadImageResource() {
		String fileUrl = "https://art-photo-oss.zcunsoft.com/data/itemZip/20200730/d7ab0ff27dab4262bab0c7f7890536ae.jpg";
		String savePath = TestCaseConstants.SAMPLE_PATH_PREFIX + "example.jpg";
		boolean success = downloadImageResource(fileUrl, savePath);
		LOGGER.debug(String.format("success = %s", success));
	}
}
