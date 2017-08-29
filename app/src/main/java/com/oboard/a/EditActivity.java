package com.oboard.a;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.view.Menu;

public class EditActivity extends AppCompatActivity {

    EditText text;
    ImageView view;

    int edit_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(S.get("plast_name", "你怕是有毒"));
        edit_id = S.get("plast_id", -1);
        setContentView(R.layout.edit);

        text = (EditText)findViewById(R.id.edit_text);
        view = (ImageView)findViewById(R.id.edit_view);
        text.setText(S.get("pcode" + edit_id, ""));

        switch (S.get("ptype" + edit_id, "com")) {
            case "com":
                view.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            v.setBackgroundDrawable(new ScriptDrawable(EditActivity.this, text.getText().toString()));
                        }
                    });
                view.performClick();

                break;
            case "text":
                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0));
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                break;
            case "image":
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0));
                view.setBackgroundDrawable(new ScriptDrawable(EditActivity.this, text.getText().toString()));
                view.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, 486);  
                        }
                    });
                break;
            default:
                break;
        }

        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) getSupportActionBar().setElevation(0);

    }

    //重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 0, 0, "全屏浏览").setIcon(R.drawable.full);
        return true;
    }

    @Override  
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  

        if (requestCode == 486 && resultCode == RESULT_OK && null != data) {  
            Uri selectedImage = data.getData();  
            String[] filePathColumn = {MediaStore.Images.Media.DATA};  

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);  
            cursor.moveToFirst();  
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);  
            String picturePath = cursor.getString(columnIndex);
            text.setText("drawimage>" + picturePath + ";");
            view.setBackgroundDrawable(new ScriptDrawable(EditActivity.this, text.getText().toString()));
            cursor.close();  
        }  
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            S.put("pcode" + edit_id, text.getText().toString());
            finish();
        } else if(item.getTitle().equals("全屏浏览")) {
            startActivity(new Intent(this, FullActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
