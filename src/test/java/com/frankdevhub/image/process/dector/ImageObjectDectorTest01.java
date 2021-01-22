package com.frankdevhub.image.process.dector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.frankdevhub.image.process.constants.TestCaseConstants;
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
import org.opencv.utils.Converters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * All rights Reserved, Designed By www.frankdevhub.site
 *
 * @version V1.0
 * @Title: ImageObjectDectorTest01.java
 * @Package com.frankdevhub.image.process.dector
 * @Description: TODO
 * @author: frankdevhub@gmail.com
 * @date: 2020年8月5日 下午18:01:47
 * @Copyright: 2020 www.frankdevhub.site Inc. All rights reserved.
 */
public class ImageObjectDectorTest01 {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // 根据三个点计算中间那个点的夹角 pt1 pt0 pt2
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

    // 找到最大的正方形轮廓
    private int findLargestSquare(List<MatOfPoint> squares) {
        LOGGER.info("invoke findLargestSquare{...}");
        if (squares.size() == 0) {
            LOGGER.info("max_square_idx = -1");
            return -1;
        }
        int max_width = 0;
        int max_height = 0;
        int max_square_idx = 0;
        int currentIndex = 0;
        for (MatOfPoint square : squares) {
            Rect rectangle = Imgproc.boundingRect(square);
            if (rectangle.width >= max_width && rectangle.height >= max_height) {
                max_width = rectangle.width;
                max_height = rectangle.height;
                max_square_idx = currentIndex;
            }
            currentIndex++;
        }
        LOGGER.info("max_square_idx = " + max_square_idx);
        return max_square_idx;
    }

    // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    @Test
    public void testObjectDectorExample() {
        LOGGER.info("load image");
        // ticket.jpg; //矩形发票测试用例
        // bank_card.jpg //桌面圆角银行卡测试用例
        // book_02.jpg //书本测试用例
        // painting_example.jpg //墙面悬挂油画测试用例
        // painting_example01.jpg //墙面悬挂油画测试用例
        // painting_example02.jpg //墙面悬挂油画测试用例
        // painting_example03.jpg //墙面悬挂油画测试用例
        // yitulu_01.png //艺图录后台管理系统中浏览的测试用例
        // 52ddf260680df5f12428317f42ccb69.jpg 书本拍摄图片测试案例
        Mat img = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "52ddf260680df5f12428317f42ccb69.jpg");
        // namedWindow("gray_src",0);防止图片显示过大撑满屏幕导致画面失真
        // 0=true可压缩 1=false不可压缩
        HighGui.namedWindow("source image", 0);
        HighGui.imshow("source image", img);

        double fscale = 0.3; // 设定一个缩放的比例
        Size outSize = new Size();
        outSize.width = img.cols() * fscale;
        outSize.height = img.rows() * fscale;
        // 按照比例裁剪
        Imgproc.resize(img, img, outSize, 0, 0, Imgproc.INTER_AREA);
        LOGGER.info("source image width = " + img.size().width);
        LOGGER.info("source image height = " + img.size().height);

        HighGui.imshow("resized image", img);
        HighGui.waitKey();
        Mat greyImg = img.clone();
        // 彩色转灰色
        /*
         * Imgproc.cvtColor(img, greyImg, Imgproc.COLOR_BGR2GRAY);
         * HighGui.imshow("greyImg", greyImg); HighGui.waitKey();
         */
        Mat gaussianBlurImg = greyImg.clone();
        // 高斯滤波，降噪
        Imgproc.GaussianBlur(greyImg, gaussianBlurImg, new Size(3, 3), 2, 2);
        HighGui.imshow("gaussianBlurImg", gaussianBlurImg);
        HighGui.waitKey();
        Mat cannyImg = gaussianBlurImg.clone();
        // Canny边缘检测
        Imgproc.Canny(gaussianBlurImg, cannyImg, 80, 240, 3, false);
        HighGui.imshow("cannyImg", cannyImg);
        HighGui.waitKey();
        // 膨胀，连接边缘
        Mat dilateImg = cannyImg.clone();
        Imgproc.dilate(cannyImg, dilateImg, new Mat(), new Point(-1, -1), 3, 1, new Scalar(1));
        HighGui.imshow("dilateImg", dilateImg);
        HighGui.waitKey();
        // 对边缘检测的结果图再进行轮廓提取
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        List<MatOfPoint> drawContours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        // TODO:灰度明显的情况下灵敏度需要增加
        // Imgproc.adaptiveThreshold(dilateImg, dilateImg, 255,
        // Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV,
        // 3, 3);
        Imgproc.findContours(dilateImg, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat linePic = Mat.zeros(dilateImg.rows(), dilateImg.cols(), CvType.CV_8UC3);
        HighGui.imshow("linePic", linePic);
        HighGui.waitKey();
        // 找出轮廓对应凸包的四边形拟合
        List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
        List<MatOfPoint> hulls = new ArrayList<MatOfPoint>();
        MatOfInt hull = new MatOfInt();
        MatOfPoint2f approx = new MatOfPoint2f();
        approx.convertTo(approx, CvType.CV_8UC3); // CV_32F?? CV_8UC3??

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
        // 找出最大的矩形
        int index = findLargestSquare(squares);
        MatOfPoint largest_square = squares.get(index);
        Mat polyPic = Mat.zeros(img.size(), CvType.CV_8UC3);
        Imgproc.drawContours(polyPic, squares, index, new Scalar(0, 0, 255), 2);
        // 存储矩形的四个凸点
        hull = new MatOfInt();
        Imgproc.convexHull(largest_square, hull, false);
        List<Integer> hullList = hull.toList();
        List<Point> polyContoursList = largest_square.toList();
        List<Point> hullPointList = new ArrayList<Point>();
        List<Point> lastHullPointList = new ArrayList<Point>();

        for (int i = 0; i < hullList.size(); i++) {
            Imgproc.circle(polyPic, polyContoursList.get(hullList.get(i)), 10,
                    new Scalar(r.nextInt(255), r.nextInt(255), r.nextInt(255), 3));
            hullPointList.add(polyContoursList.get(hullList.get(i)));
            LOGGER.info("hullList[" + i + "] = " + hullList.get(i).toString());
        }
        // 对获取的线条对象进行加权处理
        Core.addWeighted(polyPic, 0.5, img, 0.5, 0, img);
        for (int i = 0; i < hullPointList.size(); i++) {
            lastHullPointList.add(hullPointList.get(i));
        }
        // dstPoints储存的是变换后各点二维坐标
        // imgPoints储存的是上面得到的四个角的坐标
        // TODO 假设是多边形的情况下?
        Point[] dstPoints = {new Point(0, 0), new Point(img.cols(), 0), new Point(img.cols(), img.rows()),
                new Point(0, img.rows())};
        Point[] imgPoints = new Point[4];

        // 图像锁定的面积最大的四边形的顶点坐标
        boolean sorted = false;
        // 粗略估计为目标多边形的边界数量的粗略值
        // 由相似度无限逼近获取的结果值定义n为多边形面积最大概率出现的边树
        int n = 4;
        // 对四个点进行排序 分出左上 右上 右下 左下
        LOGGER.info("lastHullPointList size() = " + n);
        while (!sorted) {
            for (int i = 1; i < lastHullPointList.size(); i++) {
                sorted = true;
                LOGGER.info("i = " + i + "\t,n = " + n);
                try {
                    if (lastHullPointList.get(i - 1).x > lastHullPointList.get(i).x) {
                        Point tempp1 = lastHullPointList.get(i);
                        Point tempp2 = lastHullPointList.get(i - 1);
                        lastHullPointList.set(i, tempp2);
                        lastHullPointList.set(i - 1, tempp1);
                        sorted = false;
                    }
                } catch (Exception e) {
                    // TODO
                    // java.lang.ArrayIndexOutOfBoundsException: -1
                    // 05:11:39,557 [main] ERROR
                    // com.frankdevhub.image.process.dector.ImageObjectDectorTest01
                    // - error: i = 0 ,n = 4
                    e.printStackTrace();
                    LOGGER.error("error:\t" + "i = " + i + "\t,n = " + n);
                    break;
                }
            }
            n--;
        }
        // 即先对四个点的x坐标进行冒泡排序分出左右，再根据两对坐标的y值比较分出上下
        if (lastHullPointList.get(0).y < lastHullPointList.get(1).y) {
            imgPoints[0] = lastHullPointList.get(0);
            imgPoints[3] = lastHullPointList.get(1);
        } else {
            imgPoints[0] = lastHullPointList.get(1);
            imgPoints[3] = lastHullPointList.get(0);
        }
        if (lastHullPointList.get(2).y < lastHullPointList.get(3).y) {
            imgPoints[1] = lastHullPointList.get(2);
            imgPoints[2] = lastHullPointList.get(3);
        } else {
            imgPoints[1] = lastHullPointList.get(3);
            imgPoints[2] = lastHullPointList.get(2);
        }
        LOGGER.info("print imgPoints elements");
        getPointGridValues(imgPoints);

        List<Point> listSrcs = Arrays.asList(imgPoints[0], imgPoints[1], imgPoints[2], imgPoints[3]);
        Mat imgPointsMat = Converters.vector_Point_to_Mat(listSrcs, CvType.CV_32F);

        List<Point> dstSrcs = Arrays.asList(dstPoints[0], dstPoints[1], dstPoints[2], dstPoints[3]);
        Mat dstPointsMat = Converters.vector_Point_to_Mat(dstSrcs, CvType.CV_32F);
        // 参数分别为输入输出图像、变换矩阵、大小。
        // 坐标变换后就得到了我们要的最终图像。
        Mat transMat = Imgproc.getPerspectiveTransform(imgPointsMat, dstPointsMat); // 得到变换矩阵
        Mat outPic = new Mat();

        LOGGER.info("handled image width = " + img.size().width);
        LOGGER.info("handled image height = " + img.size().height);
        Imgproc.warpPerspective(img, outPic, transMat, img.size());

        HighGui.imshow("border dector processed", outPic);
        HighGui.waitKey();
        LOGGER.info("transform process complete");
    }

    // 输出多边形对象多个特征点的二维坐标位置
    private void getPointGridValues(Point[] points) {

        LOGGER.info("invoke getPointGridValues{...}");
        LOGGER.info("print point element grid values");

        for (Point p : points) {
            double x_value = p.x;
            double y_value = p.y;
            Assert.isTrue(x_value >= 0, "x_value should not less than zero");
            Assert.isTrue(y_value >= 0, "y_value should not less than zero");

            LOGGER.info("point array element x  = " + x_value + ", y = " + y_value + "");
        }
    }
}
