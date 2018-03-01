//package com.my.ui.m;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.ActivityOptionsCompat;
//import android.support.v4.util.Pair;
//import android.view.View;
//
//import com.google.gson.Gson;
//import com.lf.controler.tools.download.helper.LoadParam;
//import com.lf.view.tools.SimpleFenYeFragment3;
//import com.lf.view.tools.activity.BaseImagePreActivity;
//import com.my.shop.R;
//import com.my.shop.comment.Comment;
//import com.my.shop.commodity.Commodity;
//import com.my.ui.BaseTitleActivity;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Created by wangwei on 17/11/13.
// */
//public class CommentListActivtiy extends BaseTitleActivity {
//
//    Commodity mCommodity;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.shop_activity_comment_list);
//
//
//        String dataString = getIntent().getStringExtra("data");
//        if (null != dataString) {
//            //显式打开
//            mCommodity = new Gson().fromJson(dataString, Commodity.class);
//
//            SimpleFenYeFragment3<?> fragment = (SimpleFenYeFragment3<?>) getSupportFragmentManager().findFragmentById(R.id.comment_list_layout_comments);
//            LoadParam loadParam = new LoadParam();
//            loadParam.addParams("src_id", "commodity_" + mCommodity.comm_id);
//            fragment.setLoadParam(loadParam);
//        }
//
//    }
//
//
//    public void showImage(View view) {
//
//        SimpleFenYeFragment3<?> fragment = (SimpleFenYeFragment3<?>) getSupportFragmentManager().findFragmentById(R.id.comment_list_layout_comments);
//        Comment comment = (Comment) fragment.getItem(view);
//
//        Intent intent = new Intent();
//        intent.setClass(this, BaseImagePreActivity.class);
//        //设置默认查看第几张图
//        if (view.getId() == R.id.image_0) {
//            intent.putExtra("task_big_image_index", 0);
//        } else if (view.getId() == R.id.image_1) {
//            intent.putExtra("task_big_image_index", 1);
//        }else if (view.getId() == R.id.image_2) {
//            intent.putExtra("task_big_image_index", 2);
//        }else if (view.getId() == R.id.image_3) {
//            intent.putExtra("task_big_image_index", 3);
//        }else if (view.getId() == R.id.image_4) {
//            intent.putExtra("task_big_image_index", 4);
//        }else if (view.getId() == R.id.image_5) {
//            intent.putExtra("task_big_image_index", 5);
//        }else if (view.getId() == R.id.image_6) {
//            intent.putExtra("task_big_image_index", 6);
//        }else if (view.getId() == R.id.image_7) {
//            intent.putExtra("task_big_image_index", 7);
//        }else if (view.getId() == R.id.image_8) {
//            intent.putExtra("task_big_image_index", 8);
//        }
//        //设置图片集合路径
//        ArrayList<String> images = new ArrayList<>(Arrays.asList(comment.image.split(",")));
//        intent.putStringArrayListExtra("imagepaths", images);
//
//        List<Pair<View, String>> pairs = new ArrayList<>();
//        pairs.add(Pair.create(view, "transitionPic"));
//
//        ActivityOptionsCompat options = ActivityOptionsCompat
//                .makeSceneTransitionAnimation(this, pairs.toArray(new Pair[pairs.size()]));
//
//        ActivityCompat.startActivity(this, intent, options.toBundle());
//    }
//}
