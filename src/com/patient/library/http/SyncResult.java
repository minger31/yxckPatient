package com.patient.library.http;

public class SyncResult {
	private boolean isOK = false;
	//服务端返回的Json
	private String result = "";
	
	private int errorCode = 0;
	private String errormsg = "";

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

	// public int getErrorCode() {
	// return errorCode;
	// }
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		if (errorCode == -100) {
			errormsg = "请求地址为空";
		} else if (errorCode == -200) {
			errormsg = "服务器连接超时";
		}else if (errorCode == -300) {
			errormsg = "数据连接异常中断";
		}
		return errormsg;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
