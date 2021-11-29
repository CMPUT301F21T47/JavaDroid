package com.example.habitshare;

/**
 * The interface for the CustomListAdapters of RecyclerViews
 * The programmer can implement this interface be using the setOnItemClickListener method to define
 * the behavior after clicking on an item
 */
public interface OnItemClickListener {
    void onItemClick(int position);
}
