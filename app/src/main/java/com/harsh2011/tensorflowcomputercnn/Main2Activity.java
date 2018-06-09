package com.harsh2011.tensorflowcomputercnn;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.harsh2011.tensorflowcomputercnn.views.DrawModel;
import com.harsh2011.tensorflowcomputercnn.views.DrawView;



import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements  View.OnTouchListener {

    private static final int PIXEL_WIDTH = 28;

    private Button clearBtn, classBtn;

    private DrawModel drawModel;
    private DrawView drawView;
    private PointF mTmpPiont = new PointF();


    private float mLastX;
    private float mLastY;

    private EditText folder;

    @Override
    // In the onCreate() method, you perform basic application startup logic that should happen
    //only once for the entire life of the activity.
    protected void onCreate(Bundle savedInstanceState) {
        //initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //get drawing view from XML (where the finger writes the number)
        drawView = (DrawView) findViewById(R.id.draw);
        //get the model object
        drawModel = new DrawModel(PIXEL_WIDTH, PIXEL_WIDTH);

        //init the view with the model object
        drawView.setModel(drawModel);
        // give it a touch listener to activate when the user taps
        drawView.setOnTouchListener(this);

        //clear button
        //clear the drawing when the user taps
        clearBtn = (Button) findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawModel.clear();
                drawView.reset();
                drawView.invalidate();
            }
        });

        folder = (EditText) findViewById(R.id.folder);

        classBtn = (Button) findViewById(R.id.btn_class);
        classBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = drawView.getmOffscreenBitmap();
                saveTempBitmap(bitmap);
                Toast.makeText(Main2Activity.this, "saved",Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void saveTempBitmap(Bitmap bitmap) {

        saveImage(bitmap);

    }

    private void saveImage(Bitmap finalBitmap) {

        String fol = folder.getText().toString();

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Dataset");
        myDir.mkdirs();

        File nameDir = new File(root + "/Dataset/"+fol);
        nameDir.mkdirs();


        System.out.println(nameDir.getAbsolutePath());

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Dataset_"+ timeStamp +".jpg";

        File file = new File(nameDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //the activity lifecycle

    @Override
    //OnResume() is called when the user resumes his Activity which he left a while ago,
    // //say he presses home button and then comes back to app, onResume() is called.
    protected void onResume() {
        drawView.onResume();
        super.onResume();
    }

    @Override
    //OnPause() is called when the user receives an event like a call or a text message,
    // //when onPause() is called the Activity may be partially or completely hidden.
    protected void onPause() {
        drawView.onPause();
        super.onPause();
    }



    @Override

    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            processTouchDown(event);
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            processTouchMove(event);
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            processTouchUp();
            return true;
        }
        return false;
    }

    private void processTouchDown(MotionEvent event) {

        mLastX = event.getX();
        mLastY = event.getY();

        //user them to calcualte the position
        drawView.calcPos(mLastX, mLastY, mTmpPiont);

        float lastConvX = mTmpPiont.x;
        float lastConvY = mTmpPiont.y;

        drawModel.startLine(lastConvX, lastConvY);
    }

    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        drawView.calcPos(x, y, mTmpPiont);
        float newConvX = mTmpPiont.x;
        float newConvY = mTmpPiont.y;
        drawModel.addLineElem(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        drawView.invalidate();
    }

    private void processTouchUp() {
        drawModel.endLine();
    }
}
