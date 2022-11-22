package com.lch.reggie.utils;

// This file is auto-generated, don't edit it. Thanks.

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author lch
 * @create 2022/11/9
 * Description:发送的短信验证码工具类
 */

public class SMSUtils {
    public static void sendSms(String param) {

        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId("<your-accessKeyId>")
                .accessKeySecret("<your-accessKeySecret>")
                //.securityToken("<your-token>") // use STS token
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou") // Region ID
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();
        // Parameter settings for API request
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName("阿里云短信测试")
                .templateCode("SMS_154950909")
                .phoneNumbers("18822793553")
                .templateParam("{\"code\":\"" + param + "\"}")
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();
        // Asynchronously get the return value of the API request
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        // Synchronously get the return value of the API request
        SendSmsResponse resp = null;
        try {
            // Asynchronous processing of return values
            /*response.thenAccept(resp -> {
                System.out.println(new Gson().toJson(resp));
            }).exceptionally(throwable -> { // Handling exceptions
                System.out.println(throwable.getMessage());
                return null;
            });*/
            resp = response.get();
            System.out.println(new Gson().toJson(resp));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // Finally, close the client
            client.close();
        }
    }
}
