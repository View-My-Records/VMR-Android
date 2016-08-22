package com.vmr.login.interfaces;

/**
 * Created by abhijit on 8/16/16.
 */

public interface LoginFragmentInterface {

    void onIndividualLoginClick(String email, String password, String domain, boolean remember);

    void onFamilyLoginClick(String email, String password, String name, String domain, boolean remember);

    void onProfessionalLoginClick(String email, String password, String name, String domain, boolean remember);

    void onCorporateLoginClick(String email, String password, String name, String domain, boolean remember);
}
