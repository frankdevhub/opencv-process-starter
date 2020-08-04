package com.zcunsoft.image.process.env;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @Author:frankdevhub@gmail.com</br>
 * 
 * @CreateDate:2020年8月3日 下午4:45:41</br>
 * @Version: 1.0</br>
 * @Type:DevEnvironmentTest.java</br>
 * @github:https://github.com/frankdevhub</br>
 * @blog:www.frankdevhub.site</br>
 *
 */
public class DevEnvironmentTest {

	// ERROR:
	// java.lang.UnsatisfiedLinkError: no opencv_java440 in java.library.path
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	// 安装环境监测,测试是否能够正常运行
	@Test
	public void checkDevEnvironment() {
		LOGGER.debug("load local system library");

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Mat.eye(4, 4, CvType.CV_8UC1);
		System.out.println("mat = \n" + mat.dump());
	}
}
