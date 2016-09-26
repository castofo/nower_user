package castofo.com.co.nower.login;

/**
 * Created by Alejandro on 29/08/2016.
 */
public interface LoginInteractor {

  interface OnLoginFinishedListener {
    void onEmailError();

    void onPasswordError();

    void onSuccess();
  }

  void login(String email, String password, OnLoginFinishedListener listener);
}
