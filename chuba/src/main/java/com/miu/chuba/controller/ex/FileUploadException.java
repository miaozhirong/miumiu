package com.miu.chuba.controller.ex;

/**
 * 文件上传相关异常的基类
 */
public class FileUploadException extends RuntimeException {

	private static final long serialVersionUID = -4816421833308748249L;

	public FileUploadException() {
		super();
	}

	public FileUploadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileUploadException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileUploadException(String message) {
		super(message);
	}

	public FileUploadException(Throwable cause) {
		super(cause);
	}

}
