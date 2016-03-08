package com.shihc.demo.andrewdemo.mvvm;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import me.tatarka.bindingcollectionadapter.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter.ItemViewArg;

public class MyRecyclerViewAdapter<User> extends BindingRecyclerViewAdapter<User> {
    private String TAG = "MyRecyclerViewAdapter";

    public MyRecyclerViewAdapter(@NonNull ItemViewArg<User> arg) {
      super(arg);
    }

    @Override
    public ViewDataBinding onCreateBinding(LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup viewGroup) {
        ViewDataBinding binding = super.onCreateBinding(inflater, layoutId, viewGroup);
        Log.d(TAG, "created binding: " + binding);
        return binding;
    }

    @Override
    public void onBindBinding(ViewDataBinding binding, int bindingVariable, @LayoutRes int layoutId, int position, User item) {
        super.onBindBinding(binding, bindingVariable, layoutId, position, item);
        Log.d(TAG, "bound binding: " + binding + " at position: " + position);
    }
}