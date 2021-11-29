package com.example.habitshare;

/**
 * The interface for the CustomListAdapters of RecyclerViews
 * The programmer can implement this interface be using the setOnItemClickListener method to define
 * the behavior after long pressing on an item
 */
public interface OnItemLongClickListener {
    boolean onItemLongClick(int position);
}
