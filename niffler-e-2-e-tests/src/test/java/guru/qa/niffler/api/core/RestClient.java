package guru.qa.niffler.api.core;

import guru.qa.niffler.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.CookieManager;
import java.net.CookiePolicy;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

@ParametersAreNonnullByDefault
public abstract class RestClient {

    protected static final Config CFG = Config.getInstance();

    private final OkHttpClient okHttpClient;
    private final Retrofit retrofit;

    public RestClient(String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);
    }

    public RestClient(String baseUrl, boolean followRedirect) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);
    }

    public RestClient(String baseUrl, boolean followRedirect, @Nullable Interceptor... interceptors) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY, interceptors);
    }

    public RestClient(String baseUrl, Converter.Factory factory) {
        this(baseUrl, false, factory, HttpLoggingInterceptor.Level.BODY);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, @Nullable Interceptor... interceptors) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirect);

        if (isNotEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }

        builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level));
        builder.addNetworkInterceptor(new AllureOkHttp3()
                .setRequestTemplate("http-request.ftl")
                .setResponseTemplate("http-response.ftl")
        );
        builder.cookieJar(
                new JavaNetCookieJar(
                        new CookieManager(
                                ThreadSafeCookieStore.INSTANCE,
                                CookiePolicy.ACCEPT_ALL
                        )
                )
        );
        this.okHttpClient = builder.build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(factory)
                .build();
    }

    @Nonnull
    public <T> T create(final Class<T> service) {
        return this.retrofit.create(service);
    }

    public static final class EmtyRestClient extends RestClient {
        public EmtyRestClient(String baseUrl) {
            super(baseUrl);
        }

        public EmtyRestClient(String baseUrl, boolean followRedirect) {
            super(baseUrl, followRedirect);
        }

        public EmtyRestClient(String baseUrl, Converter.Factory factory) {
            super(baseUrl, factory);
        }

        public EmtyRestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
            super(baseUrl, followRedirect, factory, level, interceptors);
        }
    }
}
