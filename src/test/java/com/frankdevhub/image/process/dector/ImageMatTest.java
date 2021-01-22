package com.frankdevhub.image.process.dector;

import com.frankdevhub.image.process.constants.TestCaseConstants;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @Author:frankdevhub@gmail.com</br>
 * 
 * @CreateDate:2020年8月4日 下午4:04:12</br>
 * @Version: 1.0</br>
 * @Type:ImageMatTest.java</br>
 * @github:https://github.com/frankdevhub</br>
 * @blog:www.frankdevhub.site</br>
 *
 */
public class ImageMatTest {

	@Test
	public void testImageMat() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// 第一种创建方法 create
		/*
		 * Mat src = new Mat(); src.create(300, 300, CvType.CV_8UC3);
		 * src.setTo(new Scalar(0, 0, 255));
		 * Imgcodecs.imwrite(".\\screenshot\\mat.jpg", src);
		 */
		// 第二种创建方法 直接初始化
		// zeros:0黑色 ones:1黑色 CV_8UC1:单通道 CV_8UC3:三通道
		/*
		 * Mat src = Mat.zeros(300, 300, CvType.CV_8UC1);
		 * Imgcodecs.imwrite(".\\screenshot\\mat.jpg", src);
		 */

		// 类型与大小读取
		Mat src = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "nini.jpg", Imgcodecs.IMREAD_GRAYSCALE);
		int type = src.type();
		int width = src.cols();
		int heigth = src.rows();
		int channels = src.channels();
		int depth = src.depth();

		System.out.println("type:" + type);
		System.out.println("width:" + width);
		System.out.println("heigth:" + heigth);
		System.out.println("channels:" + channels);
		System.out.println("depth:" + depth);
		if (CvType.CV_8UC3 == type) {
			System.out.println("type is CV_8UC3");
		} else if (CvType.CV_8UC1 == type) {
			System.out.println("type is CV_8UC1");
		} else {
			System.out.println("UnKnow Image Type");
		}

		// Mat类型转换 变换成浮点数图像
		Mat dst = new Mat(src.size(), CvType.CV_32FC1);
		src.convertTo(dst, CvType.CV_32F);
		// 读取与修改每个像素值: 1、一次一个一个像素取 2、一次全部取出来
		/*
		 * byte[] onepixel = new byte[channels];//一次一个一个像素取,很耗时 int r = 0, g =
		 * 0, b = 0; int gray = 0; for (int row = 0; row < heigth; row++) { for
		 * (int col = 0; col < width; col++) { src.get(row, col, onepixel); if
		 * (channels == 3) {//3通道 b = onepixel[0]&0xff; g = onepixel[1]&0xff; r
		 * = onepixel[2]&0xff;
		 * 
		 * //修改 b = 255 - b; g = 255 - g; r = 255 - r; //修改后，放进去 onepixel[0] =
		 * (byte)b; onepixel[1] = (byte)g; onepixel[2] = (byte)r; } else {//单通道
		 * gray = onepixel[0]&0xff; gray = 255 - gray; onepixel[0] = (byte)gray;
		 * } src.put(row, col, onepixel); } }
		 */

		byte[] data = new byte[channels * width * heigth];// 一次全部取出来
		src.get(0, 0, data);
		int r = 0, g = 0, b = 0;
		int gray = 0;
		for (int row = 0; row < heigth; row++) {
			for (int col = 0; col < width; col++) {
				if (channels == 3) {
					b = data[row * channels * width + col * channels] & 0xff;
					g = data[row * channels * width + col * channels + 1] & 0xff;
					r = data[row * channels * width + col * channels + 2] & 0xff;
					b = 255 - b;
					g = 255 - g;
					r = 255 - r;
					data[row * channels * width + col * channels] = (byte) b;
					data[row * channels * width + col * channels + 1] = (byte) g;
					data[row * channels * width + col * channels + 2] = (byte) r;
				} else {
					gray = data[row * channels * width + col * channels] & 0xff;
					gray = 255 - gray;
					data[row * channels * width + col * channels] = (byte) gray;
				}
			}

		}
		src.put(0, 0, data);
		Imgcodecs.imwrite(TestCaseConstants.SAMPLE_PATH_PREFIX + "border_test01.jpg", src);
		src.release();
		dst.release();
	}
}
