package com.lf.controler.tools.download.helper;

import java.util.ArrayList;

/**
 * 加载的数据类型是列表
 * @author ludeyuan
 *
 * @param <T>
 */
public abstract class ListLoader<T> extends BeanLoader<ArrayList<T>>{
	
	public ListLoader(ArrayList<T> t) {
		super(t);
	}

	
//	/**
//	 * 获取某一项数据
//	 * @param 该数据的索引
//	 * @return 该项数据
//	 */
//	public T get(int index){
//		if(get()==null){
//			return null;
//		}
//		return get().get(index);
//	}
}
