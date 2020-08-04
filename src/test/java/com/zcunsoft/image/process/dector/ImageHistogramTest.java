package com.zcunsoft.image.process.dector;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcunsoft.image.process.constants.TestCaseConstants;
import com.zcunsoft.image.process.util.HistogramUtil;

/**
 *
 * @Author:frankdevhub@gmail.com</br>
 * 
 * @CreateDate:2020年8月4日 下午5:32:27</br>
 * @Version: 1.0</br>
 * @Type:HistogramTest.java</br>
 * @github:https://github.com/frankdevhub</br>
 * @blog:www.frankdevhub.site</br>
 *
 */
public class ImageHistogramTest {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Test
	public void testImageHistogram() {
		LOGGER.debug("testImageHistogram start");

		Mat src = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "yitulu_01.png");
		if (src.empty()) {
			return;
		}
		// 灰度直方图---矩形块
		// HistogramUtil.showGrayHistogramRectangle(src);
		// 灰度直方图---描点
		// HistogramUtil.showGrayHistogramLine(src);
		// 3个彩色直方图---矩形块
		HistogramUtil.showColorHistogram(src);
		// 1个彩色直方图---描点
		HistogramUtil.showColorHistogramInOne(src);
	}
}
