package com.lf.view.tools;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lf.app.App;
import com.lf.view.tools.imagecache.CircleImageView;
import com.lf.view.tools.imagecache.MyImageView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
/**
 * 工具类，将bean表示的数据内容填入到layout中对应的子view中，显示出来，
 * 为了配对，需要bean中的变量名和layout的子view的id相同，
 * 本类主要是为了减少界面显示数据时的代码量，满足绝大多数时候的显示需求，无法适配所有情况
 * 同时相应地带来了一些效率上的问题，如果发现用了本类中的方法导致界面卡滞，请换回基础方法
 * @author wangwei
 *
 */
public class Bean2View {


    /**
     * 将bean表示的数据内容填入到layout中对应的子view中 显示出来，bean中的变量名需要和layout的子view的id相同，
     *
     * @param context
     * @param bean
     * @param layout
     */
    public static void show(Context context, Object bean, View layout) {
        if (bean.getClass().isPrimitive() || bean.getClass() == String.class)//基础类型，相当于直接把bean的值显示到layout中
        {
            try {
                //由于基础类型（String 、int等）bean本身就是当field来用，所以就不存在field的名字，所以无法通过field的名字来找到对应的view
                //这里约定需要通过固定的名字“target”去找到view，所以要使用本方法的话，就得将view的id写成@+id/target
                View view = layout.findViewById(App.id("target"));
                showView(context, bean, view);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //以下为自定类型

        Field[] fields = bean.getClass().getDeclaredFields();
        //遍历类型中的field，将feild填入到layout的对应的子View中
        for (Field field : fields) {
            View view = layout.findViewById(App.id(field.getName()));

            if (null == view)
                continue;

            Object fieldValue = null;
            try {
                //通过get函数获取变量的值
                Method[] methods = bean.getClass().getDeclaredMethods();
                String methodName = "get" + field.getName();

                for (Method method : methods) {
                    if (method.getName().equalsIgnoreCase(methodName)) {
                        fieldValue = method.invoke(bean);
                        break;
                    }
                }
                //如果无法通过get函数获取变量值，则直接通过变量本身去获取
                if (null == fieldValue) {
                    // 获取原来的访问控制权限
                    boolean accessFlag = field.isAccessible();
                    // 修改访问控制权限
                    field.setAccessible(true);
                    fieldValue = field.get(bean);
                    //恢复访问控制权限
                    field.setAccessible(accessFlag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (null == fieldValue)
                continue;

            showView(context, fieldValue, view);
        }
    }


    /**
     * 将Class中的field和layout中的子view通过子view的setTag方法关联上，
     * 并且将所有的要显示数据的子view放入到list中返回
     * 本方法主要是用在Adapter中，作用类似于ViewHolder，将所有的子view缓存再list里
     * 本方法需要配合showViews方法同时使用
     *
     * @param classType
     * @param layout
     * @return
     * @see #showViews
     */
    public static ArrayList<View> holdViews(Class<?> classType, ViewGroup layout) {

        ArrayList<View> views = new ArrayList<View>();

        if (classType.isPrimitive() || classType == String.class)//基础类型，不需要通过任何方式去取field的值，因为bean本身就是field
        {
            try {
                //由于基础类型（String 、int等）bean本身就是当field来用，所以就不存在field的名字，所以无法通过field的名字来找到对应的view
                //这里约定需要通过固定的名字“target”去找到view，所以要使用本方法的话，就得将view的id写成@+id/target
                View view = layout.findViewById(App.id("target"));
                if (null != view) {
                    views.add(view);
                }
                return views;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //自定义类型
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields) {
            View view = layout.findViewById(App.id(field.getName()));
//			if(null == view && -1 != layout.getId() && App.id(field.getName()) == layout.getId())
//			{
//				view = layout;
//			}
            if (null == view)
                continue;

            try {
                //如果有get函数，就将get函数设置为view的Tag
                Method[] methods = classType.getDeclaredMethods();
                String methodName = "get" + field.getName();
                for (Method method : methods) {
                    if (method.getName().equalsIgnoreCase(methodName)) {
                        view.setTag(App.id("tag_bean2view_feilder_holder"), method);
                        break;
                    }
                }

                //如果没有Get函数，就直接将field本身作为view的tag
                if (null == view.getTag(App.id("tag_bean2view_feilder_holder")))
                    view.setTag(App.id("tag_bean2view_feilder_holder"), field);
                views.add(view);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return views;
    }


    /**
     * 批量将bean中field显示到views中的各个view中，views中的view需要含有tag，tag可以表示与之配对的field
     *
     * @param context
     * @param bean
     * @param views
     */
    public static void showViews(Context context, Object bean, ArrayList<View> views) {
        for (View view : views) {
            Object fieldValue;
            try {

                Object tag = view.getTag(App.id("tag_bean2view_feilder_holder"));
                if (null == tag)//基础类型
                {
                    fieldValue = bean;
                } else if (tag instanceof Method)//有get函数的变量
                {
                    Method method = (Method) tag;
                    fieldValue = method.invoke(bean);
                } else//没有get函数的变量
                {
                    Field field = (Field) tag;
                    // 获取原来的访问控制权限
                    boolean accessFlag = field.isAccessible();
                    // 修改访问控制权限
                    field.setAccessible(true);
                    fieldValue = field.get(bean);
                    //恢复访问控制权限
                    field.setAccessible(accessFlag);
                }
                showView(context, fieldValue, view);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    //正则表达式用来判断String内容是html还是普通格式
    public static final Pattern p = Pattern.compile("</?[^>]+>|&?;|<br/>");

    /**
     * 将fieldValue的值填入view中显示出来，目前支持有限个view的类型，若有新的需求后期再扩展
     *
     * @param context
     * @param fieldValue
     * @param view
     */
    @SuppressWarnings({"unchecked"})
    public static void showView(Context context, Object fieldValue, View view) {
        if (view instanceof CustomView) //自定义的view需要实现接口CustomView来实现数据显示，优先按CustomView处理
        {
            ((CustomView) view).show(fieldValue);
        } else if (view instanceof EditText) {
            TextView textView = ((TextView) view);
            String textString = fieldValue.toString();
            if (p.matcher(textString).find())
                textView.setText(Html.fromHtml(fieldValue.toString()));
            else
                textView.setText(fieldValue.toString());
        } else if (view instanceof RadioButton) {
            ((RadioButton) view).setChecked((Boolean) fieldValue);
        } else if (view instanceof TextView) {
            TextView textView = ((TextView) view);
            String textString = fieldValue.toString();
            if (null != textView.getHint()) {
                String text = textView.getHint().toString();//注意，这里是hint，不是text，因为text会在Adapter中多次被赋值，无法重用
                if (p.matcher(textString).find()) {
                    if (text.contains("@"))
                        textView.setText(Html.fromHtml(text.replace("@", fieldValue.toString())));
                    else
                        textView.setText(Html.fromHtml(String.format(text, fieldValue)));
                } else {
                    if (text.contains("@"))
                        textView.setText(text.replace("@", fieldValue.toString()));//有时textView需要显示拼接出来的文字，这里主要满足这个需求
                    else {
                        textView.setText(String.format(text, fieldValue));
                    }
                }
            } else {
                if (p.matcher(textString).find())
                    textView.setText(Html.fromHtml(fieldValue.toString()));
                else
                    textView.setText(fieldValue.toString());
            }
        } else if (view instanceof MyImageView) {
            ((MyImageView) view).setImagePath(fieldValue.toString());
        } else if (view instanceof CircleImageView) {
            String url = fieldValue.toString();
            if (!TextUtils.isEmpty(url))
                ((CircleImageView) view).setImagePath(url);
        } else if (view instanceof ImageView) {
            String url = fieldValue.toString();
            if (!TextUtils.isEmpty(url)) {
                Glide.clear(view);
                if (url.endsWith("gif")) {
                    Glide.with(context).load(Uri.parse(fieldValue.toString())).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into((ImageView) view);
                } else
                    //.diskCacheStrategy(DiskCacheStrategy.SOURCE)防止图片变绿,diskCacheStrategy缓存策略，这里表示缓存原图
                    Glide.with(context).load(Uri.parse(fieldValue.toString())).diskCacheStrategy(DiskCacheStrategy.SOURCE).into((ImageView) view);
            }
        } else if (view instanceof AdapterView) {
//            if(((AdapterView) view).getAdapter() == null) {
            GeneralAdapter adapter = new GeneralAdapter(context, (List<Object>) fieldValue);
            ((AdapterView) view).setAdapter(adapter);
//            }
//            else
//            {
//                ((GeneralAdapter)((AdapterView) view).getAdapter()).notifyDataSetChanged();
//            }
        } else if (view instanceof RecyclerView) {
            GeneralRecyclerViewAdapter adapter = new GeneralRecyclerViewAdapter((List<Object>) fieldValue);
            ((RecyclerView) view).setAdapter(adapter);
        } else if (view instanceof RecyclerView) {
            GeneralRecyclerViewAdapter adapter = new GeneralRecyclerViewAdapter((List<Object>) fieldValue);
            ((RecyclerView) view).setAdapter(adapter);
        }
//        else {
//            try {
////                view.setActivated((boolean) fieldValue);//一般用于list里的某项选中状态
//            } catch (Exception e) {
//
//            }
//        }

//		else if(view instanceof AbsListView)//GridView 或者 ListView
//		{
//			GeneralAdapter adapter = new GeneralAdapter(context, (List<Object>)fieldValue);
//			((AbsListView)view).setAdapter(adapter);
//		}

//		else if(view instanceof Gallery)
//		{
//			GeneralAdapter adapter = new GeneralAdapter(context, (List<Object>)fieldValue);
//			((Gallery)view).setAdapter(adapter);
//		}
//		else if(view instanceof ImageView)
//		{
//			((ImageView)view).setImageResource(MyR.drawable(context, fieldValue.toString()));
//		}

    }


    /**
     * 通用的Adapter，不需要每次为不同的需求，写不同的Adapter，
     * 只需在布局中给listview、gridview等设置tag，tag表示子view的布局文件名称
     * 这里采用view holder的思路，对绘制的效率影响不大
     *
     * @author wangwei
     */
    public static class GeneralAdapter extends ArrayAdapter<Object> {

        public GeneralAdapter(Context context, List<Object> objects) {
            super(context, 0, objects);
            // TODO Auto-generated constructor stub
        }


        @SuppressWarnings("unchecked")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                //为了简化代码，不需要在java中配置Adapter，这里通过listview的tag来指定Adapter中子项对应的布局的文件名
                convertView = View.inflate(getContext(), App.layout((String) parent.getTag()), null);
                ArrayList<View> views = holdViews(getItem(position).getClass(), (ViewGroup) convertView);
                convertView.setTag(views);
            }

            Object item = getItem(position);
            ArrayList<View> views = (ArrayList<View>) convertView.getTag();
            showViews(getContext(), item, views);
            convertView.setTag(App.id("tag_bean2view_feilder_holder"), item);//给点击响应提供便利

            return convertView;
        }
    }


    /**
     * 自定义的view无法通过setText等已知函数来显示数据，所以需要自行继承此接口实现数据显示
     *
     * @author wangwei
     */
    public static interface CustomView {
        public void show(Object fieldValue);
    }


    public static class GeneralRecyclerViewAdapter extends RecyclerView.Adapter<GeneralRecyclerViewHolder> {

        List<Object> mDatas;

        public GeneralRecyclerViewAdapter(List<Object> datas) {
            mDatas = datas;
        }

        @Override
        public GeneralRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(App.layout((String) parent.getTag()), parent, false);
            return new GeneralRecyclerViewHolder(mDatas.get(0).getClass(), itemView);
        }

        @Override
        public void onBindViewHolder(GeneralRecyclerViewHolder holder, int position) {
            showViews(holder.mViews.get(0).getContext(), getItem(position), holder.mViews);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }


        public Object getItem(int position) {
            return mDatas.get(position);
        }
    }


    public static class GeneralRecyclerViewHolder extends RecyclerView.ViewHolder {
        public ArrayList<View> mViews;

        public GeneralRecyclerViewHolder(Class aClass, View itemView) {
            super(itemView);
            mViews = holdViews(aClass, (ViewGroup) itemView);
        }

    }
}
