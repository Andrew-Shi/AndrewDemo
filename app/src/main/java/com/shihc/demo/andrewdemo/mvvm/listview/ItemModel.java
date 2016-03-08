package com.shihc.demo.andrewdemo.mvvm.listview;


import android.databinding.Observable;
import android.databinding.ObservableField;

/**
 * Created by shihc on 16/3/8.
 */
public class ItemModel {
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> sex = new ObservableField<>();

    public ItemModel(String name, String sex) {
        this.name.set(name);
        this.sex.set(sex);
    }
}
