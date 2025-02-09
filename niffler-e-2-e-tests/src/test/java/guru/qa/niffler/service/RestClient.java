package guru.qa.niffler.service;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang.ArrayUtils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.CookieManager;
import java.net.CookiePolicy;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public abstract class RestClient {

    protected static final Config CFG = Config.getInstance();
    protected final Retrofit retrofit;

    public RestClient(String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), BODY);
    }

    public RestClient(String baseUrl, Converter.Factory factory) {
        this(baseUrl, false, factory, BODY);
    }

    public RestClient(String baseUrl, boolean followRedirect) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), BODY);
    }

    public RestClient(String baseUrl,
                      boolean followRedirect,
                      Converter.Factory factory,
                      HttpLoggingInterceptor.Level level,
                      Interceptor... interceptors) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirect);

        if (ArrayUtils.isNotEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        builder.addInterceptor(new HttpLoggingInterceptor().setLevel(level));

        builder.cookieJar(
                new JavaNetCookieJar(
                        new CookieManager(
                                ThreadSafeCookieStore.INSTANCE,
                                CookiePolicy.ACCEPT_ALL
                        )
                )
        );
        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(factory)
                .build();
    }

    public Retrofit retrofit() {
        return this.retrofit;
    }

    public static final class EmptyRestClient extends RestClient {

        public EmptyRestClient(String baseUrl) {
            super(baseUrl);
        }

        public EmptyRestClient(String baseUrl, Converter.Factory factory) {
            super(baseUrl, factory);
        }

        public EmptyRestClient(String baseUrl, boolean followRedirect) {
            super(baseUrl, followRedirect);
        }

        public EmptyRestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
            super(baseUrl, followRedirect, factory, level, interceptors);
        }
    }
}
