package com.vmr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vmr.LoginFragments.FragmentLoginCorporate;
import com.vmr.LoginFragments.FragmentLoginFamily;
import com.vmr.LoginFragments.FragmentLoginIndividual;
import com.vmr.LoginFragments.FragmentLoginProfessional;


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
                FragmentLoginIndividual fragmentLoginIndividual = new FragmentLoginIndividual();
                return fragmentLoginIndividual;
            case 1:
                FragmentLoginFamily fragmentLoginFamily = new FragmentLoginFamily();
                return fragmentLoginFamily;
            case 2:
                FragmentLoginProfessional fragmentLoginProfessional = new FragmentLoginProfessional();
                return fragmentLoginProfessional;
            case 3:
                FragmentLoginCorporate fragmentLoginCorporate = new FragmentLoginCorporate();
                return fragmentLoginCorporate;
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
