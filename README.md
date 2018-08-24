# head-upload（头像上传组件）

效果图:</br>
<img src="./img/des.gif"  height="530" width="320">

说明:
1. 系统头像上传组件,调用系统原生的拍照和裁剪功能，利用了一个透明的Activity的上面浮了一层Dialog。来实现组件的封装。

2. 兼容安卓7.0以后的裁剪功能。

##使用方法

```JAVA
//参数一:当前Activity,参数二:回调。参数三：是否要裁剪。
//返回的是一个本地图片存储的uri
SimpleUpLoadDialog.getPicUrlFromDialog(this, new SimpleUpLoadDialog.OnGetListener() {
                    public void onGet(Uri uri) {
                        uploadFile(uri);
                    }
                }, true);
```
