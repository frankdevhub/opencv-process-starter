package com.zcunsoft.image.cases;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
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
 * @Title: TransMarkedAreaExample.java
 * @Package com.zcunsoft.image.cases
 * @Description:
 * @author: frankdevhub@gmail.com
 * @date: 2020年8月8日 下午9:02:31
 * @version V1.0
 * @Copyright: 2020 www.frankdevhub.site Inc. All rights reserved.
 * 
 */
public class TransMarkedAreaExample {

	/**
	 * for (Rect rect : faceDetections.toArray())
	 * 
	 * {
	 * 
	 * Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
	 * 
	 * //Get ROI
	 * 
	 * Mat imageROI = mat.submat(rectCrop);
	 * 
	 * //Move this declaration to onCameraViewStarted
	 * 
	 * Mat bw = new Mat();
	 * 
	 * //Use Imgproc.COLOR_RGB2GRAY for 3 channel image.
	 * 
	 * Imgproc.cvtColor(imageROI, bw, Imgproc.COLOR_RGBA2GRAY);
	 * 
	 * Imgproc.cvtColor(bw, imageROI, Imgproc.COLOR_GRAY2RGBA);
	 * 
	 * }
	 ***/

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private final Logger LOGGER = LoggerFactory.getLogger(TransMarkedAreaExample.class);

	  //09:37:32,861 [main] INFO  com.zcunsoft.image.process.dector.ImageObjectDectorTest01  - point array element x  = 489.0, y = 86.0
	  //09:37:32,861 [main] INFO  com.zcunsoft.image.process.dector.ImageObjectDectorTest01  - point array element x  = 862.0, y = 86.0
	  //09:37:32,861 [main] INFO  com.zcunsoft.image.process.dector.ImageObjectDectorTest01  - point array element x  = 863.0, y = 471.0
	  //09:37:32,861 [main] INFO  com.zcunsoft.image.process.dector.ImageObjectDectorTest01  - point array element x  = 489.0, y = 472.0
	  //09:37:32,862 [main] INFO  com.zcunsoft.image.process.dector.ImageObjectDectorTest01  - handled image width = 1343.0
	  //09:37:32,862 [main] INFO  com.zcunsoft.image.process.dector.ImageObjectDectorTest01  - handled image height = 566.0
	
	@Test
	public void testSubmatImage() {
		// yitulu_01.png
		LOGGER.info("invoke testSubmatImage{...}");
		Mat img = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "yitulu_01.png");
		Assert.notNull(img, "cannot find img");

		double fscale = 0.8;
		Size outSize = new Size();
		outSize.width = img.cols() * fscale;
		outSize.height = img.rows() * fscale;
		Imgproc.resize(img, img, new Size(outSize.width, outSize.height), 0, 0, Imgproc.INTER_AREA);
		LOGGER.info("source image width = " + img.size().width);
		LOGGER.info("source image height = " + img.size().height);

		HighGui.imshow("source image", img);
		HighGui.waitKey();

		LOGGER.info("invoke submat function with rectangle grid values");
		double x = 489.0;
		double y = 86.0;
		int width = (int) (862.0 - 489.0);
		int height = (int) (472.0 - 86.0);
		Rect rectCrop = new Rect();
		rectCrop.x = (int) x;
		rectCrop.y = (int) y;
		rectCrop.width = width;
		rectCrop.height = height;

		Mat imageROI = img.submat(rectCrop);
		HighGui.imshow("submat imageROI", imageROI);
		HighGui.waitKey();

	}

}
