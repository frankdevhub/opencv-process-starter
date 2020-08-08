/**
 * 
 */
package com.zcunsoft.image.process.base;

import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All rights Reserved, Designed By www.frankdevhub.site
 * 
 * @Title: ImageResizeTest.java
 * @Package com.zcunsoft.image.process.base
 * @Description: TODO
 * @author: frankdevhub@gmail.com
 * @date: 2020年8月7日 上午10:32:01
 * @version V1.0
 * @Copyright: 2020 www.frankdevhub.site Inc. All rights reserved.
 * 
 */
public class ImageResizeTest {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

}
