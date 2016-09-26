package castofo.com.co.nower.login;

/**
 * Created by Alejandro on 29/08/2016.
 */
public class LoginPresenterImpl implements LoginPresenter, LoginInteractor.OnLoginFinishedListener {

  private LoginView mLoginView;
  private LoginInteractor mLoginInteractor;

  public LoginPresenterImpl(LoginView loginView) {
    this.mLoginView = loginView;
    this.mLoginInteractor = new LoginInteractorImpl();
  }

  @Override
  public void validateCredentials(String email, String password) {
    if (mLoginView != null) mLoginView.showProgress();

    mLoginInteractor.login(email, password, this);
  }

  @Override
  public void onEmailError() {
    if (mLoginView != null) {
      mLoginView.setEmailError();
      mLoginView.hideProgress();
    }
  }

  @Override
  public void onPasswordError() {
    if (mLoginView != null) {
      mLoginView.setPasswordError();
      mLoginView.hideProgress();
    }
  }

  /**
   * The login information was correct and the user can proceed.
   */
  @Override
  public void onSuccess() {
    if (mLoginView != null) mLoginView.navigateToMap();
  }

  @Override
  public void onDestroy() {
    mLoginView = null;
  }
}
