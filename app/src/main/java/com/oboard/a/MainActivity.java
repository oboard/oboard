package com.oboard.a;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private BottomNavigationBar mBottomBar;
    
    FragmentManager fm;
    FragmentTransaction ft;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private HomeFragment mHomeFragment = new HomeFragment();
    private RunFragment mRunFragment = new RunFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        S.init(this,"com.oboard.a");
        S.put("max", S.get("max", 0));
        S.ok();
        
        initActionBar();
        initBottomBar();
        
        setDefaultFragment();
    }
    
    public void onAdds(final View v) {
        HomeFragment.mAdds.setVisibility(View.GONE);
        HomeFragment.mAdd.show();
        if (v instanceof LinearLayout) return;

        final EditText t = new EditText(this);
        t.setSingleLine(true);
        new AlertDialog.Builder(this)
            .setMessage("项目名")
            .setView(t)
            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    HomeFragment.mRecyclerViewAdapter.addItem(HomeFragment.mRecyclerViewAdapter.getItemCount(), t.getText().toString());
                    S.put("pname" + S.get("max", 0), t.getText().toString());
                    S.put("ptype" + S.get("max", 0), v.getTag().toString());
                    if (v.getTag().toString().equals("com"))
                        S.put("pcode" + S.get("max", 0), ">HelloWorld;");
                    else
                        S.put("pcode" + S.get("max", 0), "");
                    S.put("max", S.get("max", 0) + 1);
                    S.ok();
                }
            }).setPositiveButton("取消", null)
            .show();
    }
    
    
    private void setDefaultFragment() {  
        fm = getSupportFragmentManager();  
        ft = fm.beginTransaction();
        ft.replace(R.id.main_frame, mHomeFragment);
        ft.commit();
        
        fragments.add(mHomeFragment);
        fragments.add(mRunFragment);
    }
    
    public void initBottomBar() {
        mBottomBar = (BottomNavigationBar)findViewById(R.id.main_bottom_navigation_bar);
        mBottomBar.setMode(BottomNavigationBar.MODE_DEFAULT);
        mBottomBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomBar.addItem(new BottomNavigationItem(R.drawable.home, "主页").setActiveColorResource(R.color.gray))
            .addItem(new BottomNavigationItem(R.drawable.run, "流程").setActiveColorResource(R.color.red))
            .setFirstSelectedPosition(0)
            .initialise();

        mBottomBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                public void onTabSelected(int position) {
                    if (fragments != null) {
                        if (position < fragments.size()) {
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            Fragment fragment = fragments.get(position);
                            if (fragment.isAdded()) {
                                ft.replace(R.id.main_frame, fragment);
                            } else {
                                ft.add(R.id.main_frame, fragment);
                            }
                            ft.commitAllowingStateLoss();
                        }
                    }
                }
                public void onTabReselected(int position) {}
                public void onTabUnselected(int position) {
                    if (fragments != null) {
                        if (position < fragments.size()) {
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            Fragment fragment = fragments.get(position);
                            ft.remove(fragment);
                            ft.commitAllowingStateLoss();
                        }
                    }
                }
            });
        //fragments = getFragments();
        //setDefaultFragment();
        //bottomNavigationBar.setTabSelectedListener(this);
    }


    public void initActionBar() {
        mToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
    }
}
