package com.ronda.posprinter.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自己写的一个对Spinner的加强版
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/06/07
 * Version: v1.0
 */

public class LSpinner<T> extends Spinner {

    protected final List<T> mData = new ArrayList<>();
    private MyAdapter adapter;

    public LSpinner(Context context) {
        super(context);
        initView();
    }

    public LSpinner(Context context, int mode) {
        super(context, mode);
        initView();
    }

    public LSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public LSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        initView();
    }

    private void initView() {
        adapter = new MyAdapter();
        setAdapter(adapter);
    }

    @Override
    public T getSelectedItem() {
        return (T) super.getSelectedItem();
    }

    //===========对数据常用的操作===============
    public void setData(T... items) {
        setData(Arrays.asList(items));
    }

    public void setData(List<? extends T> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        adapter.notifyDataSetChanged();

    }

    public void addData(T data) {
        addData(mData.size(), data);
        if (data == null) {
            return;
        }
        adapter.notifyDataSetChanged();
    }

    public void addData(int index, T data) {
        if (data == null) {
            return;
        }
        mData.add(index, data);
        adapter.notifyDataSetChanged();
    }

    public void addData(List<? extends T> data) {
        if (data == null) {
            return;
        }
        mData.addAll(data);
        adapter.notifyDataSetChanged();
    }

    public void removeData(T data) {
        if (data == null || !mData.contains(data)) {
            return;
        }

        int index = mData.indexOf(data);
        mData.remove(data);
        adapter.notifyDataSetChanged();
    }

    public void removeData(int position) {

        if (position < 0 || position >= mData.size()) {
            return;
        }
        mData.remove(position);
        adapter.notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
        adapter.notifyDataSetChanged();
    }


    public List<T> getData() {
        return mData;
    }

    public T getData(int position) {
        return mData.get(position);
    }


    /**
     * 一般对于Spinner使用的现成的适配器可以选择ArrayAdapter
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public T getItem(int position) {
            return (T) mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // Spinner用于显示的Item
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //KLog.i("getView --> position: " + position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);


                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag(); // 这两种布局的id是相同的，所以可以使用同一个ViewHolder类型
            holder.textView.setText(mData.get(position).toString());
            return convertView;
        }

        // Spinner下拉的所有Item
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            //KLog.i("getDropDownView --> position: " + position);

            if (convertView == null) {
                // View.inflate(Context context, int resource, ViewGroup root)
                // 如果 root == null, 则等价于 inflater.inflate(resource, null, false) 也等价于 inflater.inflate(resource, null). layout_width 和 layout_height 是无效的
                // 如果 root != null， 则等价于 inflater.inflate(resource, root, true)
                // View.inflate() 有缺陷，不如使用 LayoutInflater.from().inflate()
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);

                convertView.setTag(new ViewHolder(convertView));
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();

            holder.textView.setText(mData.get(position).toString());

            if (getSelectedItemPosition() == position) {
                holder.textView.setBackgroundColor(Color.GREEN);
            } else {
                holder.textView.setBackground(null);
            }
            return convertView;
        }
    }

    public class ViewHolder {
        public TextView textView;

        public ViewHolder(View convertView) {
            this.textView = (TextView) convertView.findViewById(android.R.id.text1);
        }
    }


    private OnSelectedListener onSelectedListener;

    public interface OnSelectedListener<T> {
        void onItemSelected(AdapterView<?> parent, View view, int position, long id);
    }

    public void setOnSelectedListener(OnSelectedListener selectedListener) {
        onSelectedListener = selectedListener;
    }


}
