package com.scchen2.head_upload;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.scchen2.head_upload.UploadDialog.CHOOSE_PICTURE;
import static com.scchen2.head_upload.UploadDialog.CROP_SMALL_PICTURE;
import static com.scchen2.head_upload.UploadDialog.TAKE_PICTURE;
import static com.scchen2.head_upload.UploadDialog.tempUri;

public class SimpleUpLoadDialog extends Activity implements EasyPermissions.PermissionCallbacks{
    static private OnGetListener getListener;
    static private boolean isCroped;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_load_dialog);
        checkNeedPermission();
    }


    public static void getPicUrlFromDialog(Activity preViousActivity, OnGetListener listener,boolean croped) {
        getListener = listener;
        isCroped = croped;
        Intent intent = new Intent(preViousActivity, SimpleUpLoadDialog.class);
        preViousActivity.startActivity(intent);
    }

    public interface OnGetListener {
        void onGet(Uri uri);
    }

    /**
     * 页面返回时的操作
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    if (isCroped) {
                        tempUri = Utils.startPhotoZoom(tempUri, this); // 开始对图片进行裁剪处理
                        Utils.showInAlbum(this, tempUri);
                    } else {
                        getListener.onGet(tempUri);
                        getListener = null;
                        this.finish();
                    }
                    break;
                case CHOOSE_PICTURE:
                    if (isCroped) {
                        tempUri = Utils.startPhotoZoom(data.getData(), this); // 开始对图片进行裁剪处理
                    } else {
                        getListener.onGet(tempUri);
                        getListener = null;
                        this.finish();
                    }
                    break;
                case CROP_SMALL_PICTURE:
                    Log.i("beginTime", "beginTime");
                    if (data != null) {
//                        Bundle extras = data.getExtras();
//                        if (extras != null && null != getCropedListener) {
//                            final Bitmap mPhoto = extras.getParcelable("data");
//                        }
                        getListener.onGet(tempUri);
                        getListener = null;
                        this.finish();
                    }
                    break;
                default:
                    break;
            }
        }else {
            this.finish();
        }
    }
    private void checkNeedPermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "应用需要请求部分权限", 1, permissions);
        }else {
            new UploadDialog(this).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // Some permissions have been granted
        new UploadDialog(this).show();
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // Some permissions have been denied
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AlertDialog.Builder(getApplicationContext()).setCancelable(false)
                    .setTitle("需要权限")
                    .setMessage("没有请求的权限，此应用程序可能无法正常工作。打开应用程序设置以修改应用程序权限。")
                    .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Create app settings intent
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }).setNegativeButton(getString(android.R.string.cancel), null).show();
        }
    }
}
