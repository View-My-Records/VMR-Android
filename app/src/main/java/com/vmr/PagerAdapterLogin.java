package com.vmr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vmr.LoginFragments.FragmentCorporate;
import com.vmr.LoginFragments.FragmentFamily;
import com.vmr.LoginFragments.FragmentIndividual;
import com.vmr.LoginFragments.FragmentProfessional;


/*
 * Created by abhijit on 6/10/16.
 */
public class PagerAdapterLogin extends FragmentPagerAdapter {
    int numberOfPages;
    String[] pages = { "Individual", "Family", "Professional", "Corporate" };

    public PagerAdapterLogin(FragmentManager fm) {
        super(fm);
        this.numberOfPages = pages.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentIndividual fragmentIndividual = new FragmentIndividual();
                return fragmentIndividual;
            case 1:
                FragmentFamily fragmentFamily = new FragmentFamily();
                return fragmentFamily;
            case 2:
                FragmentProfessional fragmentProfessional = new FragmentProfessional();
                return fragmentProfessional;
            case 3:
                FragmentCorporate fragmentCorporate = new FragmentCorporate();
                return fragmentCorporate;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfPages;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return pages[0];
            case 1:
                return pages[1];
            case 2:
                return pages[2];
            case 3:
                return pages[3];
            default:
                return null;
        }
    }
}
