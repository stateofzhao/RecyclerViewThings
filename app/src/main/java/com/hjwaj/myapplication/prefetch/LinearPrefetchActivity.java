package com.hjwaj.myapplication.prefetch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjwaj.myapplication.R;
import com.hjwaj.myapplication.VH;

/**
 * 通过设置setItemPrefetchEnabled、Thread.sleep(DELAY)
 * 可以开启GPU呈现模式分析，对比观察性能
 *
 * 另外实现了提前5个item预加载
 */
public class LinearPrefetchActivity extends Activity {
    private static final int DELAY = 64;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_with_simple_recycler_view);
        RecyclerView rv = findViewById(R.id.rv);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
        PreloadLinearLayoutManager llm = new PreloadLinearLayoutManager(this);
        llm.setPreloadItemCount(5);
//        llm.setItemPrefetchEnabled(false);
        rv.setLayoutManager(llm);
        rv.setAdapter(new RecyclerView.Adapter() {
            private int count = 11;

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                Log.d("RecyclerView", "onCreateViewHolder " + viewType);
                TextView tv = new TextView(parent.getContext());
                tv.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 360));
                tv.setTextSize(20);
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(0xff111111);
                tv.setBackgroundColor(0xffffffff);
//                try {
//                    Thread.sleep(DELAY);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                return new VH(tv);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                int viewType = getItemViewType(position);
                switch (viewType) {
                    case 0:
                        Log.d("RecyclerView", "onBindViewHolder " + position);
                        ((TextView) holder.itemView).setText("" + position);
                        holder.itemView.setBackgroundColor((position & 1) == 0 ? 0xffffffff : 0xffeeeeee);
//                        try {
//                            Thread.sleep(DELAY);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        break;
                    case 1:
                        Log.d("RecyclerView", "onBindViewHolder loading");
                        ((TextView) holder.itemView).setText("Loading...");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("RecyclerView", "notifyDataSetChanged called");
                                count += 10;
                                notifyDataSetChanged();
                            }
                        }, 2000);
//                        try {
//                            Thread.sleep(DELAY);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        break;
                }
            }

            @Override
            public int getItemCount() {
                return count;
            }

            @Override
            public int getItemViewType(int position) {
                if (position == getItemCount() - 1) return 1;
                return 0;
            }
        });
    }
}
