package com.shihc.demo.andrewdemo.mvvm;


import com.shihc.demo.andrewdemo.R;
import com.shihc.demo.andrewdemo.databinding.LoginViewBinding;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private LoginViewBinding mBinding;
    private LoginModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.login_view);
        mViewModel = new LoginModel(this, getResources());
        mBinding.setData(mViewModel);
        mBinding.setActivity(this);
        attachButtonListener();

        ensureModelDataIsLodaded();

    }

    private void attachButtonListener() {
        mBinding.existingOrNewUser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mViewModel.isExistingUserChecked.set(checkedId == R.id.returningUserRb ? true : false);
            }
        });
    }

    public void onLoginClick(View view){
        mViewModel.logInClicked();
    }

    private void ensureModelDataIsLodaded() {
        if (!mViewModel.isLoaded()) {
            mViewModel.loadAsync();
        }
    }

    public void showShortToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
