package com.oboard.a;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class FullActivity extends Activity {
    
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = new View(this);
        v.setBackground(new ScriptDrawable(this,"rgb>255>255>255;drawrect;rgb>0>0>0;" + S.get("pcode" + S.get("plast_id",0), "")));
        v.setOnClickListener(new View.OnClickListener() {
           public void onClick(View view) {
               finish();
           } 
        });
        
        setContentView(v);
    }
    
}
