//package com.my.ui.m;
//
//import android.Manifest;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AlertDialog;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.EditorInfo;
//import android.widget.AdapterView;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RatingBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.lf.app.App;
//import com.lf.controler.tools.download.helper.NetLoader;
//import com.lf.view.tools.ScreenParameter;
//import com.lf.view.tools.UnitConvert;
//import com.lf.view.tools.WaitDialog;
//import com.lf.view.tools.activity.FileUpLoadActivity;
//import com.my.app.R;
//import com.my.m.comment.Comment;
//import com.my.m.comment.CommentAddLoader;
//import com.my.ui.BaseTitleActivity;
//
//import java.util.ArrayList;
//
///**
// * 发表评论界面
// * Created by wangwei on 17/11/14.
// */
//public class CommentAddActivity extends BaseTitleActivity implements TextView.OnEditorActionListener, AdapterView.OnItemClickListener, RatingBar.OnRatingBarChangeListener {
//
//
//    private ArrayList<String> mUnUploadImages = new ArrayList<>();
//    private Comment mComment = new Comment();
//    public final static String EXTRA_CALLBACK_NAME = "name";
//    public final static String EXTRA_CALLBACK_ID = "id";
//    public final static String EXTRA_SRC_ID = "src_id";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.base_activity_comment_add);
//
//        EditText editText = (EditText) findViewById(R.id.comment_add_edit_text);
//        editText.setOnEditorActionListener(this);
//
//        RatingBar ratingBar = (RatingBar) findViewById(R.id.comment_add_rating_score);
//        ratingBar.setOnRatingBarChangeListener(this);
//
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(CommentAddLoader.getInstance().getAction());
//        registerReceiver(mReceiver, filter);
//
//        DragGrid dragGrid = (DragGrid) findViewById(R.id.comment_add_grid_images);
//        ArrayList<ChannelItem> dragLists = new ArrayList<ChannelItem>();
//        dragGrid.setAdapter(new MyDragAdapter(this, dragLists));
//        dragGrid.setMyOnItemClickListener(this);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_comment_add, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == R.id.action_add_comment) {
//            addComment();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    @Override
//    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//        if (i == EditorInfo.IME_ACTION_DONE) {
//            addComment();
//            return true;
//        }
//        return false;
//    }
//
//
//    /**
//     * 提交评论
//     */
//    private void addComment() {
//
//        mComment.src_id = getIntent().getStringExtra(EXTRA_SRC_ID);
//
//        RatingBar ratingBar = (RatingBar) findViewById(R.id.comment_add_rating_score);
//        float rating = ratingBar.getRating();
//        if (rating <= 0) {
//            Snackbar.make(findViewById(R.id.root), R.string.comment_add_no_score, Snackbar.LENGTH_SHORT).show();
//            return;
//        }
//        mComment.score = new Float(rating * 2).intValue();
//
//        EditText editText = (EditText) findViewById(R.id.comment_add_edit_text);
//        String text = editText.getText().toString();
//        if (TextUtils.isEmpty(text))//允许评论为空
//        {
//            mComment.text = "null";
//        } else if (text.length() < 10) {
//            Snackbar.make(findViewById(R.id.root), R.string.comment_add_no_text, Snackbar.LENGTH_SHORT).show();
//            return;
//        } else
//            mComment.text = text;
//
//        DragGrid dragGrid = (DragGrid) findViewById(R.id.comment_add_grid_images);
//        MyDragAdapter adapter = (MyDragAdapter) dragGrid.getAdapter();
//        mUnUploadImages.clear();
//        List<ChannelItem> dragLists = adapter.getChannnelLst();
//        for (ChannelItem item : dragLists) {
//
//            if (!"add".equals(item.getImage())) {
//                mUnUploadImages.add(item.getImage());
//            }
//        }
//
//        if (mUnUploadImages.size() > 0) {
//            Intent intent = new Intent(this, FileUpLoadActivity.class);
//            intent.putExtra(FileUpLoadActivity.EXTRA_URL, "http://www.doubiapp.com/mall/upLoadPicture.json");
//            intent.putExtra(FileUpLoadActivity.EXTRA_PATHS, mUnUploadImages);
//            intent.putExtra(FileUpLoadActivity.EXTRA_NEED_SCALE, true);
//            startActivityForResult(intent, FileUpLoadActivity.REQUEST_CODE);
//        } else {
//            WaitDialog.show(this, "", true);
//            CommentAddLoader.getInstance().commitComment(mComment, String.valueOf(System.currentTimeMillis()) , getIntent().getStringExtra(EXTRA_CALLBACK_NAME), getIntent().getStringExtra(EXTRA_CALLBACK_ID));
//        }
//    }
//
//
//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            boolean isSuccess = intent.getBooleanExtra(NetLoader.STATUS, false);
//            if (action.equals(CommentAddLoader.getInstance().getAction())) {
//                WaitDialog.cancel(CommentAddActivity.this);
//
//                if (isSuccess) {
//                    Toast.makeText(App.mContext, R.string.comment_add_success, Toast.LENGTH_SHORT).show();
//                    finish();
//                } else
//                    Snackbar.make(findViewById(R.id.root), intent.getStringExtra(NetLoader.MESSAGE), Snackbar.LENGTH_SHORT).show();
//            }
//        }
//    };
//
//
//    public void goToChooseImage(View view) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
//                    getString(R.string.mis_permission_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
//        } else {
//            int maxNum = 9;
//            MultiImageSelector selector = MultiImageSelector.create(CommentAddActivity.this);
//            selector.showCamera(true);
//            selector.count(maxNum);
//            selector.multi();
//
//            DragGrid dragGrid = (DragGrid) findViewById(R.id.comment_add_grid_images);
//            MyDragAdapter adapter = (MyDragAdapter) dragGrid.getAdapter();
//            ArrayList<String> selectPath = new ArrayList<>();
//            List<ChannelItem> dragLists = adapter.getChannnelLst();
//            for (ChannelItem item : dragLists) {
//
//                if (!"add".equals(item.getImage())) {
//                    selectPath.add(item.getImage());
//                }
//            }
//
//            selector.origin(selectPath);
//            selector.start(CommentAddActivity.this, REQUEST_IMAGE);
//        }
//    }
//
//
//    private static final int REQUEST_IMAGE = 2;
//    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
//
//
//    private void requestPermission(final String permission, String rationale, final int requestCode) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.mis_permission_dialog_title)
//                    .setMessage(rationale)
//                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ActivityCompat.requestPermissions(CommentAddActivity.this, new String[]{permission}, requestCode);
//                        }
//                    })
//                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
//                    .create().show();
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                goToChooseImage(null);
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE) {//图片选择完成
//            if (resultCode == RESULT_OK) {
//                ArrayList<String> selectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
//                DragGrid dragGrid = (DragGrid) findViewById(R.id.comment_add_grid_images);
//                MyDragAdapter adapter = (MyDragAdapter) dragGrid.getAdapter();
//                ArrayList<ChannelItem> dragLists = new ArrayList<ChannelItem>();
//                for (int i = 0; i < selectPath.size(); i++) {
//                    ChannelItem item = new ChannelItem(i + 1, "item" + (i + 1), selectPath.get(i));
//                    dragLists.add(item);
//                }
//                adapter.getChannnelLst().clear();
//                adapter.addItems(dragLists);
//            }
//        } else if (requestCode == FileUpLoadActivity.REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {//图片上传完成
//
//                ArrayList<String> imageUrls = data.getStringArrayListExtra(FileUpLoadActivity.RESULT_URLS);
//
//                StringBuilder stringBuilder = new StringBuilder();
//
//                for (String url : imageUrls) {
//                    stringBuilder.append(url);
//                    stringBuilder.append(",");
//                }
//                mComment.image = stringBuilder.toString();
//                CommentAddLoader.getInstance().commitComment(mComment, String.valueOf(System.currentTimeMillis()), getIntent().getStringExtra(EXTRA_CALLBACK_NAME), getIntent().getStringExtra(EXTRA_CALLBACK_ID));
//                WaitDialog.show(this, "", true);
//            }
//        }
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//        DragGrid dragGrid = (DragGrid) findViewById(R.id.comment_add_grid_images);
//        MyDragAdapter adapter = (MyDragAdapter) dragGrid.getAdapter();
//        if (i == adapter.getCount() - 1) {
//            goToChooseImage(null);
//        }
//    }
//
//    @Override
//    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//        TextView textView = (TextView) findViewById(R.id.comment_add_text_score);
//        textView.setText(String.format(getString(R.string.comment_add_score_1), v));
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(mReceiver);
//    }
//
//
//
//
//    /**
//     * 可拖动、删除的九宫格
//     * Created by wangwei on 17/11/14.
//     */
//    public class MyDragAdapter extends DragAdapter {
//
//        private Context mContext;
//        private int imageWidth;
//
//        public MyDragAdapter(Context context, List<ChannelItem> channelList) {
//            super(context, channelList);
//            mContext = context;
//            imageWidth = (ScreenParameter.getDisplayWidthAndHeight(context)[0] - UnitConvert.DpToPx(context, 44))/3;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup viewGroup) {
//
//            View view = LayoutInflater.from(mContext).inflate(R.layout.base_item_comment_add_image, null);
//            ImageView item_image = view.findViewById(R.id.comment_add_image);
//            ViewGroup.LayoutParams layoutParams = item_image.getLayoutParams();
//            layoutParams.height = imageWidth;
//            item_image.setLayoutParams(layoutParams);
//            ImageView item_icon = view.findViewById(R.id.delete_icon);
//            ChannelItem channel = getItem(position);
//            if ("add".equals(channel.getImage())) {
//                item_icon.setVisibility(View.GONE);
//                item_image.setImageResource(R.drawable.shop_button_add_image);
//            } else {
//                item_icon.setVisibility(View.VISIBLE);
//                Glide.with(mContext).load(Uri.parse("file://" + channel.getImage())).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(item_image);
//            }
//
//            if ((position == holdPosition && isItemShow)) {
//                selectedPos = position;
//                if (isReset) {
//                    selectedPos = -1;
//                    holdPosition = -1;
//                }
//            }
//
//            if (isChanged && (position == holdPosition) && !isItemShow) {
//                isChanged = false;
//            }
//            if (remove_position == position) {
//            }
//            return view;
//        }
//
//
//        @Override
//        public void remove(int deletePosition) {
//            //如果已经有九项，移除一项的时候，在末尾加上“添加按钮”
//            List<ChannelItem> dragLists = getChannnelLst();
//            if (dragLists.size() == 9 && !"add".equals(dragLists.get(8).getImage())) {
//                addItem(new ChannelItem(9, "", "add"));
//            }
//            super.remove(deletePosition);
//        }
//
//
//        @Override
//        public void addItems(ArrayList<ChannelItem> channels) {
//            //如果不足九项，则在末尾加上“添加按钮”
//            if (channels.size() < 9 && channels.size() > 0) {
//                channels.add(new ChannelItem(9, "", "add"));
//            }
//            super.addItems(channels);
//        }
//    }
//
//}
