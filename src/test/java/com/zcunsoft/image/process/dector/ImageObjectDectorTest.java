package com.zcunsoft.image.process.dector;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
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
	private MatOfPoint2f approx = new MatOfPoint2f();
	private List<MatOfPoint> hulls = new ArrayList<MatOfPoint>();
	private Integer rectAreaIndex = -1;
	private List<Point> corners = new ArrayList<Point>(); // 多条线条对象相交的定点的集合
	private List<Point> newPointList = new ArrayList<Point>();
	private MatOfPoint result = null; // 目标图像内面积最大的矩形坐标属性

	private double maxL = -1;

	// 降噪处理测试
	// 1.MeanShift滤波，降噪(效率不高)
	// 2.彩色转灰度
	// 3.高斯滤波，降噪
	private Mat imageDigitalFilter(Mat src) {
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
	private Mat imageBorderDector(Mat src) {
		Assert.assertNotNull("cannot find src", src);
		// 构造返回结果矩阵
		Imgproc.Canny(src, src, 40, 120, 3, false);// Canny边缘检测
		Imgproc.dilate(src, src, new Mat(), new Point(-1, -1), 3, 1, new Scalar(1)); // 膨胀，连接边缘
		HighGui.imshow("bordered", src);
		return src;
	}

	// 根据三个点计算中间那个点的夹角 pt1 pt0 pt2
	private static double getAngle(Point pt1, Point pt2, Point pt0) {
		double dx1 = pt1.x - pt0.x;
		double dy1 = pt1.y - pt0.y;
		double dx2 = pt2.x - pt0.x;
		double dy2 = pt2.y - pt0.y;
		return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
	}

	// 轮廓检测
	private List<MatOfPoint> imageCounterDector(Mat src) {
		Assert.assertNotNull("cannot find src", src);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(src, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE); // RETR_EXTERNAL&CHAIN_APPROX_SIMPLE

		HighGui.imshow("countered", src);
		HighGui.waitKey();
		return contours;
	}

	// 寻找凸包拟合多边形
	private List<MatOfPoint> approxPolygon(List<MatOfPoint> contours) throws Exception {
		Assert.assertNotNull("cannot find contours", contours);
		// 找出轮廓对应凸包的四边形拟合
		List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
		List<MatOfPoint> hulls = new ArrayList<MatOfPoint>();
		MatOfInt hull = new MatOfInt();
		MatOfPoint2f approx = new MatOfPoint2f();
		approx.convertTo(approx, CvType.CV_32F);
		this.approx = approx;

		for (MatOfPoint contour : contours) {
			Imgproc.convexHull(contour, hull);// 获取多边形的凸包结构对象
			Point[] contourPoints = contour.toArray();// 凸包结构对象获取新的轮廓点
			int[] indices = hull.toArray();
			List<Point> newPoints = new ArrayList<Point>();
			for (int index : indices) {
				newPoints.add(contourPoints[index]);
			}
			MatOfPoint2f contourHull = new MatOfPoint2f();
			contourHull.fromList(newPoints);
			// 多边形拟合凸包边框
			Imgproc.approxPolyDP(contourHull, approx, Imgproc.arcLength(contourHull, true) * 0.02, true);
			// 筛选出面积大于某一阈值的，且四边形的各个角度都接近直角的凸四边形
			MatOfPoint approxf1 = new MatOfPoint();
			approx.convertTo(approxf1, CvType.CV_32S);
			// TODO
			LOGGER.info("approx rows = " + approx.rows() + "\tapprox cols = " + approx.cols() + ",contourArea = "
					+ Math.abs(Imgproc.contourArea(approx)) + ", convex = " + Imgproc.isContourConvex(approxf1));
			if (approx.rows() == 4 && Math.abs(Imgproc.contourArea(approx)) > 40000
					&& Imgproc.isContourConvex(approxf1)) {
				double maxCosine = 0;
				for (int j = 2; j < 5; j++) {
					double cosine = Math.abs(
							getAngle(approxf1.toArray()[j % 4], approxf1.toArray()[j - 2], approxf1.toArray()[j - 1]));
					maxCosine = Math.max(maxCosine, cosine);
				}
				LOGGER.info("transform angle");
				if (maxCosine < 0.3) {
					MatOfPoint tmp = new MatOfPoint();
					contourHull.convertTo(tmp, CvType.CV_32S);
					squares.add(approxf1);
					hulls.add(tmp);
				}
			}
		}
		this.hulls = hulls;
		LOGGER.info("hulls size = " + this.hulls.size());
		return hulls;
	}

	// 找到离散点的集合中的正方形区域
	private MatOfPoint findLargestSquare(List<MatOfPoint> squares) throws Exception {
		Assert.assertNotNull("cannot find squares", squares);
		int max_width = 0;
		int max_height = 0;
		int max_square_idx = 0;
		int currentIndex = 0;
		if (squares.size() == 0) {
			throw new Exception("squares size cannot be zero");
		} else {
			for (MatOfPoint square : squares) {
				Rect rectangle = Imgproc.boundingRect(square);
				// 遍历获取最大的矩形特征对象
				if (rectangle.width >= max_width && rectangle.height >= max_height) {
					max_width = rectangle.width;
					max_height = rectangle.height;
					max_square_idx = currentIndex;
				}
				currentIndex++;
			}
			MatOfPoint result = squares.get(max_square_idx);
			Assert.assertNotNull("cannot find result", result);
			LOGGER.info("result is null = " + Boolean.valueOf(null == result));
			LOGGER.info("result rows  =" + result.rows());
			LOGGER.info("result cols = " + result.cols());
			if (result.rows() != 0 || result.cols() != 0) {
				this.result = result;
				this.rectAreaIndex = max_square_idx;
				return result;
			}
			return result;
		}
	}

	// 寻找矩形的四条边,直线轮廓检测
	// 点到点的距离
	private double getSpacePointToPoint(Point p1, Point p2) {
		double a = p1.x - p2.x;
		double b = p1.y - p2.y;
		return Math.sqrt(a * a + b * b);
	}

	// 两直线的交点
	private Point computeIntersect(double[] a, double[] b) {
		if (a.length != 4 || b.length != 4)
			throw new ClassFormatError();
		double x1 = a[0], y1 = a[1], x2 = a[2], y2 = a[3], x3 = b[0], y3 = b[1], x4 = b[2], y4 = b[3];
		double d = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
		if (d != 0) {
			Point pt = new Point();
			pt.x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
			pt.y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
			return pt;
		} else
			return new Point(-1, -1);
	}

	// newPointList size = 0
	// corners size = 0
	private void getAreaLineCorners() {
		// 找到高精度拟合时得到的顶点中 距离小于低精度拟合得到的四个顶点maxL的顶点，排除部分顶点的干扰
		Assert.assertNotNull("cannot find result", this.result);
		LOGGER.info("result size = " + result.size());
		LOGGER.info("approx array = " + approx.toArray().length);
		LOGGER.info("newPointList size = " + newPointList.size());
		for (Point p : approx.toArray()) {
			if (!(getSpacePointToPoint(p, this.result.toList().get(0)) > maxL
					&& getSpacePointToPoint(p, this.result.toList().get(1)) > maxL
					&& getSpacePointToPoint(p, this.result.toList().get(2)) > maxL
					&& getSpacePointToPoint(p, this.result.toList().get(3)) > maxL)) {
				newPointList.add(p); // 二次矫正膨胀之后最新的边界分割点集合
			}
		}
		// 找到剩余顶点连线中，边长大于 2 * maxL的四条边作为四边形物体的四条边
		List<double[]> lines = new ArrayList<double[]>();
		for (int i = 0; i < newPointList.size(); i++) {
			Point p1 = newPointList.get(i);
			Point p2 = newPointList.get((i + 1) % newPointList.size());
			if (getSpacePointToPoint(p1, p2) > 2 * maxL) {
				lines.add(new double[] { p1.x, p1.y, p2.x, p2.y });
			}
		}
		LOGGER.info("lines size = " + lines.size());
		// 计算出这四条边中 相邻两条边的交点，即物体的四个顶点
		List<Point> corners = new ArrayList<Point>();
		for (int i = 0; i < lines.size(); i++) {
			Point corner = computeIntersect(lines.get(i), lines.get((i + 1) % lines.size()));
			corners.add(corner);
		}
		LOGGER.info("corners size = " + corners.size());
		this.corners = corners;

	}

	// 对多个点按顺时针排序
	private void sortCorners(List<Point> corners) throws Exception {
		if (corners.size() == 0)
			return;
		Point p1 = corners.get(0);
		int index = 0;
		for (int i = 1; i < corners.size(); i++) {
			Point point = corners.get(i);
			if (p1.x > point.x) {
				p1 = point;
				index = i;
			}
		}
		corners.set(index, corners.get(0));
		corners.set(0, p1);
		Point lp = corners.get(0);
		for (int i = 1; i < corners.size(); i++) {
			for (int j = i + 1; j < corners.size(); j++) {
				Point point1 = corners.get(i);
				Point point2 = corners.get(j);
				if ((point1.y - lp.y * 1.0) / (point1.x - lp.x) > (point2.y - lp.y * 1.0) / (point2.x - lp.x)) {
					Point temp = point1.clone();
					corners.set(i, corners.get(j));
					corners.set(j, temp);
				}
			}
		}
		this.corners = corners;
	}

	// 在假设已经知道需要瞬时针方向旋转的情况下进行角度矫正
	private Mat transFormROIImageArea() throws Exception {
		// 校验是否含有定点的数据
		if (this.corners.size() <= 0)
			throw new Exception("corners size cannot be zero");
		// 对顶点顺时针排序
		sortCorners(corners);
		// 获取目标图像的尺寸大小
		Point p0 = corners.get(0);
		Point p1 = corners.get(1);
		Point p2 = corners.get(2);
		Point p3 = corners.get(3);
		double space0 = getSpacePointToPoint(p0, p1);
		double space1 = getSpacePointToPoint(p1, p2);
		double space2 = getSpacePointToPoint(p2, p3);
		double space3 = getSpacePointToPoint(p3, p0);

		double imgWidth = space1 > space3 ? space1 : space3;
		double imgHeight = space0 > space2 ? space0 : space2;

		// 如果提取出的图片宽小于高，则旋转90度
		if (imgWidth < imgHeight) {
			double temp = imgWidth;
			imgWidth = imgHeight;
			imgHeight = temp;
			Point tempPoint = p0.clone();
			p0 = p1.clone();
			p1 = p2.clone();
			p2 = p3.clone();
			p3 = tempPoint.clone();
		}
		Mat quad = Mat.zeros((int) imgHeight * 2, (int) imgWidth * 2, CvType.CV_8UC3);
		MatOfPoint2f cornerMat = new MatOfPoint2f(p0, p1, p2, p3);
		MatOfPoint2f quadMat = new MatOfPoint2f(new Point(imgWidth * 0.4, imgHeight * 1.6),
				new Point(imgWidth * 0.4, imgHeight * 0.4), new Point(imgWidth * 1.6, imgHeight * 0.4),
				new Point(imgWidth * 1.6, imgHeight * 1.6));
		// 提取图像
		Mat transmtx = Imgproc.getPerspectiveTransform(cornerMat, quadMat);
		Imgproc.warpPerspective(result, quad, transmtx, quad.size());
		return quad;
	}

	// ERROR:
	// junit.framework.AssertionFailedError: cannot find rectArea
	// 简单规则物体的检测流程测试
	public void testDectorFlow() throws Exception {
		// mask_example.jpg//camera_env03.jpg//bank_card.jpg
		Mat src = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "ticket.jpg");
		Assert.assertNotNull("cannot find src image", src);

		// 降噪处理,二次膨胀色差增强突出边界线条
		Mat filtered = imageDigitalFilter(src);
		Mat bordered = imageBorderDector(filtered);
		// 物体边缘检测
		List<MatOfPoint> contours = imageCounterDector(bordered);
		LOGGER.info("contours size = " + contours.size());
		Assert.assertNotNull("cannot find contours", contours);
		HighGui.imshow("border dector", bordered);
		HighGui.waitKey();
		// TODO: approxPolygon 检测矩形边界???cores = []
		List<MatOfPoint> squares = approxPolygon(contours);
		LOGGER.info("squares size = " + squares.size());
		Assert.assertNotNull("cannot find squares", squares);
		// 锁定离散的像素点中最大的矩阵区域面积
		// TODO: rectArea?
		MatOfPoint rectArea = findLargestSquare(squares);
		Assert.assertNotNull("cannot find rectArea", rectArea);
		// 找到这个最大的四边形对应的凸边框，再次进行多边形拟合，此次精度较高，拟合的结果可能是大于4条边的多边形
		LOGGER.info("rectAreaIndex = " + this.rectAreaIndex);

		MatOfPoint contourHull = this.hulls.get(this.rectAreaIndex);
		MatOfPoint2f tmp = new MatOfPoint2f();
		contourHull.convertTo(tmp, CvType.CV_32F);
		Imgproc.approxPolyDP(tmp, approx, 3, true);
		HighGui.imshow("border approxPolyDP", bordered);
		HighGui.waitKey();

		// TODO: corner?
		getAreaLineCorners();
		double maxL = Imgproc.arcLength(approx, true) * 0.02;
		this.maxL = maxL;

		// 获取提取后的矩阵
		// TODO:ERROR: corners = [] ?????
		Mat handled = transFormROIImageArea();
		LOGGER.info("display handled image sample");
		HighGui.imshow("handled", handled);
		HighGui.waitKey();
	}

	@Test
	public void testObjectDectorExample() throws Exception {
		new ImageObjectDectorTest().testDectorFlow();
	}

}
