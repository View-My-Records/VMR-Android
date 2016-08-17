package com.vmr.login.interfaces;

/**
 * Created by abhijit on 8/16/16.
 */

public interface LoginFragmentInterface {

    void onIndividualLoginClick(String email, String password, String domain);

    void onFamilyLoginClick(String email, String password, String name, String domain);

    void onProfessionalLoginClick(String email, String password, String name, String domain);

    void onCorporateLoginClick(String email, String password, String name, String domain);
}
