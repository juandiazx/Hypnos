package com.example.hypnosapp;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsPaginaPrincipal extends FragmentPagerAdapter {
    public TabsPaginaPrincipal(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DiaFragment1();
            case 1:
                return new DiaFragment2();
            case 2:
                return new DiaFragment3();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3; // Número de pestañas
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Antes de ayer";
            case 1:
                return "Ayer";
            case 2:
                return "Hoy";
            default:
                return null;
        }
    }

}


