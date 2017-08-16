# 菲林
***
##### 简介

##### 菲林——是一款遵循Material Design的图片分享软件。
以一种系统提供的一些简单纯净的排版，加上用户对自己的照片添加的滤镜，添加贴纸，裁剪，旋转等处理，做出一种白色明信片，更好的纪录用户的点滴。
* 用户管理[用户登录，注册，关注其他用户，搜索用户，修改个人信息，修改密码等]
* 卡片管理[添加卡片，删除卡片，分享卡片，查看卡片等]
* 技巧管理[查看技巧，投稿等]

***

##### 开发工具
* 开发语言： Java
* 开发平台： Android Studio
*  服务器：[Bmob后端云服务器](http://www.bmob.cn/)
*  测试工具：魅族魅蓝 Metal   主屏5.5英寸           分辨率1080*1920  480dp(未对所有设备适配)


***

##### 权限说明
	 `
	 <!--允许联网 -->
	 <uses-permission android:name="android.permission.INTERNET" />
	 <!--获取GSM（2g）、WCDMA（联通3g）等网络状态的信息  -->
	 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	 <!--获取wifi网络状态的信息 -->
	 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	 <!--保持CPU 运转，屏幕和键盘灯有可能是关闭的,用于文件上传和下载 -->
	 <uses-permission android:name="android.permission.WAKE_LOCK" />
	 <!--获取sd卡写的权限，用于文件上传和下载-->
	 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	 <!--允许读取手机状态 用于创建BmobInstallation-->
	 <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
	 <!--允许弹出AlertDialog-->
	 <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
	 <uses-permission android:name="android.permission.SYSTEM_OVERLAY_WINDOW" />
	 <!--允许使用摄像头-->
	 <uses-permission android:name="android.permission.CAMERA"/>  
	`

***

##### 开源技术
* **LRecyclerView**
* **butterknife**
* **fresco**
* **zxing**
* **imageeditlibrary**


