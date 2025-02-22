package guru.qa.niffler.data.logging;

import io.qameta.allure.attachment.AttachmentData;
import io.qameta.allure.attachment.AttachmentProcessor;
import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;
import io.qameta.allure.attachment.http.HttpRequestAttachment;
import io.qameta.allure.attachment.http.HttpResponseAttachment;
import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class AllureOkHttpInterceptor implements Interceptor {
    private static final String requestTemplatePath = "http-request.ftl";
    private static final String responseTemplatePath = "http-response.ftl";
    private final AttachmentProcessor<AttachmentData> processor = new DefaultAttachmentProcessor();


    @NotNull
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String requestUrl = request.url().toString();

        HttpRequestAttachment.Builder requestAttachmentBuilder = HttpRequestAttachment.Builder
                .create("Request to: " + requestUrl, requestUrl)
                .setMethod(request.method())
                .setHeaders(toMapConverter(request.headers().toMultimap()));

        RequestBody requestBody = request.body();
        if (Objects.nonNull(requestBody)) {
            requestAttachmentBuilder.setBody(readRequestBody(requestBody));
        }

        HttpRequestAttachment requestAttachment = requestAttachmentBuilder.build();
        processor.addAttachment(requestAttachment, new FreemarkerAttachmentRenderer(requestTemplatePath));
        Response response = chain.proceed(request);
        HttpResponseAttachment.Builder responseAttachmentBuilder = HttpResponseAttachment.Builder
                .create("Response from: " + requestUrl)
                .setResponseCode(response.code())
                .setHeaders(toMapConverter(response.headers().toMultimap()));
        Response.Builder responseBuilder = response.newBuilder();
        ResponseBody responseBody = response.body();
        if (Objects.nonNull(responseBody)) {
            byte[] bytes = responseBody.bytes();
            responseAttachmentBuilder.setBody(new String(bytes, StandardCharsets.UTF_8));
            responseBuilder.body(ResponseBody.create(bytes, responseBody.contentType()));
        }

        HttpResponseAttachment responseAttachment = responseAttachmentBuilder.build();
        processor.addAttachment(responseAttachment, new FreemarkerAttachmentRenderer(responseTemplatePath));
        return responseBuilder.build();
    }

    private static Map<String, String> toMapConverter(Map<String, List<String>> items) {
        Map<String, String> result = new HashMap<>();
        items.forEach((key, value) -> result.put(key, String.join("; ", value)));
        return result;
    }

    private static String readRequestBody(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readString(StandardCharsets.UTF_8);
    }
}
