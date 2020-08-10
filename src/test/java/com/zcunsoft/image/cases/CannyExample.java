package com.zcunsoft.image.cases;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcunsoft.image.process.constants.TestCaseConstants;

/**
 * All rights Reserved, Designed By www.frankdevhub.site
 * 
 * @Title: CannyExample.java
 * @Package com.zcunsoft.image.cases
 * @Description:
 * @author: frankdevhub@gmail.com
 * @date: 2020年8月9日 下午7:37:19
 * @version V1.0
 * @Copyright: 2020 www.frankdevhub.site Inc. All rights reserved.
 * 
 */
public class CannyExample {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	/**
	 * Canny边缘检测的步骤： <br/>
	 * 1.消除噪声，一般使用高斯平滑滤波器卷积降噪 <br/>
	 * 2.计算梯度幅值和方向，此处按照sobel滤波器步骤来操作 <br/>
	 * 3.非极大值抑制，排除非边缘像素 <br/>
	 * 4.滞后阈值（高阈值和低阈值），若某一像素位置的幅值超过高阈值，该像素被保留为边缘像素； <br/>
	 * 若小于低阈值，则被排除；若在两者之间，该像素仅在连接到高阈值像素时被保留。推荐高低阈值比在2:1和3:1之间 <br/>
	 **/

	private double getAngle(Point pt1, Point pt2, Point pt0) {
		LOGGER.info("invoke getAngle{...}");
		double dx1 = pt1.x - pt0.x;
		double dy1 = pt1.y - pt0.y;
		double dx2 = pt2.x - pt0.x;
		double dy2 = pt2.y - pt0.y;
		double angle = (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
		LOGGER.info("angle = " + angle);
		return angle;
	}

	/**
	 * Imgproc.Canny(image, edges, threshold1, threshold2, apertureSize,
	 * L2gradient)<br/>
	 * image：输入图像，即源图像，填Mat类的对象即可，且需为单通道8位图像 <br/>
	 * threshold1：双阀值抑制中的低阀值 <br/>
	 * threshold2：双阀值抑制中的高阀值 <br/>
	 * apertureSize：sobel算子模板大小，默认为3 <br/>
	 * L2gradient：计算图像梯度幅值的标识，有默认值false,梯度幅值指沿某方向的方向导数最大的值，即梯度的模 <br/>
	 *
	 **/

	// Canny多级边缘检测算法测试用例
	@Test
	public void testCannyParameters() {
		LOGGER.info("invoke testCannyParameters{....}");

		LOGGER.info("load image");
		Mat img = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "bank_card.jpg");
		double fscale = 0.3;
		Size outSize = new Size();
		outSize.width = img.cols() * fscale;
		outSize.height = img.rows() * fscale;

		Imgproc.resize(img, img, outSize, 0, 0, Imgproc.INTER_AREA);
		LOGGER.info("source image width = " + img.size().width);
		LOGGER.info("source image height = " + img.size().height);

		Mat greyImg = img.clone();
		Imgproc.cvtColor(img, greyImg, Imgproc.COLOR_BGR2GRAY);// 彩色转灰色
		Mat gaussianBlurImg = greyImg.clone();
		Imgproc.GaussianBlur(greyImg, gaussianBlurImg, new Size(3, 3), 2, 2);// 高斯滤波，降噪
		Mat cannyImg = gaussianBlurImg.clone();
		Imgproc.Canny(gaussianBlurImg, cannyImg, 90, 120, 3, false);// Canny边缘检测
		Mat dilateImg = cannyImg.clone();
		Imgproc.dilate(cannyImg, dilateImg, new Mat(), new Point(-1, -1), 3, 1, new Scalar(1));// 膨胀，连接边缘

		// 对边缘检测的结果图再进行轮廓提取
		List<MatOfPoint> drawContours = new ArrayList<MatOfPoint>();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();

		Imgproc.findContours(dilateImg, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Mat linePic = Mat.zeros(dilateImg.rows(), dilateImg.cols(), CvType.CV_8UC3);
		// 找出轮廓对应凸包的四边形拟合
		List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
		List<MatOfPoint> hulls = new ArrayList<MatOfPoint>();
		MatOfInt hull = new MatOfInt();
		MatOfPoint2f approx = new MatOfPoint2f();
		approx.convertTo(approx, CvType.CV_32F);

		for (int i = 0; i < contours.size(); i++) {
			MatOfPoint contour = contours.get(i); // 边框的凸包
			Imgproc.convexHull(contour, hull);// 用凸包计算出新的轮廓点
			Point[] contourPoints = contour.toArray();
			int[] indices = hull.toArray();
			List<Point> newPoints = new ArrayList<Point>();
			for (int index : indices) {
				newPoints.add(contourPoints[index]);
			}
			MatOfPoint2f contourHull = new MatOfPoint2f();
			contourHull.fromList(newPoints);
			// 再次进行拟合
			LOGGER.info("invoke approxPolyDP{...}");
			Imgproc.approxPolyDP(contourHull, approx, Imgproc.arcLength(contourHull, true) * 0.02, true);

			MatOfPoint mat = new MatOfPoint();
			mat.fromArray(approx.toArray());
			drawContours.add(mat);
			// 筛选出面积大于某一阈值的，且四边形的各个角度都接近直角的凸四边形
			MatOfPoint approxf1 = new MatOfPoint();
			approx.convertTo(approxf1, CvType.CV_32S);
			LOGGER.info("approx.rows() = " + approx.rows() + "\tMath.abs(Imgproc.contourArea(approx) = "
					+ Math.abs(Imgproc.contourArea(approx)));
			// 理想状态下是矩形,四边形;如果有干扰项则模糊估计为近似于矩形的多边形(4<rows<6)
			// 相似度匹配值散落区间内求出密度最高相邻相似度最高的值作为筛选的阀值

			// TODO: P1
			if (approx.rows() == 4 && Math.abs(Imgproc.contourArea(approx)) > 30000 // case:20000
					&& Imgproc.isContourConvex(approxf1)) {
				double maxCosine = 0;
				for (int j = 2; j < 5; j++) {
					double cosine = Math.abs(
							getAngle(approxf1.toArray()[j % 4], approxf1.toArray()[j - 2], approxf1.toArray()[j - 1]));
					maxCosine = Math.max(maxCosine, cosine);
				}
				LOGGER.info("maxCosine = " + maxCosine);
				// 旋转角度的粗略估计
				if (maxCosine < 0.9494) {
					MatOfPoint tmp = new MatOfPoint();
					contourHull.convertTo(tmp, CvType.CV_32S);
					squares.add(approxf1);
					hulls.add(tmp);
					LOGGER.info("insert new tmp element");
				}
			}
		}

		Random r = new Random();
		for (int i = 0; i < drawContours.size(); i++) {
			Imgproc.drawContours(linePic, drawContours, i, new Scalar(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
		}
		HighGui.imshow("linePic mark lines", linePic);
		HighGui.waitKey();
	}

}
