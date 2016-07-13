package com.sheepyang.gankgirl.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.sheepyang.gankgirl.R;
import com.sheepyang.gankgirl.base.RxBaseActivity;
import com.sheepyang.gankgirl.utils.AppUtil;
import com.sheepyang.gankgirl.utils.BitmapUtil;
import com.sheepyang.gankgirl.utils.SnackbarUtil;
import com.sheepyang.gankgirl.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 发送动态
 *
 * @author Administrator
 */
public class SendMessageActivity extends RxBaseActivity implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_PHOTO_ALBUM = 2;
    private File outImage;
    private PopupWindow photowindow;
    private ImageView img;
    private boolean isSend = false;
    private boolean ismaxphoto;
    private Bitmap mBm;
    private List<Bitmap> photolist = new ArrayList<Bitmap>();
    private ArrayList<String> photoStringlist = new ArrayList<String>();
    private GridView mGViewSendMsg;
    private GridphotoApater mGpApater;
    private ProgressDialog pDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_message;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        img = (ImageView) findViewById(R.id.sendmsg_img);
        mGViewSendMsg = (GridView) findViewById(R.id.gridView_sendmessage);
        pDialog = new ProgressDialog(this);
        if (savedInstanceState != null) {
            String imagePath = savedInstanceState.getString("imagPath");
            if (!TextUtils.isEmpty(imagePath)) {
                outImage = new File(imagePath);
            }

            mGpApater = new GridphotoApater();
            mGViewSendMsg.setAdapter(mGpApater);

            photoStringlist = savedInstanceState
                    .getStringArrayList("photoStringlist");
            if (photoStringlist != null && photoStringlist.size() > 1) {
                togetBitmap();
            } else {
                initData();
            }

        } else {
            initData();
        }
    }

    private void togetBitmap() {
        pDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {

                for (int i = 0; i < photoStringlist.size(); i++) {
                    if (photoStringlist.get(i).equals("addImage")) {
                        Drawable drawable = getResources().getDrawable(
                                R.drawable.btn_add_img);
                        BitmapDrawable db = (BitmapDrawable) drawable;
                        mBm = db.getBitmap();
                        photolist.add(mBm);
                    } else {
                        photolist.add(BitmapUtil.getSmallBitmap(
                                photoStringlist.get(i), 320));
                    }
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mGpApater = new GridphotoApater();
                        mGViewSendMsg.setAdapter(mGpApater);
                        pDialog.dismiss();

                    }
                });

            }
        }).start();
    }

    private void initData() {
        mGpApater = new GridphotoApater();
        mGViewSendMsg.setAdapter(mGpApater);
        Drawable drawable = getResources().getDrawable(R.drawable.btn_add_img);
        BitmapDrawable db = (BitmapDrawable) drawable;
        mBm = db.getBitmap();
        photolist.add(mBm);
        photoStringlist.add("addImage");
    }

    @Override
    public void initToolBar() {

    }

    private void initPhotoWindow() {
        View photoView = LayoutInflater.from(this).inflate(
                R.layout.editdata_selectphoto, null);
        photowindow = new PopupWindow(photoView, LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        photowindow.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.home_bg)));
        photoView.findViewById(R.id.button_photo1).setOnClickListener(this);
        photoView.findViewById(R.id.button_photo2).setOnClickListener(this);
        photoView.findViewById(R.id.button_photocall).setOnClickListener(this);
        photowindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                img.setVisibility(View.GONE);
            }
        });
        photowindow.setFocusable(true);
        photowindow.setOutsideTouchable(true);
        photowindow.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM, 0,
                0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendmessage_cancel:
                isCancel();
                break;
            case R.id.sendmessage_OK:
                if (!isSend) {
                    SendMessage();
                    isSend = true;
                }
                break;
            case R.id.button_photo1:
                // 相册
                toPhotoalbum();
                photowindow.dismiss();
                break;
            case R.id.button_photo2:
                // 相机获取
                toTakepic();
                photowindow.dismiss();
                break;
            case R.id.button_photocall:
                photowindow.dismiss();
                break;
        }
    }

    /**
     * 进入相册
     */
    private void toPhotoalbum() {

        // 相册
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);

        getAlbum.setType("image/*");

        startActivityForResult(getAlbum, REQUEST_PHOTO_ALBUM);
    }

    /**
     * 进入相机
     */
    private void toTakepic() {
        long times = System.currentTimeMillis();
        // 相机获取
        outImage = new File(File.separator + "GankGirl" + File.separator + "FriendsCircle" + File.separator+ times + "msg.jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(outImage);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outImage != null) {
            outState.putString("imagPath", outImage.getAbsolutePath());
            List<byte[]> photoBytes = new ArrayList<byte[]>();

            outState.putStringArrayList("photoStringlist", photoStringlist);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHOTO_ALBUM) {
            // try {
            Uri originalUri = data.getData(); // 获得图片的uri
            startPhotoZoom(StorageUtil.getPath(SendMessageActivity.this,
                    originalUri));
            // startPhotoZoom(Uri.fromFile(new
            // File(StorageUtil.getPath(SendMessageActivity.this,
            // originalUri))));
        }
        if (requestCode == REQUEST_CAMERA) {
            if (outImage != null) {
                startPhotoZoom(outImage.getAbsolutePath());
            } else {
                SnackbarUtil.showMessage(findViewById(R.id.main), "获取图片路径失败");
            }

            // startPhotoZoom(Uri.fromFile(outImage.getAbsoluteFile()));
        }

    }

    private void SendMessage() {
    }

    private void isCancel() {
        finish();
    }

    /**
     * 实现图片剪辑，
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 10);
        intent.putExtra("aspectY", 9);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, 5);

    }

    /**
     * 实现图片剪辑，
     *
     * @param paths
     */
    public void startPhotoZoom(String paths) {
        // 先这样吧，不裁剪了
        Bitmap bitmap = BitmapUtil.getSmallBitmap(paths, 320);
        byte[] bitmapByte = BitmapUtil.Bitmap2Bytes(bitmap);
        if (bitmapByte == null || bitmapByte.length == 0) {
            SnackbarUtil.showMessage(findViewById(R.id.main), "图片破损或者获取不到图片资源");
            return;
        }
        Log.d("lcc", "图片大小：" + bitmapByte.length / 1024);
        int size = bitmapByte.length / 1024 / 1024;
        if (size > 3) {
            SnackbarUtil.showMessage(findViewById(R.id.main), "请选择小于3m的图片上传");
            return;
        }

        photolist.add(photolist.size() - 1, bitmap);
        photoStringlist.add(photoStringlist.size() - 1, paths);
        mGpApater.notifyDataSetChanged();
    }

    private final class GridphotoApater extends BaseAdapter {

        @Override
        public int getCount() {
            return photolist.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, final View view, ViewGroup parent) {
            View inflate = getLayoutInflater().inflate(
                    R.layout.sendmsg_gridphoto, null);
            inflate.setLayoutParams(new GridView.LayoutParams(
                    parent.getWidth() / 3, parent.getWidth() / 3));

            ImageView ivpic = (ImageView) inflate.findViewById(R.id.iv_pic);
            ImageView ivdelete = (ImageView) inflate
                    .findViewById(R.id.iv_delete);
            Bitmap bitmap = photolist.get(position);
            ivpic.setImageBitmap(bitmap);

            final int pos = position;
            View.OnClickListener piClickListener = new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.iv_pic:
                            img.setVisibility(View.VISIBLE);
                            AppUtil.closeSoftInput(SendMessageActivity.this);
                            initPhotoWindow();
                            break;
                        case R.id.iv_delete:
                            photolist.remove(pos);
                            photoStringlist.remove(pos);
                            mGpApater.notifyDataSetChanged();
                            if (ismaxphoto) {
                                photolist.add(mBm);
                                photoStringlist.add("addImage");
                                ismaxphoto = false;
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
            if (position == photolist.size() - 1 && !ismaxphoto) {
                ivdelete.setVisibility(View.GONE);

                ivpic.setOnClickListener(piClickListener);
            }
            if (photolist.size() == 10) {
                photolist.remove(9);
                photoStringlist.remove(9);
                ismaxphoto = true;
            }
            ivdelete.setOnClickListener(piClickListener);

            return inflate;
        }

    }

}
