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

import com.zcunsoft.image.process.constants.TestCaseConstants;

/**
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
	// private final String SAMPLE_PATH_PREFIX =
	// "D:/frankdevhub-workspace/samples/";
	// private final String SAMPLE_URL =
	// "https://art-photo-oss.zcunsoft.com/data/itemZip/20200730/d7ab0ff27dab4262bab0c7f7890536ae.jpg";

	@Test
	public void imageBorderDector() {
		LOGGER.debug("imageBorderDector start");
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

		Mat src = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "cute_puppy_gray01.png");
		Imgproc.resize(src, src, new Size(src.cols() / 2, src.rows() / 2));

		LOGGER.debug("display source image sample");
		HighGui.imshow("source", src);
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
		/**
		 *
		 * RETR_EXTERNAL = 0, 只检测最外围轮廓，包含在外围轮廓内的内围轮廓被忽略； RETR_LIST =
		 * 1,检测所有的轮廓，包括内围、外围轮廓，但是检测到的轮廓不建立等级关系，彼此之间独立，没有等级关系，这就意味着这个检索模式下不存在父轮廓或内嵌轮廓
		 * RETR_CCOMP =
		 * 2,检测所有的轮廓，但所有轮廓只建立两个等级关系，外围为顶层，若外围内的内围轮廓还包含了其他的轮廓信息，则内围内的所有轮廓均归属于顶层；
		 * RETR_TREE = 3,检测所有轮廓，所有轮廓建立一个等级树结构。外层轮廓包含内层轮廓，内层轮廓还可以继续包含内嵌轮廓
		 * RETR_FLOODFILL = 4; 官方没定义 使用此参数需要把输入源转为CV_32SC1 Mat dst = new Mat();
		 * edges.convertTo(dst,CvType.CV_32SC1); CHAIN_APPROX_NONE =
		 * 1,保存物体边界上所有连续的轮廓点到contours向量内； CHAIN_APPROX_SIMPLE =
		 * 2,仅保存轮廓的拐点信息，把所有轮廓拐点处的点保存入contours向量内，拐点与拐点之间直线段上的信息点不予保留；
		 * CHAIN_APPROX_TC89_L1 = 3,使用teh-Chinl chain 近似算法;
		 * CHAIN_APPROX_TC89_KCOS = 4; 使用teh-Chinl chain 近似算法。
		 *
		 **/

		LOGGER.debug("invoke findContours{...}");
		Imgproc.findContours(edges, list, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		// 绘制轮廓轮廓或填充轮廓
		LOGGER.debug("invoke drawContours{...}");
		Imgproc.drawContours(src, list, -1, new Scalar(0, 255, 0), Imgproc.LINE_4, Imgproc.LINE_AA);
		HighGui.imshow("dst", src);
		HighGui.waitKey(0);
	}
}
