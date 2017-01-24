package castofo.com.co.nower.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Alejandro on 10/01/2017.
 */
public class ServiceFactory {

  public static class Builder {

    private String mBaseUrl;
    private List<Interceptor> mInterceptors;
    // private Authenticator mAuthenticator;
    private int mTimeoutInSeconds;

    public Builder() {
      this.mInterceptors = new ArrayList<>();
      // this.mAuthenticator = Authenticator.NONE;
      this.mTimeoutInSeconds = 0;
    }

    public ServiceFactory.Builder addInterceptor(Interceptor interceptor) {
      mInterceptors.add(interceptor);
      return this;
    }

    public ServiceFactory.Builder addInterceptors(Interceptor... interceptors) {
      mInterceptors.addAll(Arrays.asList(interceptors));
      return this;
    }

    /*
    public ServiceFactory.Builder withAuthenticator(Authenticator authenticator) {
      if (authenticator != null) mAuthenticator = authenticator;
      return this;
    }
    */

    public ServiceFactory.Builder withBaseUrl(String baseUrl) {
      this.mBaseUrl = baseUrl;
      return this;
    }

    public ServiceFactory.Builder withTimeout(int timeInSeconds) {
      this.mTimeoutInSeconds = timeInSeconds;
      return this;
    }

    public <T> T buildService(final Class<T> serviceClass) {
      return createService(serviceClass);
    }

    private OkHttpClient createClient() {
      OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
      for (Interceptor interceptor : mInterceptors) {
        clientBuilder.addInterceptor(interceptor);
      }
      if (mTimeoutInSeconds > 0) {
        clientBuilder.connectTimeout(mTimeoutInSeconds, TimeUnit.SECONDS);
        clientBuilder.readTimeout(mTimeoutInSeconds, TimeUnit.SECONDS);
      }
      // clientBuilder.authenticator(mAuthenticator);
      return clientBuilder.build();
    }

    private <T> T createService(final Class<T> serviceClass) {
      return new Retrofit.Builder()
          .baseUrl(mBaseUrl)
          .addConverterFactory(GsonConverterFactory.create())
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .client(createClient())
          .build()
          .create(serviceClass);
    }
  }
}
