package com.lf.tool.data_persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayDataEditor {
	//要修改的数据集合
	private Map<Integer,DataEditor> map = new HashMap<Integer, DataEditor>();
	//要添加的数据集合
	private List<DataEditor> appendList = new ArrayList<DataEditor>();;
	
    protected ArrayDataEditor(){
    	
    }
    
    protected Map<Integer,DataEditor> getMap(){
    	return map;
    }
    
    protected List<DataEditor> getAppendList(){
    	return appendList;
    }
    
    
    /**
     * 获取具体某一条数据的编辑器
     * @param index
     * @return DataEditor
     */
    public DataEditor getDataEditor(int index){
    	if(map.containsKey(index)){
    		return map.get(index);
    	}else{
    		DataEditor editor = new DataEditor();
    		map.put(index, editor);
    		return editor;
    	}
    	
    }
    
    
    /**
     * 获取要添加的数据的编辑器
     * @return DataEditor
     */ 
    public DataEditor getAppendDataEditor(){
    	DataEditor editor = new DataEditor();
    	appendList.add(editor);
		return editor;
    }
}
