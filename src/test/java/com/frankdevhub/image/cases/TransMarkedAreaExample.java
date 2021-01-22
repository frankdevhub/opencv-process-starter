package com.frankdevhub.image.cases;

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

import com.frankdevhub.image.process.constants.TestCaseConstants;

/**
 * All rights Reserved, Designed By www.frankdevhub.site
 *
 * @version V1.0
 * @Title: TransMarkedAreaExample.java
 * @Package com.frankdevhub.image.cases
 * @Description:
 * @author: frankdevhub@gmail.com
 * @date: 2020年8月8日 下午9:02:31
 * @Copyright: 2020 www.frankdevhub.site Inc. All rights reserved.
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

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * <p>
     * <span>09:37:32,861 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point array
     * element x = 489.0, y = 86.0 <br/>
     * <span>09:37:32,861 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point array
     * element x = 862.0, y = 86.0 </span> <br/>
     * <span>09:37:32,861 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point array
     * element x = 863.0, y = 471.0</span> <br/>
     * <span>09:37:32,861 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point array
     * element x = 489.0, y = 472.0 </span> <br/>
     * <span>09:37:32,862 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - handled image
     * width = 1343.0 </span> <br/>
     * <span>09:37:32,862 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - handled image
     * height = 566.0 </span> <br/>
     * </p>
     */

    // 转角为0的四边形裁剪
    @Test
    public void testSubmatImage() {
        // yitulu_01.png
        // 52ddf260680df5f12428317f42ccb69.jpg
        LOGGER.info("invoke testSubmatImage{...}");
        Mat img = Imgcodecs.imread(TestCaseConstants.SAMPLE_PATH_PREFIX + "52ddf260680df5f12428317f42ccb69.jpg");
        HighGui.namedWindow("source image", 0);
        Assert.notNull(img, "cannot find img");
        HighGui.imshow("source image", img);

        double fscale = 0.3;
        Size outSize = new Size();
        outSize.width = img.cols() * fscale;
        outSize.height = img.rows() * fscale;
        Imgproc.resize(img, img, new Size(outSize.width, outSize.height), 0, 0, Imgproc.INTER_AREA);
        LOGGER.info("source image width = " + img.size().width);
        LOGGER.info("source image height = " + img.size().height);

        HighGui.imshow("source image", img);
        HighGui.waitKey();

        LOGGER.info("invoke submat function with rectangle grid values");

        /*
         * 12:19:39,404 [main] INFO
         * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - print
         * point element grid values 12:19:39,407 [main] INFO
         * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point
         * array element x = 114.0, y = 682.0 L1 12:19:39,407 [main] INFO
         * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point
         * array element x = 890.0, y = 688.0 R1 12:19:39,407 [main] INFO
         * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point
         * array element x = 909.0, y = 1221.0 12:19:39,407 [main] INFO
         * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point
         * array element x = 91.0, y = 1237.0
         */

        /*
         * double x = 489.0; double y = 86.0; int width = (int) (862.0 - 489.0);
         * int height = (int) (472.0 - 86.0);
         */
        double x = 114.0;
        double y = 682.0;
        int width = (int) (909.0 - 91.0);
        int height = (int) (1237.0 - 688.0);

        Rect rectCrop = new Rect();
        rectCrop.x = (int) x;
        rectCrop.y = (int) y;
        rectCrop.width = width;
        rectCrop.height = height;

        Mat imageROI = img.submat(rectCrop);
        HighGui.imshow("submat imageROI", imageROI);
        HighGui.waitKey();

    }

    /**
     * 10:37:00,081 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - print point
     * element grid values 10:37:00,083 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point array
     * element x = 374.0, y = 283.0 10:37:00,084 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point array
     * element x = 1028.0, y = 291.0 10:37:00,084 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point array
     * element x = 1051.0, y = 702.0 10:37:00,084 [main] INFO
     * com.frankdevhub.image.process.dector.ImageObjectDectorTest01 - point array
     * element x = 309.0, y = 676.0
     ***/

    // 转角不为0的四边形裁剪
    @Test
    public void testSubmatImage1() {

    }
}
