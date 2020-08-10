package com.zcunsoft.image.cases;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.zcunsoft.image.process.constants.TestCaseConstants;

/**
 * All rights Reserved, Designed By www.frankdevhub.site
 * 
 * @Title: FindContoursExample.java
 * @Package com.zcunsoft.image.cases
 * @Description: TODO
 * @author: frankdevhub@gmail.com
 * @date: 2020年8月9日 下午5:29:15
 * @version V1.0
 * @Copyright: 2020 www.frankdevhub.site Inc. All rights reserved.
 * 
 */
public class FindContoursExample {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	/***
	 * image:8位单通道图像。<br/>
	 * contours：存储检测到的轮廓的集合。<br/>
	 * hierarchy：可选的输出向量，包含了图像轮廓的拓扑信息。<br/>
	 * mode：轮廓检索模式。有如下几种模式：<br/>
	 * RETR_EXTERNAL只检测最外围的轮廓<br/>
	 * RETR_LIST提取所有的轮廓,不建立上下等级关系,只有兄弟等级关系<br/>
	 * RETR_CCOMP提取所有轮廓,建立为双层结构<br/>
	 * RETR_TREE提取所有轮廓,建立网状结构<br/>
	 * 
	 * method：轮廓的近似方法。取值如下:<br/>
	 * CHAIN_APPROX_NONE获取轮廓的每一个像素,像素的最大间距不超过1 <br/>
	 * CHAIN_APPROX_SIMPLE压缩水平垂直对角线的元素,只保留该方向的终点坐标(也就是说一条中垂线a-b,中间的点被忽略了)<br/>
	 * CHAIN_APPROX_TC89_L1使用TEH_CHAIN逼近算法中的LI算法 <br/>
	 * CHAIN_APPROX_TC89_KCOS使用TEH_CHAIN逼近算法中的KCOS算法 offset：每个轮廓点的可选偏移量<br/>
	 ***/

	@Test
	public void testFindContours() {
		// yitulu_01.png
		// test_20200806231643.png 《父辈的旗帜硫磺岛》 卷曲面测试用例
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "test_20200806231643.png");
		Mat dst = src.clone();
		Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGRA2GRAY);
		Imgproc.adaptiveThreshold(dst, dst, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 3, 3);

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1,
				new Point(0, 0));

		LOGGER.info("contours.size()  =" + contours.size());
		for (int i = 0; i < contours.size(); i++) {
			Imgproc.drawContours(src, contours, i, new Scalar(0, 0, 0, 0), 1);
		}

		Assert.notNull(dst, "cannot dst image");
		HighGui.imshow("find contours exmaple", dst);
		HighGui.waitKey();
	}
}
