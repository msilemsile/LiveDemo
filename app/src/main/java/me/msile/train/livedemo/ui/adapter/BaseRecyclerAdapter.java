package me.msile.train.livedemo.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Recycler adapter 基类
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<T> mList;  //列表数据
    protected Context mContext;
    protected Resources mResources;
    protected LayoutInflater mInflater;

    public BaseRecyclerAdapter(Context context, List<T> list) {
        mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
        this.mResources = context.getResources();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
        }
    }

    public void reset(List list) {
        mList = list;
    }

    public boolean isDataEmpty() {
        return mList == null || mList.isEmpty();
    }

    public void addAll(List list) {
        if (mList != null && list != null) {
            mList.addAll(list);
        }
    }

    public void add(T object) {
        if (mList != null && object != null) {
            mList.add(object);
        }
    }

    public List<T> getList() {
        return mList;
    }

    public T getItemData(int pos) {
        if (mList == null) {
            return null;
        }
        int size = mList.size();
        if (pos >= 0 && pos < size) {
            return mList.get(pos);
        }
        return null;
    }

    public interface RecyclerViewListener {
        void onItemClick(View v, int position);
    }

    public RecyclerViewListener mListener;

    public void setListener(RecyclerViewListener mListener) {
        this.mListener = mListener;
    }
}
