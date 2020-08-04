package com.zcunsoft.image.process.dector;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @Author:frankdevhub@gmail.com</br>
 * 
 * @CreateDate:2020年8月3日 下午9:24:39</br>
 * @Version: 1.0</br>
 * @Type:ImageBorderDectorTest.java</br>
 * @github:https://github.com/frankdevhub</br>
 * @blog:www.frankdevhub.site</br>
 *
 */
public class ImageBorderDectorTest {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final String SAMPLE_PATH_PREFIX = "D:/frankdevhub-workspace/samples/";

	@Test
	public void imageBorderDector() {
		LOGGER.info("imageBorderDector start");
		LOGGER.debug("load testing image sample");
		// Mat src =
		// Imgcodecs.imread("src/main/resources/samples/border_test01.jpg");

		// Mat src =
		// Imgcodecs.imread("src/main/resources/cameras/camera_env02.jpg");

		/**
		 * IMREAD_UNCHANGED=-1不进行转化,比如保存为了16位的图片，读取出来仍然为16位
		 * IMREAD_GRAYSCALE=0进行转化为灰度图,比如保存为了16位的图片,读取出来为8位,类型为CV_8UC1
		 * IMREAD_COLOR=1进行转化为三通道图像。
		 * IMREAD_ANYDEPTH=2如果图像深度为16位则读出为16位,32位则读出为32位,其余的转化为8位
		 * IMREAD_ANYCOLOR=4图像以任何可能的颜色格式读取 IMREAD_LOAD_GDAL=8使用GDAL驱动读取文件
		 *
		 */

		Mat src = Imgcodecs.imread(SAMPLE_PATH_PREFIX + "yitulu_01.png");
		Imgproc.resize(src, src, new Size(src.cols() / 2, src.rows() / 2));

		LOGGER.debug("display source image sample");
		HighGui.imshow("source image display", src);
		HighGui.waitKey();

		// 图片灰度化
		LOGGER.debug("invoke cvtColor{...}");
		Mat gary = new Mat();
		Imgproc.cvtColor(src, gary, Imgproc.COLOR_BGR2GRAY);

		// 图像边缘处理
		LOGGER.debug("invoke Canny{...}");
		Mat edges = new Mat();
		Imgproc.Canny(gary, edges, 200, 500, 3, false);

		// 发现轮廓
		LOGGER.debug("seach and mark image border entities");
		List<MatOfPoint> list = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();

		// 查找图片对象中的轮廓
		LOGGER.debug("invoke findContours{...}");
		Imgproc.findContours(edges, list, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		// 绘制轮廓轮廓或填充轮廓
		LOGGER.debug("invoke drawContours{...}");
		Imgproc.drawContours(src, list, -1, new Scalar(0, 255, 0), Imgproc.LINE_4, Imgproc.LINE_AA);
		HighGui.imshow("marked image display", src);
		HighGui.waitKey(0);
	}
}
