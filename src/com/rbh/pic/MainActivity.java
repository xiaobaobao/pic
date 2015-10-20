package com.rbh.pic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements OnClickListener{
	
	//android调用系统相机和相册进行拍照裁剪处理，解决不同安卓版本存在无法加载相册的问题，处理了是否有sd卡的存在的情况
	private static final int IMAGE_REQUEST_CODE = 0; 
	private static final int SELECT_PIC_KITKAT = 3; 
	private static final int CAMERA_REQUEST_CODE = 1; 
	private static final int RESULT_REQUEST_CODE = 2; 
	private static final String IMAGE_FILE_NAME_01 = "face.jpg"; 
	private Uri personPath = null; 
	private Uri companyPath = null; 
	private static final String IMAGE_FILE_NAME_02= "logo.jpg"; 
	private Dialog mydialog; 
	private boolean isPic ; 
	private boolean isOpen =false; 
	private boolean sdCardExist=true; 
	
	
	
	ImageView imageView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageview);
        
    }

    





@Override
public void onClick(View v) {
	switch (v.getId()) {
	case R.id.paizhao:
		paizhao();
		break;
case R.id.xiangce:
	xiangche();
		break;

	default:
		break;
	}
}
    
    
    
    
    
    public void paizhao(){
    	 Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
         // 判断存储卡是否可以用，可用进行存储  
           if (isPic) {
               intentFromCapture.putExtra(  
                       MediaStore.EXTRA_OUTPUT,  
                       Uri.fromFile(new File(Environment  
                               .getExternalStorageDirectory()+"/ttx",  
                               IMAGE_FILE_NAME_01))); 
           }else{
               intentFromCapture.putExtra(  
                       MediaStore.EXTRA_OUTPUT,  
                       Uri.fromFile(new File(Environment  
                               .getExternalStorageDirectory()+"/ttx",  
                               IMAGE_FILE_NAME_02))); 
           }


         startActivityForResult(intentFromCapture,  
                 CAMERA_REQUEST_CODE);
    }
    
    public void xiangche(){
    	// pickPhotoFromGallery();// 从相册中去获取
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);  
        intent.addCategory(Intent.CATEGORY_OPENABLE);  
        intent.setType("image/*");  
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {  
            startActivityForResult(intent,SELECT_PIC_KITKAT);  
        } else {  
            startActivityForResult(intent,IMAGE_REQUEST_CODE);  
        }
    }
    
    
    
    
    
    
    
    
    
  //实现返回方法进行处理 
    public void onActivityResult(int requestCode, int resultCode, Intent data) { 
    // 结果码不等于取消时候

        if (resultCode != Activity.RESULT_CANCELED) {  
            switch (requestCode) {  
            case IMAGE_REQUEST_CODE:  
                startPhotoZoom(data.getData());  
                break;  
            case SELECT_PIC_KITKAT:  
                startPhotoZoom(data.getData());  
                break;  
            case CAMERA_REQUEST_CODE:
                if (!sdCardExist) {
                    //如果不存在SD卡，进行提示  
                  Toast.makeText(this, "没有内存卡，无法拍照", Toast.LENGTH_SHORT).show();  
                  finish();
                }else{
                    File tempFile =null;
                    if (isPic) {

                         tempFile = new File(Environment.getExternalStorageDirectory()+ "/ttx/",IMAGE_FILE_NAME_01);  
                    }else{
                        tempFile = new File(Environment.getExternalStorageDirectory()+ "/ttx/",IMAGE_FILE_NAME_02);  

                    }
                    startPhotoZoom(Uri.fromFile(tempFile));  
                }

                break;  
            case RESULT_REQUEST_CODE:  
                Bundle bundle = data.getExtras();
                Bitmap photo = bundle.getParcelable("data");
                //photo = ThumbnailUtils.extractThumbnail(photo, 50, 50);  
                if (isPic) {
                	imageView.setImageBitmap(photo);
                }else{
                	imageView.setImageBitmap(photo);
                }
                saveBitmap(photo);
                break;  
            }  
        }  
        super.onActivityResult(requestCode, resultCode, data);  
    }  

    
    
    
  //实现裁剪方法
    public void startPhotoZoom(Uri uri) {  
        if (uri == null) {  
            Log.i("tag", "The uri is not exist.");  
            return;  
        }  

        Intent intent = new Intent("com.android.camera.action.CROP");  
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {  
            String url=Utils.getPath(MainActivity.this,uri);  
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");  
        }else{  
            intent.setDataAndType(uri, "image/*");  
        }  

        // 设置裁剪  
        intent.putExtra("crop", "true");  
        // aspectX aspectY 是宽高的比例  
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);  
        // outputX outputY 是裁剪图片宽高  
        intent.putExtra("outputX", 300);  
        intent.putExtra("outputY", 300);  
        intent.putExtra("return-data", true);  
        startActivityForResult(intent, RESULT_REQUEST_CODE);  
    }  
    
    
  //保存的方法
    public void saveBitmap(Bitmap mBitmap) {  
        File f=null;
        if (isPic) {
             f = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME_01); 
             personPath = Uri.fromFile(f);
        }else{
             f = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME_02);  
             companyPath = Uri.fromFile(f);
        }
        try {  
            f.createNewFile();  
            FileOutputStream fOut = null;  
            fOut = new FileOutputStream(f);  
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);  
            fOut.flush();  
            fOut.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }




}
