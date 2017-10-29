package com.proevolutionsoftware.driveembetter.authentication;

import com.proevolutionsoftware.driveembetter.entity.SingletonUser;

/**
 * Created by alfredo on 20/08/17.
 */

public interface BaseProvider {
    // TODO: lasciare gli account Google, Twitter e Facebook sempre loggati a meno che l'utente non prema i bottoni appositi
    // alla pressione di logout (nella MainFragmentActivity) solo l'account Firebase faraà logout
    void signIn(String email, String password);
    void signOut();
    SingletonUser getUserInformations();
    boolean isSignIn();
}
