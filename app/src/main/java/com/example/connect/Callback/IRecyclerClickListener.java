package com.example.connect.Callback;

import android.view.ContextMenu;
import android.view.View;

public interface IRecyclerClickListener {
    void OnItemClickListener(View view, int pos);
    void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int pos);
}