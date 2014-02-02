// Copyright (c) 2014, importre. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package io.github.importre.android.chromeadb;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class InfoFragment extends Fragment implements View.OnClickListener {

    private Button mBtnVersion;
    private Button mBtnFeedback;
    private Button mBtnLicense;

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        initButtons(view);
        setVersion();
        return view;
    }

    private void initButtons(View view) {
        mBtnVersion = (Button) view.findViewById(R.id.version);
        mBtnVersion.setOnClickListener(this);
        mBtnFeedback = (Button) view.findViewById(R.id.feedback);
        mBtnFeedback.setOnClickListener(this);
        mBtnLicense = (Button) view.findViewById(R.id.license);
        mBtnLicense.setOnClickListener(this);
    }

    private void setVersion() {
        try {
            FragmentActivity activity = getActivity();
            PackageManager pm = activity.getPackageManager();
            PackageInfo info = pm.getPackageInfo(activity.getPackageName(), PackageManager.GET_ACTIVITIES);
            String ver = String.format(Locale.US, "%s (Build %d)", info.versionName, info.versionCode);
            mBtnVersion.setText(ver);
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.version:
                break;
            case R.id.feedback:
                sendFeedback();
                break;
            case R.id.license:
                showOssLicenses();
                break;
            default:
                // do nothing
                break;
        }
    }

    private void sendFeedback() {
        FragmentActivity activity = getActivity();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(activity.getString(R.string.feedback_type));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{activity.getString(R.string.chromeadb_gmail)});
        intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.feedback_subject));

        try {
            startActivity(Intent.createChooser(intent, "Send feedback"));
        } catch (Exception e) {
            Toast.makeText(activity, activity.getString(R.string.feedback_error_msg), Toast.LENGTH_SHORT).show();
        }
    }

    private void showOssLicenses() {
        FragmentActivity activity = getActivity();
        Intent intent = new Intent(activity, OssLicenseActivity.class);
        activity.startActivity(intent);
    }
}
