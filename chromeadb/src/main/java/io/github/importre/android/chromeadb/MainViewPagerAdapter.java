// Copyright (c) 2014, importre. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package io.github.importre.android.chromeadb;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final int COUNTS = 2;

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return HelpFragment.newInstance();
            case 1:
                return InfoFragment.newInstance();
            default:
                throw new RuntimeException("unexpected position");
        }
    }

    @Override
    public int getCount() {
        return MainViewPagerAdapter.COUNTS;
    }
}
