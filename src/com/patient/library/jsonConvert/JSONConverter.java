package com.patient.library.jsonConvert;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/************************************************
 * <dl>
 * <dt>JSONConverter.java</dt>
 * <dd>Description:JSON互转通用工具</dd>
 * 
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医药报社</dd>
 * <dd>CreateDate: 2013-5-7</dd>
 * </dl>
 * 
 * @author lihs
 ************************************************/
public class JSONConverter {
	
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();
	
	
	/**
	 * JSON字符解析成Map
	 * @param json
	 * @return
	 */
	public static Map convertToMap(String json) {
		return gson.fromJson(json, Map.class);
	}
	
	/**
	 *  JSON字符解析成List
	 * @param json
	 * @return
	 */
	public static List convertToArray(String json){
		return gson.fromJson(json, List.class);
	}
	
	/**
	 * JSON字符解析成<T>对象
	 * @param <T>
	 * @param json
	 * @param clazz 对象类型
	 * @return
	 */
	public static <T> T convertToObject(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}
	
	/**
	 * JSON字符解析成<T>对象
	 * @param <T>
	 * @param json
	 * @param typeToken 对象类型，参数实例化方法：new TypeToken<TestVO>(){}
	 * @return
	 */
	public static <T> T convertToObject(String json, TypeToken<T> typeToken) {
		return gson.fromJson(json, typeToken.getType());
	}
	
	/**
	 * JSON字符解析成List<T>对象
	 * @param <T>
	 * @param json
	 * @param typeToken 对象类型，参数实例化方法：new TypeToken<List<TestVO>>(){}
	 * @return
	 */
	public static <T> List<T> convertToArray(String json, TypeToken<List<T>> typeToken){
		return gson.fromJson(json, typeToken.getType());
	}
	
	/**
	 * 对象转换层JSON字符
	 * @param src
	 * @return
	 */
	public static String convertToString(Object src) {
		return gson.toJson(src);
	}
}
