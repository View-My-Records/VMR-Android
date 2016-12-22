package com.vmr.screen.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vmr.screen.login.fragment.FragmentLoginCorporate;
import com.vmr.screen.login.fragment.FragmentLoginFamily;
import com.vmr.screen.login.fragment.FragmentLoginIndividual;
import com.vmr.screen.login.fragment.FragmentLoginProfessional;


/*
 * Created by abhijit on 6/10/16.
 */
public class LoginPagerAdapter extends FragmentPagerAdapter {

    private int numberOfPages;
    private String[] pages = { "Individual", "Family", "Professional", "Corporate" };

    private OnLoginClickListener onLoginClickListener;

    LoginPagerAdapter(FragmentManager fm) {
        super(fm);
        this.numberOfPages = pages.length;
    }

    public void setOnLoginClickListener(OnLoginClickListener onLoginClickListener) {
        this.onLoginClickListener = onLoginClickListener;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentLoginIndividual fragmentLoginIndividual = new FragmentLoginIndividual();
                fragmentLoginIndividual.setCallbackInterface(onLoginClickListener);
                return fragmentLoginIndividual;
            case 1:
                FragmentLoginFamily fragmentLoginFamily = new FragmentLoginFamily();
                fragmentLoginFamily.setCallbackInterface(onLoginClickListener);
                return fragmentLoginFamily;
            case 2:
                FragmentLoginProfessional fragmentLoginProfessional = new FragmentLoginProfessional();
                fragmentLoginProfessional.setCallbackInterface(onLoginClickListener);
                return fragmentLoginProfessional;
            case 3:
                FragmentLoginCorporate fragmentLoginCorporate = new FragmentLoginCorporate();
                fragmentLoginCorporate.setCallbackInterface(onLoginClickListener);
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


    public interface OnLoginClickListener {

        void onIndividualLoginClick(String email, String password, String domain, boolean remember);

        void onFamilyLoginClick(String email, String password, String name, String domain, boolean remember);

        void onProfessionalLoginClick(String email, String password, String name, String domain, boolean remember);

        void onCorporateLoginClick(String email, String password, String name, String domain, boolean remember);

        void onFragmentChanged(String domain);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
