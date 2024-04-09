package com.greyhouse34.gh34mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


//import class for Uploading part start

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.webkit.ValueCallback;

import java.io.File;
import java.io.IOException;

//import class for Uploading part End



public class MainActivity extends AppCompatActivity {

    private WebView web;
    String webURL = "https://greyhouse34.co.za";
    //private LinearLayout damymainbg;
    private ProgressBar daprogressBar;
   // private ProgressDialog daprogressDialog;
    //private  ScrollView dascrollView;
    private SwipeRefreshLayout swipeRefreshLayout;



    public Context context;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;

    // the same for Android 5.0 methods only
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    RelativeLayout relativeLayout;
    Button danointernetRetryBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //animate bg gradient realquick
       // damymainbg = findViewById(R.id.mymainbg);
       // TransitionDrawable trans = (TransitionDrawable) damymainbg.getBackground();
        //trans.startTransition(2000);

        //Now Lets FireUp the mighty we view
        web = (WebView) findViewById(R.id.mywebview);
        web.loadUrl(webURL);

        WebSettings daWebsetting = web.getSettings();
        daWebsetting.setJavaScriptEnabled(true);




        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                internetChecker();
                super.onReceivedError(view, request, error);
            }
        });

        //improve webview performance
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        web.getSettings().setAppCacheEnabled(true);
        web.getSettings().setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath()+"/cache");
        web.getSettings().setDatabaseEnabled(true);
        web.getSettings().setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath()+"/databases");
        web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        daWebsetting.setDomStorageEnabled(true);
        daWebsetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        daWebsetting.setUseWideViewPort(true);
        daWebsetting.setSavePassword(true);
        daWebsetting.setSaveFormData(true);
        daWebsetting.setEnableSmoothTransition(true);


        //progressBar
        daprogressBar = (ProgressBar) findViewById(R.id.myprogressbar);
        //scroller
        //dascrollView = (ScrollView) findViewById(R.id.myscrollview);
        web.setWebChromeClient(new WebChromeClient(){
           //@Override
            public void onProgressChanged(WebView view, int progress){
                daprogressBar.setProgress(progress);
                if(progress<100 && daprogressBar.getVisibility() == ProgressBar.GONE){
                    daprogressBar.setVisibility(ProgressBar.VISIBLE);
                }
                if(progress == 100){
                    //dascrollView.scrollTo(0, 0);
                    daprogressBar.setVisibility(ProgressBar.GONE);
                }
            }

            //-------------------------------------------------------------------
            //code for showing "file chooser"

            // for Lollipop, all in one
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    // create the file where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }

                    // continue only if the file was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                return true;
            }

            // creating image files (Lollipop only)
            private File createImageFile() throws IOException {

                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere");

                if (!imageStorageDir.exists()) {
                    imageStorageDir.mkdirs();
                }

                // create an image file name
                imageStorageDir = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                return imageStorageDir;
            }

            // openFileChooser for Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;

                try {
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere");

                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs();
                    }

                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                    mCapturedImageURI = Uri.fromFile(file); // save to the private variable

                    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(i, getString(R.string.image_chooser));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Camera Exception:" + e, Toast.LENGTH_LONG).show();
                }

            }

            // openFileChooser for Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // openFileChooser for other Android versions
            /* may not work on KitKat due to lack of implementation of openFileChooser() or onShowFileChooser()
               https://code.google.com/p/android/issues/detail?id=62220
               however newer versions of KitKat fixed it on some devices */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

            //End Of code for showing "file chooser"
            //---------------------------------------------------------------------------------------


        });

        //progressDialog (Did Not Work Properly and for now im just lazy to debug this)
        /*
        daprogressDialog = new ProgressDialog(MainActivity.this);
        daprogressDialog.setMessage("Loading Please Wait...");
        web.setWebViewClient(new daLoader(daprogressDialog));
         */

        //Pull to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.myswiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        web.reload();
                    }
                },2000);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_blue_bright)
        );

        //Storage permission for downloads
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                Log.d("permission", "Permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, 1);
            }
        }

        //do the Download
        web.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie",cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url,contentDisposition,mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,contentDisposition,mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(),"Downloading File", Toast.LENGTH_SHORT).show();
            }
        });

        //Checking Internet Connection thingies
        danointernetRetryBTN = (Button) findViewById(R.id.mynointernetRetryBTN);
        relativeLayout = (RelativeLayout) findViewById(R.id.mynointernet);
        internetChecker();
        danointernetRetryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                internetChecker();
            }
        });
    }



/*
    public class daLoader extends WebViewClient{
        ProgressDialog daprogressDialog;
        public daLoader(ProgressDialog daprogressDialog){
            this.daprogressDialog = daprogressDialog;
            daprogressDialog.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(webURL);
            return super.shouldOverrideUrlLoading(view,webURL);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(daprogressDialog.isShowing()){
                daprogressDialog.dismiss();
            }
        }
    }

 */


    // return here when file selected from camera or from SD Card
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // code for all versions except of Lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e, Toast.LENGTH_LONG).show();
                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }

        } // end of code for all versions except of Lollipop

        // start of code for Lollipop only
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.getData() == null) {
                    // if there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        } // end of code for Lollipop only
    }



    @Override
    public void onBackPressed() {
        if(web.canGoBack()){
            web.goBack();
        }else{
            super.onBackPressed();
        }
    }

    public void internetChecker(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobiledata = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobiledata.isConnected() || wifi.isConnected()){
            web.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            web.reload();
        }else{
            web.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }


}