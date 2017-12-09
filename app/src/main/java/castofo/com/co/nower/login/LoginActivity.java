package castofo.com.co.nower.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import castofo.com.co.nower.R;
import castofo.com.co.nower.location.MapActivity;

public class LoginActivity extends AppCompatActivity implements LoginView {

  @BindView(R.id.et_email)
  AppCompatEditText emailEditText;
  @BindView(R.id.et_password)
  AppCompatEditText passwordEditText;
  @BindView(R.id.pb_progress)
  ProgressBar progressBar;

  private LoginPresenter mLoginPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    mLoginPresenter = new LoginPresenterImpl(this);
  }

  @Override
  public void showProgress() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideProgress() {
    progressBar.setVisibility(View.GONE);
  }

  @Override
  public void setEmailError() {
    emailEditText.setError(getString(R.string.error_incorrect_email));
  }

  @Override
  public void setPasswordError() {
    passwordEditText.setError(getString(R.string.error_incorrect_password));
  }

  @Override
  public void navigateToMap() {
    Intent navigateToMapIntent = new Intent(this, MapActivity.class);
    startActivity(navigateToMapIntent);
    finish();
  }

  /**
   * Validates the login information given by the user.
   */
  @OnClick(R.id.btn_login)
  public void onLoginClick() {
    mLoginPresenter.validateCredentials(emailEditText.getText().toString(),
        passwordEditText.getText().toString());
  }

  @Override
  protected void onDestroy() {
    mLoginPresenter.onDestroy();
    super.onDestroy();
  }
}
