package com.tmesh.common.utils;

import java.util.List;
import java.util.Map;

/**
 * 校验工具类
 * 
 * @author TMesh
 * @since 2024-01-23 09:22
 */
public class EmptyUtils {

	/**
	 * 功能描述: list 登陆null 或 list.size ==0 返回 true
	 * 
	 * @param list
	 * @return
	 * @author TMesh 2013-8-17 2024-01-23 09:22
	 */
	public static <E> boolean isEmpty(List<E> list) {
		return (list == null || list.isEmpty());
	}

	/**
	 * 功能描述: list 登陆null 或 list.size ==0 返回 true
	 * 
	 * @param list
	 * @return
	 * @author TMesh 2024-01-23 09:22
	 */
	public static <E> boolean isNotEmpty(List<E> list) {
		return (list != null && !list.isEmpty());
	}

	/**
	 * 功能描述: map 登陆null 或 map.size ==0 返回 true
	 * 
	 * @param map
	 * @return
	 * @author TMesh 2024-01-23 09:22
	 */
	public static <K,V> boolean isEmpty(Map<K,V> map) {
		return (map == null || map.isEmpty());
	}

	/**
	 * 功能描述: map 登陆null 或 list.size ==0 返回 true
	 * 
	 * @param map
	 * @return
	 * @author TMesh 2013-8-17 2024-01-23 09:22
	 */
	public static <K,V> boolean isNotEmpty(Map<K,V> map) {
		return (map != null && !map.isEmpty());
	}

	/**
	 * 功能描述: str 等于空
	 * 
	 * @return
	 * @author TMesh 2024-01-23 09:22
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 * 功能描述: str 不等于空
	 * 
	 * @return
	 * @author TMesh 2024-01-23 09:22
	 */
	public static boolean isNotEmpty(String str) {
		return str != null && !str.isEmpty();
	}

	/**
	 * 功能描述: Object 不等于空
	 * 
	 * @return
	 * @author TMesh 2024-01-23 09:22
	 */
	public static <E> boolean isNotEmpty(E e) {
		return e != null;
	}

	/**
	 * 功能描述: Object 等于空
	 * 
	 * @return
	 * @author TMesh 2024-01-23 09:22
	 */
	public static <E> boolean isEmpty(E e) {
		return e == null;
	}

	/**
	 * 功能描述:
	 * 
	 * @param array
	 * @return
	 * @author TMesh 2024-01-23 09:22
	 */
	public static <E> boolean isEmpty(E[] array) {
		return (array == null || array.length == 0);
	}

	/**
	 * 功能描述:
	 * 
	 * @param array
	 * @return
	 * @author TMesh 2024-01-23 09:22
	 */
	public static <E> boolean isNotEmpty(E[] array) {
		return (array != null && array.length > 0);
	}

}
