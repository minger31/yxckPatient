/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.patient.library.http;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.patient.library.http.callback.StringDownloadHandler;
import com.patient.util.LogUtil;

public class SyncHttpHandler {

    private final AbstractHttpClient client;
    private final HttpContext context;
    private final StringDownloadHandler mStringDownloadHandler = new StringDownloadHandler();

    private int executionCount = 0;
    private String charset;

    public SyncHttpHandler(AbstractHttpClient client, HttpContext context, String charset) {
        this.client = client;
        this.context = context;
        this.charset = charset;
    }

    private SyncResult makeRequestWithRetries(HttpRequestBase request) throws IOException {

        boolean retry = true;
        IOException ioException = null;
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        SyncResult mSyncResult = new SyncResult();
        while (retry) {
            try {
                HttpResponse response = client.execute(request, context);
                Object responseBody = mStringDownloadHandler.handleEntity(response.getEntity(), null, charset);
              //SUNJIAN ADD 令牌验证失败的时候进行登录处�?               
                if (!TextUtils.isEmpty(responseBody.toString())) {
					JSONObject json = null;
					try {
					    
						json = new JSONObject(responseBody.toString());
					} catch (JSONException e) {
//					 
					}
					if (json != null) {
//						if (CommonConstant.ACCESSTOKEN_INVALID.equals(json.optString("status"))) {
							LogUtil.e("授权令牌（accessToken）无效，�?��重新登录");
//							if (TextUtils.isEmpty(SyncLoginAction.getAccessTokenID())) {
//								LogUtil.e("授权令牌（accessToken）无效，再次获取也失�?);
//							} else {
//								LogUtil.d("重新登录成功");
//				            	String tokenId = NetPhoneApplication.getPreference().getKeyValue(PrefType.LOGIN_ACCESSTOKENID, "");
//				            	LogUtil.d("accessToken=" + tokenId);
//				            	((HttpRequest)request).getUriBuilder().setParameter("accessToken", tokenId);
//								throw new AccessTokenException("授权令牌（accessToken）无�?);
//							}
//							
//						}
					}
                }
                mSyncResult.setOK(true);
                mSyncResult.setResult(responseBody+"");
                return mSyncResult;
            } catch (UnknownHostException e) {
                ioException = e;
                retry = retryHandler.retryRequest(ioException, ++executionCount, context);
                //suanjian add
            } catch(AccessTokenException e){
            	//授权令牌（accessToken）无�?//              
            	retry = retryHandler.retryRequest(e, ++executionCount, context);
                retry = true;
            } catch (IOException e) {
                ioException = e;
                retry = retryHandler.retryRequest(ioException, ++executionCount, context);
            } catch (NullPointerException e) {
                ioException = new IOException("NPE in HttpClient" + e.getMessage());
                retry = retryHandler.retryRequest(ioException, ++executionCount, context);
            } catch (Exception e) {
                ioException = new IOException("Exception" + e.getMessage());
                retry = retryHandler.retryRequest(ioException, ++executionCount, context);
            }
        }
        if (ioException != null) {
            throw ioException;
        } else {
            throw new IOException("未知网络错误");
        }

    }

	public SyncResult sendRequest(HttpRequestBase... params) {

		try {
            return makeRequestWithRetries(params[0]);
			
		} catch (IOException e) {
			e.printStackTrace();
			SyncResult mSyncResult = new SyncResult();
			if (e instanceof ConnectTimeoutException
					|| e instanceof SocketTimeoutException
					|| e instanceof SocketException) {
				mSyncResult.setOK(false);
				mSyncResult.setErrorCode(-200);
			}
			
			if (e instanceof ClientProtocolException) {
				mSyncResult.setOK(false);
				mSyncResult.setErrorCode(-300);
			}
			
			return mSyncResult;

		}
	}

}
