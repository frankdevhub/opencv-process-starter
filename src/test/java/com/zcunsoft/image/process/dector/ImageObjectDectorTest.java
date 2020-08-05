package com.zcunsoft.image.process.dector;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcunsoft.image.process.constants.TestCaseConstants;

import junit.framework.Assert;

/**
 *
 * @Author:frankdevhub@gmail.com</br>
 * 
 * @CreateDate:2020年8月4日 下午11:50:17</br>
 * @Version: 1.0</br>
 * @Type:ImageROIParseTest.java</br>
 * @github:https://github.com/frankdevhub</br>
 * @blog:www.frankdevhub.site</br>
 *
 */

// 1.转灰度，降噪
// 2.边缘检测
// 3.轮廓提取
// 4.寻找凸包，拟合多边形
// 5.找到最大的正方形
// 6.重新执行步骤3，提升精度
// 7.找到长方形四条边，即为纸张的外围四边形
// 8.透视变换，提取四边形

public class ImageObjectDectorTest {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	// 降噪处理测试
	// 1.MeanShift滤波，降噪(效率不高)
	// 2.彩色转灰度
	// 3.高斯滤波，降噪
	private Mat testDigitalFilter(Mat src) {
		Assert.assertNotNull("cannot find src", src);

		LOGGER.info("testDigitalFilter start");
		LOGGER.info("loading test sample source image");

		// 缩减50%比例进行显示
		Imgproc.resize(src, src, new Size(src.cols() / 2, src.rows() / 2));
		LOGGER.info("display source image sample");
		HighGui.imshow("source", src);
		HighGui.waitKey();

		// 降噪处理
		Mat filtered = new Mat();
		// Imgproc.pyrMeanShiftFiltering(src, filter, 30, 10);// MeanShift
		Imgproc.cvtColor(src, filtered, Imgproc.COLOR_BGR2GRAY);
		// 彩色转换为灰度COLOR_BGR2GRAY
		// /Imgproc.GaussianBlur(src, filter, new Size(3, 3), 2, 2);

		LOGGER.info("display filtered image sample");

		HighGui.imshow("filter", filtered);
		HighGui.waitKey();
		return filtered;
	}

	// 物体边缘检测
	// 1.Canny边缘检测
	// 2.膨胀，连接边缘
	private Mat testBorderDector(Mat src) {
		LOGGER.info("testDigitalFilter start");
		Assert.assertNotNull("cannot find src", src);
		// 构造返回结果矩阵
		Imgproc.Canny(src, src, 20, 60, 3, false);// Canny边缘检测
		Imgproc.dilate(src, src, new Mat(), new Point(-1, -1), 3, 1, new Scalar(1)); // 膨胀，连接边缘
		HighGui.imshow("bordered", src);
		return src;
	}

	// 轮廓检测
	private Mat testCounterDector(Mat src) {
		LOGGER.info("testCounterDector start");
		Assert.assertNotNull("cannot find src", src);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(src, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE); // RETR_EXTERNAL&CHAIN_APPROX_SIMPLE
		return src;
	}

	// 寻找凸包拟合多边形

	// 简单规则物体的检测流程测试
	@Test
	public void testDectorFlow() {
		LOGGER.info("testDectorFlow start");
		// 加载测试用例图片
		Mat src = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "mask_example.jpg");
		Assert.assertNotNull("cannot find src image", src);

		// 降噪处理,二次膨胀色差增强突出边界线条
		Mat filtered = testDigitalFilter(src);
		Mat bordered = testBorderDector(filtered);
		Assert.assertNotNull("cannot find bordered", bordered);

		// 物体边缘检测
		Mat countered = testCounterDector(bordered);

		HighGui.imshow("countered", countered);
		HighGui.waitKey();
	}

}
