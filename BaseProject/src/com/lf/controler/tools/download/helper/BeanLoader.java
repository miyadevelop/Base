package com.lf.controler.tools.download.helper;

/**
 * 加载只有一个对象,只负责获取到数据
 * @author ludeyuan
 */
public abstract class BeanLoader<T> extends BaseLoader{

	protected T mBean;

	public BeanLoader(T t){
		mBean = t;
	}

	@Override
	public final void load(Object... objects) {}

	/**
	 * 获取数据Bean
	 * @return 数据Bean
	 */
	public T get(){
		return mBean;
	}

	/**
	 * 解析
	 * @param json 要解析的字符串
	 */
	protected final Result<T> parse(String json,Object... objects)  {
		Result<T> result = onParse(json);
		if(result.mIsSuccess)
		{
			onParseOver(result.mBean,objects);
			onDataRefresh(result.mBean, objects);
		}
		return result;
	}

	@Override
	public void release() {
		super.release();
		mBean = null;
	}

	/**
	 * 执行解析
	 * @return 解析到的Bean、解析是否成功、解析失败的原因
	 */
	protected abstract Result<T> onParse(String json);

	/**
	 * 更新数据（替换、增加等需要外界的需求决定）
	 * 注意：如果原来的数据是一个集合，需要保证集合的地址不变
	 * @param newT 现在的数据
	 * @param objects 加载时提交的参数
	 */
	protected abstract void onParseOver(T newT,Object... objects);


	/**
	 * 数据有更新时调用
	 * @param newT 现在的数据
	 * @param objects 加载时提交的参数
	 */
	protected void onDataRefresh(T newT,Object... objects){};

}
