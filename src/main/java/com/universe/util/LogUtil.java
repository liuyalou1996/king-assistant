package com.universe.util;

import org.apache.log4j.Logger;

public class LogUtil {

	public static Logger logger = Logger.getLogger(LogUtil.class);

	public static void error(Exception e) {
		if (e != null) {
			logger.error(e.getClass().getName()+":"+e.getMessage());
			StackTraceElement[] stes = e.getStackTrace();
			for (StackTraceElement ste : stes) {
				logger.error(ste.toString());
			}
		}
	}

	public static void debug(Exception e) {
		if (e != null) {
			logger.error(e.getClass().getName()+":"+e.getMessage());
			StackTraceElement[] stes = e.getStackTrace();
			for (StackTraceElement ste : stes) {
				logger.debug(ste.toString());
			}
		}
	}

	public static void warn(Exception e) {
		if (e != null) {
			logger.error(e.getClass().getName()+":"+e.getMessage());
			StackTraceElement[] stes = e.getStackTrace();
			for (StackTraceElement ste : stes) {
				logger.warn(ste.toString());
			}
		}
	}

	public static void info(String message) {
		logger.info(message);
	}
}
