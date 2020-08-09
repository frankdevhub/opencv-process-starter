/**
 * 
 */
package com.zcunsoft.image.cases;

import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All rights Reserved, Designed By www.frankdevhub.site
 * 
 * @Title: PerspectiveTransformExample.java
 * @Package com.zcunsoft.image.cases
 * @Description: TODO
 * @author: frankdevhub@gmail.com
 * @date: 2020年8月9日 上午10:54:30
 * @version V1.0
 * @Copyright: 2020 www.frankdevhub.site Inc. All rights reserved.
 * 
 */
public class PerspectiveTransformExample {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private final Logger LOGGER = LoggerFactory.getLogger(PerspectiveTransformExample.class);

}
