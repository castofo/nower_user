package castofo.com.co.nower.login;

import android.os.Handler;
import android.text.TextUtils;

/**
 * Created by Alejandro on 29/08/2016.
 */
public class LoginInteractorImpl implements LoginInteractor {

  /**
   * Validates if the login information is correct.
   * @param email String The email of the user
   * @param password String The password of the user
   * @param listener OnLoginFinishedListener The Callback for the result
   */
  @Override
  public void login(final String email, final String password,
                    final OnLoginFinishedListener listener) {
    // Mocks the login with a non-empty validation. A handler is created to delay the answer for
    // two seconds.
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        boolean error = false;
        if (TextUtils.isEmpty(email)) {
          listener.onEmailError();
          error = true;
        }
        if (TextUtils.isEmpty(password)) {
          listener.onPasswordError();
          error = true;
        }
        if (!error) listener.onSuccess();
      }
    }, 2000);
  }
}
