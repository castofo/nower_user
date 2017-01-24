package castofo.com.co.nower.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by alejandrosanchezaristizabal on 1/16/17.
 */
public class ConnectivityInterceptor implements Interceptor {

  public static final String NO_INTERNET_CONNECTION_ERROR = "NO_INTERNET_CONNECTION";

  private Context mContext;

  public ConnectivityInterceptor(Context context) {
    this.mContext = context;
  }

  /**
   * Intercepts the outgoing request to check the Internet connection.
   *
   * @param chain Chain Manager for requests going out and their corresponding responses
   * @return Response Request's response
   * @throws IOException No Internet connection was found
   */
  @Override
  public Response intercept(Chain chain) throws IOException {
    if (!isInternetAvailable()) throw new IOException(NO_INTERNET_CONNECTION_ERROR);

    return chain.proceed(chain.request());
  }

  private boolean isInternetAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) mContext
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  public static boolean isInternetConnectionError(Throwable throwable) {
    return throwable != null && throwable instanceof IOException
        && NO_INTERNET_CONNECTION_ERROR.equals(throwable.getMessage());
  }
}
