package com.shihc.demo.andrewdemo.mvvm;

import com.shihc.demo.andrewdemo.BR;
import com.shihc.demo.andrewdemo.R;
import com.shihc.demo.andrewdemo.mvvm.listview.ItemModel;

import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.os.AsyncTask;
import android.view.View;

import java.util.Random;

import me.tatarka.bindingcollectionadapter.ItemView;

public class LoginModel {


    public final ObservableList<ItemModel> items = new ObservableArrayList<>();
    public final ItemView itemView = ItemView.of(BR.user, R.layout.data_binding_item);

    public ObservableField<String> numberOfUsersLoggedIn = new ObservableField();
    public ObservableBoolean isExistingUserChecked = new ObservableBoolean();
    private boolean mIsLoaded;
    private LoginActivity mView;
    private Resources mResources;

    public LoginModel(LoginActivity view, Resources resources) {
        mView = view;
        mResources = resources; // You might want to abstract this for testability
        setInitialState();
    }

    public boolean isLoaded() {
        return mIsLoaded;
    }

    private void setInitialState() {
        numberOfUsersLoggedIn.set("...");
        isExistingUserChecked.set(true);
    }


    public void loadAsync() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                // Simulating some asynchronous task fetching data from a remote server
                try {Thread.sleep(2000);} catch (Exception ex) {};
                numberOfUsersLoggedIn.set("" + new Random().nextInt(1000));
                mIsLoaded = true;
                return null;
            }

        }.execute((Void) null);
    }

    public void logInClicked() {
        // Illustrating the need for calling back to the view though testable interfaces.
        if (isExistingUserChecked.get()) {
            mView.showShortToast("Invalid username or password");
        }
        else {
            mView.showShortToast("Please enter a valid email address");
        }
    }
}