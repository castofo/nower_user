package castofo.com.co.nower.login;

/**
 * Created by Alejandro on 29/08/2016.
 */
public interface LoginView {

  void showProgress();

  void hideProgress();

  void setEmailError();

  void setPasswordError();

  void navigateToMap();
}
