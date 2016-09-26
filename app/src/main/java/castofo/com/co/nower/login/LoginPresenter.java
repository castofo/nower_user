package castofo.com.co.nower.login;

/**
 * Created by Alejandro on 29/08/2016.
 */
public interface LoginPresenter {

  void validateCredentials(String email, String password);

  void onDestroy();
}
