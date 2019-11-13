package eu.globaldevelopers.globalsms;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    private static final String ROOT_URL = "https://sandbox.globaltank.app/api/campillo/v1/";


    public RetroClient() {

    }

    private static Retrofit getRetroClient(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getHeader("Bearer zjeqytn8bQbpOo9xdzjsrHUYe4amAlReIpWoGA0nqSQADQNLLA"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static FileApi getApiService(String baseUrl) {
        return getRetroClient(baseUrl).create(FileApi.class);
    }

    static public OkHttpClient getHeader(final String authorizationValue ) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Interceptor.Chain chain) throws IOException {
                                Request request = null;
                                if (authorizationValue != null) {
                                    Log.d("--Authorization-- ", authorizationValue);

                                    Request original = chain.request();
                                    // Request customization: add request headers
                                    Request.Builder requestBuilder = original.newBuilder()
                                            .addHeader("Authorization", authorizationValue);

                                    request = requestBuilder.build();
                                }
                                return chain.proceed(request);
                            }
                        })
                .build();
        return okClient;

    }
}