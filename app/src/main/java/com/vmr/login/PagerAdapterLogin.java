package com.vmr.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vmr.login.fragment.FragmentLoginCorporate;
import com.vmr.login.fragment.FragmentLoginFamily;
import com.vmr.login.fragment.FragmentLoginIndividual;
import com.vmr.login.fragment.FragmentLoginProfessional;
import com.vmr.login.interfaces.LoginFragmentInterface;


/*
 * Created by abhijit on 6/10/16.
 */
class PagerAdapterLogin extends FragmentPagerAdapter {

    private int numberOfPages;
    private String[] pages = { "Individual", "Family", "Professional", "Corporate" };

    private LoginFragmentInterface loginFragmentInterface ;

    PagerAdapterLogin(FragmentManager fm, LoginFragmentInterface loginFragmentInterface) {
        super(fm);
        this.numberOfPages = pages.length;
        this.loginFragmentInterface = loginFragmentInterface;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentLoginIndividual fragmentLoginIndividual = new FragmentLoginIndividual();
                fragmentLoginIndividual.setCallbackInterface(loginFragmentInterface);
                return fragmentLoginIndividual;
            case 1:
                FragmentLoginFamily fragmentLoginFamily = new FragmentLoginFamily();
                fragmentLoginFamily.setCallbackInterface(loginFragmentInterface);
                return fragmentLoginFamily;
            case 2:
                FragmentLoginProfessional fragmentLoginProfessional = new FragmentLoginProfessional();
                fragmentLoginProfessional.setCallbackInterface(loginFragmentInterface);
                return fragmentLoginProfessional;
            case 3:
                FragmentLoginCorporate fragmentLoginCorporate = new FragmentLoginCorporate();
                fragmentLoginCorporate.setCallbackInterface(loginFragmentInterface);
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
