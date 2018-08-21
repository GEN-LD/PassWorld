package scut.bbt.passworld.concrete.splash;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import scut.bbt.passworld.R;
import scut.bbt.passworld.concrete.main.MainActivity;

public class StartActivity extends Activity {

    //权限请求码
    public int PERMISSION_REQ = 1;
    //所有权限内容
    private String[] mPermission = new String[] {
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //未获得的权限
    private List<String> mRequestPermission = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for(String permission : mPermission) {
                if(ContextCompat.checkSelfPermission(StartActivity.this,permission) != PackageManager.PERMISSION_GRANTED) {
                    mRequestPermission.add(permission);
                }
            }
            if( !mRequestPermission.isEmpty() ){
                this.requestPermissions(mRequestPermission.toArray(new String[mRequestPermission.size()]),1);
//                String[] spermission = mRequestPermission.toArray(new String[mRequestPermission.size()]);
//                ActivityCompat.requestPermissions(StartActivity.this,spermission,1);
            } else {
                jumpToMain();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //版本兼容
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if(requestCode == PERMISSION_REQ) {
        }
        switch (requestCode){
            case 1:
                if(grantResults.length > 0) {
                    for(int reslut : grantResults) {
                        if(reslut != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(StartActivity.this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(StartActivity.this,"Permission未知错误",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void jumpToMain(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(timerTask,1000);
    }
}
