package com.androidkt.tensorflowlite;

import android.app.Activity;
import com.firebase.client.Firebase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import com.androidkt.tensorflowlite.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;

    Interpreter tflite;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Bitmap photo;
    public static final String TAG = "sample tag";
    private static final String modelFile="model.tflite";
    private Firebase mref;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN=9001;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        auth=FirebaseAuth.getInstance();
        FirebaseUser usr=auth.getCurrentUser();
        if(usr==null)
        {
            Intent intent=new Intent(this,login.class);
            startActivity(intent);
        }


        mref=new Firebase("https://begin0-d8003.firebaseio.com/users");


        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        MappedByteBuffer a;
        try{
            a=loadModelFile(this);
        }
        catch (Exception e){
            Log.e(TAG, "onCreate: file not found" );
        }
        try {
            tflite = new Interpreter(loadModelFile(this));
            Toast.makeText(this,"tflite created",Toast.LENGTH_LONG).show();
            Log.e(TAG, "onCreate: model built" );
        }
        catch (IOException e) {
            Log.e(TAG, "onCreate: model not built" );
            e.printStackTrace();
        }
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
        final Button predict = (Button) this.findViewById(R.id.button);
        predict.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(photo!=null)
                    predict(photo);
                else
                    Toast.makeText(MainActivity.this, "Click an image first", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void predict(Bitmap photo)
    {
        photo = Bitmap.createScaledBitmap(photo,256,256, true);
        TextView tv1= this.findViewById(R.id.tv1);
        int[] inp=new int[256*256];
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(photo);
        photo.getPixels(inp,0,photo.getWidth(),0,0,photo.getWidth(),photo.getHeight());
        Log.e(TAG, "predict: photo is"+inp[9999]);
        float[][] out = new float[1][15];
        tflite.run(byteBuffer,out);
        int max_index=1;
        float max_num=-1.0f;
        for(int i=1;i<15;i++){
            if(max_num < out[0][i]) {
                max_index = i;
                max_num = out[0][i];
            }
        }
        String result="";
        switch (max_index) {
            case 0: result="Pepper Bell Bacterial Spot";
                break;
            case 1: result="Healthy";
                break;
            case 2: result="Potato Early Blight";
                break;
            case 3: result="Healthy";
                break;
            case 4: result="Potato Late Blight";
                break;
            case 5: result="Tomato Target Spot";
                break;
            case 6: result="Tomato Mosaic Virus";
                break;
            case 7: result="Tomato Yellow Leaf Curl Virus";
                break;
            case 8: result="Tomato Bacterial Spot";
                break;
            case 9: result="Tomato Early Blight";
                break;
            case 10: result="Healthy";
                break;
            case 11: result="Tomato Late Blight";
                break;
            case 12: result="Tomato Leaf Mold";
                break;
            case 13: result="Tomato Spetorai Leaf Spot";
                break;
            case 14: result="Tomato Spider Mites";
                break;

        }
        tv1.setText(result);
        Log.e(TAG, "predict:prediction is "+Arrays.deepToString(out) );
    }
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelFile);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        int inputSize=256;
        float IMAGE_MEAN=1;
        float IMAGE_STD=127.5f;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 1 * 256 * inputSize * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[inputSize * inputSize];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat((((val >> 16) & 0xFF))/IMAGE_STD-1);
                byteBuffer.putFloat((((val >> 8) & 0xFF))/IMAGE_STD-1);
                byteBuffer.putFloat((((val) & 0xFF))/IMAGE_STD-1);
            }
        }
        return byteBuffer;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }
}

