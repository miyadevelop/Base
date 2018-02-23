package com.lf.controler.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;

import com.lf.tools.log.MyLog;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 图片工具类(只负责读取)
 *
 * @author LinChen
 */
public class BitmapUtils {

    /**
     * 根据sd卡上的路径获取图片
     *
     * @param context
     * @param path
     * @param bitmapOptions 可以传null
     * @return
     */
    public static Bitmap getBitmapFromSD(String path, BitmapOptions bitmapOptions) {
        if (TextUtils.isEmpty(path)) {
            //如果sd卡上图片的路径为空，就直接返回空
            return null;
        }
        Bitmap result = null;
        try {
            result = getBitmap(new BufferedInputStream(new FileInputStream(path)), bitmapOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Bitmap getBitmapFromSD(String path, int reqWidth, int reqHeight) {
        BitmapOptions option = new BitmapOptions();
        option.requireWidth = reqWidth;
        option.requireHeight = reqHeight;
        return getBitmapFromSD(path, option);
    }

    public static Bitmap getBitmapFromSD(String path, int reqWidth, int reqHeight, Config config) {
        BitmapOptions option = new BitmapOptions();
        option.requireWidth = reqWidth;
        option.requireHeight = reqHeight;
        option.config = config;
        return getBitmapFromSD(path, option);
    }

    /**
     * 根据assets上的路径获取图片
     *
     * @param context
     * @param path
     * @param bitmapOptions 可以传null
     * @return
     */
    public static Bitmap getBitmapFromAssets(Context context, String path,
                                             BitmapOptions bitmapOptions) {
        Bitmap result = null;
        try {
            if (path.startsWith("assets/"))
                path = path.replace("assets/", "");
            result = getBitmap(new BufferedInputStream(context.getAssets().open(path)),
                    bitmapOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取res下的图片
     *
     * @param context
     * @param resID
     * @param bitmapOptions 可以传null
     * @return
     */
    public static Bitmap getBitmapFromRes(Context context, int resID, BitmapOptions bitmapOptions) {
        Drawable drawable = context.getResources().getDrawable(resID);
        Bitmap bitmap = drawable2Bitmap(drawable);

        if (bitmapOptions == null) {
            return bitmap;
        } else {
            return scaleWithWH(bitmap, bitmapOptions.requireWidth, bitmapOptions.requireHeight);
        }
    }

    /**
     * 获取网络图片，此方法会阻塞线程
     *
     * @param context
     * @param url
     * @param bitmapOptions 可以传null
     * @return
     */
    public static Bitmap getBitmapFromNet(Context context, String url, BitmapOptions bitmapOptions) {
        Bitmap result = null;
        try {
            URL connect = new URL(url);
            result = getBitmap(new BufferedInputStream((InputStream) connect.getContent()),
                    bitmapOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据流获取Bitmap
     *
     * @param is
     * @param bitmapOptions 可以传null
     * @return
     */
    public static Bitmap getBitmap(InputStream is, BitmapOptions bitmapOptions) {
        Bitmap result = null;
        try {
            if (bitmapOptions == null) {// 如果没有设置参数，new一个默认的
                bitmapOptions = new BitmapOptions();
            }
            result = decodeSampledBitmapFromInputstream(is, bitmapOptions);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据指定的长宽，将is解析成Bitmap
     *
     * @param is
     * @param reqWidth  指定的显示宽度
     * @param reqHeight 指定的显示长度
     * @return
     * @throws IOException
     */
    private static Bitmap decodeSampledBitmapFromInputstream(InputStream is,
                                                             BitmapOptions bitmapOptions) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        is.close();

        // 获取图片的实际大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 根据图片的实际大小来解析图片，得到图片的实际宽高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size(), options);
        // ====================屏蔽以下一句，测试压缩图片长宽的作用=====
        // 根据图片的实际大小和指定的长宽计算压缩的倍数
        if (-1 != bitmapOptions.requireWidth && -1 != bitmapOptions.requireHeight) {
            options.inSampleSize = calculateInSampleSize(options, bitmapOptions.requireWidth,
                    bitmapOptions.requireHeight);
        }
        // 决定根据指定的大小来解析图片
        options.inJustDecodeBounds = false;
        // 2014-09-22 11:28:59
        // LinChen
        // 图片数据的深层拷贝，不知效果如何，先加上。（关于图片回收的问题：图片的释放在native不一定会释放）
        options.inPurgeable = true;
        options.inInputShareable = true;
        // =====================屏蔽以下一句，测试修改config的作用=========
        // 修改config，以最合适的配置、最低的内存开销显示每个像素
        // mimetype类型：image/jpeg、image/png
        if ("image/jpeg".equals(options.outMimeType)) {
            options.inPreferredConfig = bitmapOptions.config;
        }

        Bitmap result = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size(), options);
        baos = null;
        return result;
    }

    /**
     * 根据图片的实际大小和指定的长宽计算压缩的倍数
     *
     * @param options   options.outHeight和options.outWidth反应了图片的实际大小
     * @param reqWidth  指定的显示宽度
     * @param reqHeight 指定的显示长度
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                             int reqHeight) {
        // 获取图片的实际大小
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        // 如果图片的实际长度或宽度大于指定显示的长度或宽度，获取倍数
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * 将Drawable转化成Bitmap
     *
     * @param drawable
     * @return 没有数据就返回null
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable
                            .getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * 将Bitmap转化成Drawable
     *
     * @param context
     * @param bitmap
     * @return 没有数据就返回null
     */
    public static Drawable bitmap2Drawable(Context context, Bitmap bitmap) {
        if (context == null || bitmap == null) {
            return null;
        } else {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
            return bitmapDrawable;
        }
    }

    /**
     * 保存Bitmap到指定位置
     *
     * @param bmp
     * @param filePath
     */
    public static void saveBitmap(Bitmap bmp, String filePath) {
        try {
            FileOutputStream out = new FileOutputStream(filePath);
            bmp.compress(Bitmap.CompressFormat.PNG, 75, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 图片的缩放方法,如果参数宽高为0,则不处理<br>
     * <p/>
     * <b>注意</b> src实际并没有被回收，如果你不需要，请手动置空
     *
     * @param src ：源图片资源
     * @param w   ：缩放后宽度
     * @param h   ：缩放后高度
     */
    public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w <= 0 || h <= 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }

    /***
     * 图片的缩放方法,如果参数宽高为0,则不处理<br>
     * <p/>
     * <b>注意</b> src实际并没有被回收，如果你不需要，请手动置空
     *
     * @param src   ：源图片资源
     * @param scale ：0~1 缩小，1~N放大
     */
    public static Bitmap scale(Bitmap src, float scale) {
        if (scale < 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = scale;
            float scaleHeight = scale;
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }


    /**
     * 质量压缩方法，将图片压缩到对应的kb
     *
     * @param image
     * @param size  图片的大小，单位kb
     * @return
     * @auth ww 于2017.11.21
     */
    public static Bitmap scaleToSize(Bitmap image, int size, String saveFilePath) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于sizekb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            if (options > 10)
                options -= 10;// 每次都减少10
            else
                break;
        }

        if (null != saveFilePath)//如果文件路径不为null，则直接将bitmap保存到文件，再返回null
        {
            try {
                FileOutputStream fos = new FileOutputStream(saveFilePath);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else {//否则，不存储文件，返回bitmap
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
            return bitmap;
        }
    }


    /**
     * 生成bitmap的参数
     *
     * @author LinChen
     */
    public static class BitmapOptions {
        public int requireWidth = -1;
        public int requireHeight = -1;

        public Config config = Config.ARGB_8888;
    }

    /**
     * 根据密度去压缩图片
     *
     * @param src
     * @param screenDensity 屏幕密度
     * @param scale         图片密度与屏幕密度的比例，如果为0，则获取bitmap的密度
     * @return
     */
    public static Bitmap scaleAsDensity(Bitmap scalebp, int screenDensity, Float scale) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scalebp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        //	     newOpts.inSampleSize = 2;//设置缩放比例

        newOpts.inTargetDensity = screenDensity;
        newOpts.inDensity = (int) (screenDensity * scale);  //320

        newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;
    }


    /**
     * 图片合成，将第二张图片作为遮罩层，合成为一张图片
     *
     * @param bitmap
     * @param maskBitmap
     * @return
     */
    public static Bitmap computeBitmap(Bitmap bitmap, Bitmap maskBitmap) {

        Bitmap bp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Config.ARGB_4444);
        Canvas myCanvas = new Canvas(bp);
        Paint pp = new Paint();
        pp.setDither(true);
        pp.setAntiAlias(true);
        pp.setFilterBitmap(true);
        myCanvas.drawBitmap(bitmap, 0, 0, pp);
        pp.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        myCanvas.drawBitmap(maskBitmap, new Rect(0, 0, maskBitmap.getWidth(),
                maskBitmap.getHeight()), new RectF(0, 0, bitmap.getWidth(),
                bitmap.getHeight()), pp);
        // myCanvas.drawBitmap(maskBitmap, 0, 0, pp);
        return bp;

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Context context, Bitmap bitmap) {

        MyLog.i("blur start");
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_4444);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context.getApplicationContext());

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(25.f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();
        MyLog.i("blur end");
        return outBitmap;


    }
}
