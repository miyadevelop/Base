//package com.my.ui.m;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RatingBar;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.lf.app.App;
//import com.lf.controler.tools.download.helper.FenYeMapLoader2;
//import com.lf.controler.tools.download.helper.LoadParam;
//import com.lf.view.tools.FontHelper;
//import com.lf.view.tools.SimpleFenYeFragment3;
//import com.lf.view.tools.UnitConvert;
//import com.my.shop.R;
//import com.my.shop.comment.Comment;
//import com.my.shop.comment.CommentLoader;
//
///**
// * 评论列表
// * Created by wangwei on 17/11/13.
// */
//public class CommentListFragment extends SimpleFenYeFragment3<Comment> {
//
//
//    @Override
//    public FenYeMapLoader2<Comment> getLoader() {
//        return CommentLoader.getInstance();
//    }
//
//    @Override
//    public BaseFenYeModule getRVModule(LoadParam loadParam) {
//        return new MyModule(loadParam);
//    }
//
//    int mImageHeight = UnitConvert.DpToPx(App.mContext, 100);//评论中图片的高度
//
//
//    public class MyModule extends BaseFenYeModule {
//
//        public MyModule(LoadParam loadParam) {
//            super(loadParam);
//        }
//
//        @Override
//        public RVBaseViewHolder getViewHolder(ViewGroup viewGroup, Context context, int itemType) {
//            return new MySimpleViewHolder(View.inflate(getContext(), R.layout.shop_item_comment, null));
//        }
//
//        @Override
//        public void onItemClick(View view, Comment comment) {
//        }
//
//
//        public class MySimpleViewHolder extends SimpleViewHolder {
//
//            private ImageView image0;
//            private ImageView image1;
//            private ImageView image2;
//            private ImageView image3;
//            private ImageView image4;
//            private ImageView image5;
//            private ImageView image6;
//            private ImageView image7;
//            private ImageView image8;
//            private TextView text;
//            private RatingBar comment_score;
//            private View images;
//
//            public MySimpleViewHolder(View view) {
//                super(view, Comment.class);
//                FontHelper.applyFont(getContext(), view, FontHelper.APP_FONT);
//                image0 = view.findViewById(R.id.image_0);
//                image1 = view.findViewById(R.id.image_1);
//                image2 = view.findViewById(R.id.image_2);
//                image3 = view.findViewById(R.id.image_3);
//                image4 = view.findViewById(R.id.image_4);
//                image5 = view.findViewById(R.id.image_5);
//                image6 = view.findViewById(R.id.image_6);
//                image7 = view.findViewById(R.id.image_7);
//                image8 = view.findViewById(R.id.image_8);
//                image8 = view.findViewById(R.id.image_8);
//                text = view.findViewById(R.id.text);
//                images = view.findViewById(R.id.comment_layout_images);
//                comment_score = view.findViewById(R.id.comment_score);
//            }
//
//
//            @Override
//            public void onBindViewHolder(Object data) {
//                super.onBindViewHolder(data);
//
//                Comment comment = (Comment) data;
//                if ("null".equals(comment.text) || TextUtils.isEmpty(comment.text)) {
//                    text.setText(R.string.comment_no_text);
//                }
//
//                if (!TextUtils.isEmpty(comment.getComment_image_0())) {
//                    images.setVisibility(View.VISIBLE);
//                    ViewGroup.LayoutParams lp = image0.getLayoutParams();
//                    lp.height = mImageHeight;
//                    image0.setLayoutParams(lp);
//                    Glide.with(getContext()).load(comment.comment_image_0).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image0);
//
//                } else {
//                    images.setVisibility(View.GONE);
//                    image0.setImageDrawable(null);
//                    ViewGroup.LayoutParams lp = image0.getLayoutParams();
//                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                    image0.setLayoutParams(lp);
//                }
//
//                if (!TextUtils.isEmpty(comment.getComment_image_1())) {
//                    ViewGroup.LayoutParams lp = image1.getLayoutParams();
//                    lp.height = mImageHeight;
//                    image1.setLayoutParams(lp);
//                    Glide.with(getContext()).load(comment.comment_image_1).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image1);
//                } else {
//                    image1.setImageDrawable(null);
//                    ViewGroup.LayoutParams lp = image1.getLayoutParams();
//                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                    image1.setImageDrawable(null);
//                }
//
//                if (!TextUtils.isEmpty(comment.getComment_image_2())) {
//                    ViewGroup.LayoutParams lp = image2.getLayoutParams();
//                    lp.height = mImageHeight;
//                    image2.setLayoutParams(lp);
//                    Glide.with(getContext()).load(comment.comment_image_2).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image2);
//                } else {
//                    image2.setImageDrawable(null);
//                    ViewGroup.LayoutParams lp = image2.getLayoutParams();
//                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                    image2.setImageDrawable(null);
//                }
//
//                if (!TextUtils.isEmpty(comment.getComment_image_3())) {
//                    ViewGroup.LayoutParams lp = image3.getLayoutParams();
//                    lp.height = mImageHeight;
//                    image3.setLayoutParams(lp);
//                    Glide.with(getContext()).load(comment.comment_image_3).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image3);
//                } else {
//                    image3.setImageDrawable(null);
//                    ViewGroup.LayoutParams lp = image3.getLayoutParams();
//                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                    image3.setImageDrawable(null);
//                }
//
//                if (!TextUtils.isEmpty(comment.getComment_image_4())) {
//                    ViewGroup.LayoutParams lp = image4.getLayoutParams();
//                    lp.height = mImageHeight;
//                    image4.setLayoutParams(lp);
//                    Glide.with(getContext()).load(comment.comment_image_4).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image4);
//                } else {
//                    image4.setImageDrawable(null);
//                    ViewGroup.LayoutParams lp = image4.getLayoutParams();
//                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                    image4.setImageDrawable(null);
//                }
//
//                if (!TextUtils.isEmpty(comment.getComment_image_5())) {
//                    ViewGroup.LayoutParams lp = image5.getLayoutParams();
//                    lp.height = mImageHeight;
//                    image5.setLayoutParams(lp);
//                    Glide.with(getContext()).load(comment.comment_image_5).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image5);
//                } else {
//                    image5.setImageDrawable(null);
//                    ViewGroup.LayoutParams lp = image5.getLayoutParams();
//                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                    image5.setImageDrawable(null);
//                }
//
//                if (!TextUtils.isEmpty(comment.getComment_image_6())) {
//                    ViewGroup.LayoutParams lp = image6.getLayoutParams();
//                    lp.height = mImageHeight;
//                    image6.setLayoutParams(lp);
//                    Glide.with(getContext()).load(comment.comment_image_6).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image6);
//                } else {
//                    image6.setImageDrawable(null);
//                    ViewGroup.LayoutParams lp = image6.getLayoutParams();
//                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                    image6.setImageDrawable(null);
//                }
//
//                if (!TextUtils.isEmpty(comment.getComment_image_7())) {
//                    ViewGroup.LayoutParams lp = image7.getLayoutParams();
//                    lp.height = mImageHeight;
//                    image7.setLayoutParams(lp);
//                    Glide.with(getContext()).load(comment.comment_image_7).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image7);
//                } else {
//                    image7.setImageDrawable(null);
//                    ViewGroup.LayoutParams lp = image7.getLayoutParams();
//                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                    image7.setImageDrawable(null);
//                }
//
//                if (!TextUtils.isEmpty(comment.getComment_image_8())) {
//                    ViewGroup.LayoutParams lp = image8.getLayoutParams();
//                    lp.height = mImageHeight;
//                    image8.setLayoutParams(lp);
//                    image8.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    Glide.with(getContext()).load(comment.comment_image_8).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image8);
//                } else {
//                    image8.setImageDrawable(null);
//                    ViewGroup.LayoutParams lp = image8.getLayoutParams();
//                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                    image8.setImageDrawable(null);
//                }
//
//                if (comment.score < 0)
//                    comment_score.setVisibility(View.GONE);
//                else {
//                    comment_score.setVisibility(View.VISIBLE);
//                    comment_score.setRating(new Float(comment.score) / 2);
//                }
//
//            }
//        }
//    }
//
//}
