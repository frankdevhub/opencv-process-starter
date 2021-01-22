/**
 *
 */
package com.frankdevhub.image.algo;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All rights Reserved, Designed By www.frankdevhub.site
 *
 * @Title: MatrixBaseExample.java
 * @Package com.frankdevhub.image.algo
 * @Description: TODO
 * @author: frankdevhub@gmail.com
 * @date: 2020年8月8日 下午6:52:00
 * @version V1.0
 * @Copyright: 2020 www.frankdevhub.site Inc. All rights reserved.
 *
 */
public class MatrixBaseTest {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // 利用函数构造特殊Mat
    // ERROR: java.lang.UnsatisfiedLinkError: org.opencv.core.Mat.n_eye(III)J
    @Test
    public void testCharacteristicMat() {
        LOGGER.info("invoke testCharacteristicMat{...}");
        // CV_8UC1 单通道
        // CV_8UC3 3通道每个矩阵元素包含3个uchar值
        Mat A = Mat.eye(3, 3, CvType.CV_8UC3); // 构造一个三阶对角矩阵
        // 若Mat A * Mat B = C且C为对角矩阵则前两者为互秩
        LOGGER.info("A_eye:");
        System.out.println(A.dump());

        A = Mat.zeros(3, 3, CvType.CV_64FC1); // 构造一个三阶零阶矩阵
        LOGGER.info("A_zero");
        System.out.println(A.dump());

    }
}
