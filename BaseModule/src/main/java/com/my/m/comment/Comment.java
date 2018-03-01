package com.my.m.comment;

import android.text.TextUtils;

/**
 * 评论
 * Created by wangwei on 17/11/13.
 */
public class Comment {
    public String id;//评论的id
    public String user_id;//评论者id
    public String user_name;//评论者昵称
    public String user_icon;//评论这头像网址
    public String text;//评论内容
    public String image;//图片网址，多个用英文逗号隔开，最多九个
    public String at_id;//@的人的id
    public String at_name;//@的人的昵称
    public String src_id;//评论的对象的id
    public int like_count;//本条评论点赞人数
    public String create_time;//创建时间
    public int score = -1;//打分
    public String comment_image_0;
    public String comment_image_1;
    public String comment_image_2;
    public String comment_image_3;
    public String comment_image_4;
    public String comment_image_5;
    public String comment_image_6;
    public String comment_image_7;
    public String comment_image_8;


//    public String getText() {
//
//        try {
//
//            byte[] bytes= Base64.decode(text,Base64.DEFAULT);
//            text = new String(bytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return text;
//    }

    public String getCreate_time()
    {
        if(create_time.length() > 11)
            create_time = create_time.substring(5, 16);
        return create_time;
    }


    public String getComment_image_0() {
        if (TextUtils.isEmpty(comment_image_0))
            initImages();
        return comment_image_0;
    }


    public String getComment_image_1() {
        if (TextUtils.isEmpty(comment_image_1))
            initImages();
        return comment_image_1;
    }


    public String getComment_image_2() {
        if (TextUtils.isEmpty(comment_image_2))
            initImages();
        return comment_image_2;
    }


    public String getComment_image_3() {
        if (TextUtils.isEmpty(comment_image_3))
            initImages();
        return comment_image_3;
    }


    public String getComment_image_4() {
        if (TextUtils.isEmpty(comment_image_4))
            initImages();
        return comment_image_4;
    }


    public String getComment_image_5() {
        if (TextUtils.isEmpty(comment_image_5))
            initImages();
        return comment_image_5;
    }


    public String getComment_image_6() {
        if (TextUtils.isEmpty(comment_image_6))
            initImages();
        return comment_image_6;
    }


    public String getComment_image_7() {
        if (TextUtils.isEmpty(comment_image_7))
            initImages();
        return comment_image_7;
    }


    public String getComment_image_8() {
        if (TextUtils.isEmpty(comment_image_8))
            initImages();
        return comment_image_8;
    }


    private void initImages() {
        String[] images = image.split(",");
        if (images.length > 0) {
            comment_image_0 = images[0];
            if (images.length > 1) {
                comment_image_1 = images[1];
                if (images.length > 2) {
                    comment_image_2 = images[2];
                    if (images.length > 3) {
                        comment_image_3 = images[3];
                        if (images.length > 4) {
                            comment_image_4 = images[4];
                            if (images.length > 5) {
                                comment_image_5 = images[5];
                                if (images.length > 6) {
                                    comment_image_6 = images[6];
                                    if (images.length > 7) {
                                        comment_image_7 = images[7];
                                        if (images.length > 8) {
                                            comment_image_8 = images[8];
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
