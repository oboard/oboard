package com.oboard.a;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.oboard.a.MyRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    //显示的数据
    static MyRecyclerViewAdapter mRecyclerViewAdapter;

    static RecyclerView mRecyclerView;
    static FloatingActionButton mAdd;
    static LinearLayout mAdds;

    float o_y = 0;

    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.home, container, false);
        initRecyclerView(view);
        initAdd(view);

        return view;
    }

    private void initAdd(View v) {
        mAdd = (FloatingActionButton)v.findViewById(R.id.home_add);
        mAdds = (LinearLayout)v.findViewById(R.id.home_adds);
        mAdd.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mAdd.hide();
                    mAdds.setVisibility(View.VISIBLE);
                } 
            });
    }

    private void initRecyclerView(View v) {
        //1.找到控件
        mRecyclerView = (RecyclerView)v.findViewById(R.id.home_recycler);
        //2.声名为瀑布流的布局方式: 3列,垂直方向
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //3.为recyclerView设置布局管理器
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        //3.创建适配器
        mRecyclerViewAdapter = new MyRecyclerViewAdapter(getContext());
        //设置添加,移除item的动画,DefaultItemAnimator为默认的
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //4.设置适配器
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        //添加点击事件
        mRecyclerViewAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnRecyclerItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    //Toast.makeText(MainActivity.this,"单击了:"+mDatas.get(position),Toast.LENGTH_SHORT).show();
                    //adapter.addItem(position, "添加的内容");
                    S.put("plast_id", position);
                    S.put("plast_name", S.get("pname" + position, ""));
                    S.ok();
                    startActivity(new Intent(getContext(), EditActivity.class));
                }
            });
        //设置长按事件
        mRecyclerViewAdapter.setOnItemLongClickListener(new MyRecyclerViewAdapter.onRecyclerItemLongClickListener() {
                @Override
                public void onItemLongClick(View view, final int position) {
                    //Toast.makeText(MainActivity.this,"长按了:"+mDatas.get(position),Toast.LENGTH_SHORT).show();
                    final EditText t = new EditText(getContext());
                    t.setSingleLine(true);
                    t.setText(S.get("pname" + position, ""));
                    new AlertDialog.Builder(getContext())
                        .setMessage("项目名")
                        .setView(t)
                        .setNeutralButton("删除", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mRecyclerViewAdapter.removeItem(position);
                                S.delIndex("pname", "max", position);
                                S.delIndex("ptype", "max", position);
                                S.delIndex("pcode", "max", position);
                                S.put("max", S.get("max", 0) - 1);
                                S.ok();
                            }
                        })
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mRecyclerViewAdapter.setItem(position, t.getText().toString());
                                S.put("pname" + position, t.getText().toString());
                                S.ok();
                            }
                        }).setPositiveButton("取消", null)
                        .show();
                }
            });


        mRecyclerView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent e) {
                    if (e.getAction() == e.ACTION_UP) {
                        if (e.getY() - o_y > 20 || mRecyclerView.getScrollY() < 50)
                            mAdd.show();
                        else
                            mAdd.hide();
                    } else if (e.getAction() == e.ACTION_DOWN) o_y = e.getY();
                    return false; 
                } 
            });
    }

}

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<String> list = new ArrayList<String>();//数据
    private List<String> list2 = new ArrayList<String>();//数据
    //private List<Integer> heightList;//装产出的随机数

    private OnRecyclerItemClickListener mOnItemClickListener;//单击事件
    private onRecyclerItemLongClickListener mOnItemLongClickListener;//长按事件


    public MyRecyclerViewAdapter(Context c) {
        this.context = c;
        for (int i = 0; i < S.get("max", 0); i++) {
            try {
                list.add(S.get("pname" + i, ""));
                list2.add(S.get("pcode" + i, ""));
            } catch (Exception e) {}
        }
        ////记录为每个控件产生的随机高度,避免滑回到顶部出现空白
        //heightList = new ArrayList<>();
        //for (int i = 0; i < list.size(); i++) {
        ////int height = new Random().nextInt(200) + 100;//[100,300)的随机数
        //    heightList.add(50);
        //}
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //找到item的布局
        View view = LayoutInflater.from(context).inflate(R.layout.item1, parent, false);
        return new MyViewHolder(view);//将布局设置给holder
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 绑定视图到holder,就如同ListView的getView(),但是这里已经把复用实现了,我们只需要填充数据就行.
     * 由于在复用的时候都是调用该方法填充数据,但是上滑的时候,又会随机产生高度设置到控件上,这样当滑
     * 到顶部可能就会看到一片空白,因为后面随机产生的高度和之前的高度不一样,就不能填充屏幕了,所以
     * 需要记录每个控件产生的随机高度,然后在复用的时候再设置上去
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            
            ////由于需要实现瀑布流的效果,所以就需要动态的改变控件的高度了
            //ViewGroup.LayoutParams params = holder.textView.getLayoutParams();
            //params.height = heightList.get(position);
            //holder.textView.setLayoutParams(params);

            //设置单击事件
            if (mOnItemClickListener != null) {
                holder.onView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //这里是为textView设置了单击事件,回调出去
                            //mOnItemClickListener.onItemClick(v,position);这里需要获取布局中的position,不然乱序
                            mOnItemClickListener.onItemClick(v, holder.getLayoutPosition());
                        }
                    });
            }
            //长按事件
            if (mOnItemLongClickListener != null) {
                holder.onView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //回调出去
                            mOnItemLongClickListener.onItemLongClick(v, holder.getLayoutPosition());
                            return true;//不返回true,松手还会去执行单击事件
                        }
                    });
            }
            
            //填充数据
            holder.textView.setText(list.get(position) + "");
            holder.imageView.setBackground(new ScriptDrawable(context, list2.get(position)));
        } catch (Exception e) {

        }
    }
    
    class MyViewHolder extends RecyclerView.ViewHolder {

        View onView;
        TextView textView;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item1_text);
            imageView = (ImageView) itemView.findViewById(R.id.item1_image);
            onView = itemView.findViewById(R.id.item1_on);
            
        }
    }

    /**
     * 处理item的点击事件,因为recycler没有提供单击事件,所以只能自己写了
     */
    interface OnRecyclerItemClickListener {
        public void onItemClick(View view, int position);
    }

    /**
     * 长按事件
     */
    interface onRecyclerItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }

    /**
     * 暴露给外面的设置单击事件
     */
    public void setOnItemClickListener(OnRecyclerItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * 暴露给外面的长按事件
     */
    public void setOnItemLongClickListener(onRecyclerItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setItem(int position, String value) {
        if (position > list.size()) {
            position = list.size();
        }
        if (position < 0) {
            position = 0;
        }
        /**
         * 使用notifyItemInserted/notifyItemRemoved会有动画效果
         * 而使用notifyDataSetChanged()则没有
         */
        list.set(position, value);//在集合中添加这条数据
        notifyItemChanged(position);//通知数据
    }

    /**
     * 向指定位置添加元素
     */
    public void addItem(int position, String value) {
        if (position > list.size()) {
            position = list.size();
        }
        if (position < 0) {
            position = 0;
        }
        /**
         * 使用notifyItemInserted/notifyItemRemoved会有动画效果
         * 而使用notifyDataSetChanged()则没有
         */
        list.add(position, value);//在集合中添加这条数据
        //heightList.add(position, new Random().nextInt(200) + 100);//添加一个随机高度,会在onBindViewHolder方法中得到设置
        notifyItemInserted(position);//通知插入了数据
    }

    /**
     * 移除指定位置元素
     */
    public String removeItem(int position) {
        if (position > list.size() - 1) {
            return null;
        }
        //heightList.remove(position);//删除添加的高度
        String value = list.remove(position);//所以还需要手动在集合中删除一次
        notifyItemRemoved(position);//通知删除了数据,但是没有删除list集合中的数据
        return value;
    }

}
