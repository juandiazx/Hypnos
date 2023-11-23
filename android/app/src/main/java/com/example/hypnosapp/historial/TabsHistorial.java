package com.example.hypnosapp.historial;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.hypnosapp.historial.HistorialFragmentAnual;
import com.example.hypnosapp.historial.HistorialFragmentMes;
import com.example.hypnosapp.historial.HistorialFragmentSemana;

public class TabsHistorial extends FragmentPagerAdapter {
        public TabsHistorial(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HistorialFragmentSemana();
                case 1:
                    return new HistorialFragmentMes();
                case 2:
                    return new HistorialFragmentAnual();
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
                    return "Semanal";
                case 1:
                    return "Mensual";
                case 2:
                    return "Anual";
                default:
                    return null;
            }
        }

    }
