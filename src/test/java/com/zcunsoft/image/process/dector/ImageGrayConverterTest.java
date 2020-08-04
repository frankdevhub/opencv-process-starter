package com.zcunsoft.image.process.dector;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcunsoft.image.process.constants.TestCaseConstants;

/**
 *
 * @Author:frankdevhub@gmail.com</br>
 * 
 * @CreateDate:2020年8月4日 上午11:47:18</br>
 * @Version: 1.0</br>
 * @Type:ImageGrayConverterTest.java</br>
 * @github:https://github.com/frankdevhub</br>
 * @blog:www.frankdevhub.site</br>
 *
 */
public class ImageGrayConverterTest {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Test
	// 单通道,多通道图片转换为灰度图片
	public void srcBorderDector() {
		LOGGER.info("srcGrayConverterstart");
		LOGGER.debug("load testing src sample");

		Mat src = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "camera_env03.jpg");
		Imgproc.resize(src, src, new Size(src.cols() / 2, src.rows() / 2));

		Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY);
		int channels = src.channels();// 获取图像通道数
		for (int i = 0, rlen = src.rows(); i < rlen; i++) {
			for (int j = 0, clen = src.cols(); j < clen; j++) {
				if (channels == 3) {// 图片为3通道即平常的(B,G,R)
					System.out.println(i + "," + j + "B:" + src.get(i, j)[0]);
					System.out.println(i + "," + j + "G:" + src.get(i, j)[1]);
					System.out.println(i + "," + j + "R:" + src.get(i, j)[2]);
				} else {// 图片为单通道
					System.out.println(i + "," + j + ":" + src.get(i, j)[0]);
				}
			}
		}
		HighGui.imshow("src", src);
		HighGui.waitKey();
	}
}
