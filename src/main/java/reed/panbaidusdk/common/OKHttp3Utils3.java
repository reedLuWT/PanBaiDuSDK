package reed.panbaidusdk.common;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class OKHttp3Utils3 {

    public static int DEFAULT_TIME_OUT = 10;

    private static final MediaType JSON = MediaType.parse("application/problem+json; charset=utf-8");


    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES))
            .connectTimeout(0, TimeUnit.SECONDS).readTimeout(0, TimeUnit.SECONDS).writeTimeout(0, TimeUnit.SECONDS)
            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890))).build();


    /**
     * 不同timeout的连接池
     */
    public static ConcurrentHashMap<Integer, OkHttpClient> cacheClients = new ConcurrentHashMap();


    public static OkHttpClient getHttpClient(int timeout) {

        if (timeout == 0 || DEFAULT_TIME_OUT == timeout) {
            return OK_HTTP_CLIENT;
        } else {
            OkHttpClient okHttpClient = cacheClients.get(timeout);
            if (okHttpClient == null) {
                return syncCreateClient(timeout);
            }
            return setProxy(okHttpClient);
        }
    }

    private static OkHttpClient setProxy(OkHttpClient okHttpClient) {
        return okHttpClient.newBuilder().proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890))).build();
    }

    private static synchronized OkHttpClient syncCreateClient(int timeout) {
        OkHttpClient okHttpClient;

        okHttpClient = cacheClients.get(timeout);
        if (okHttpClient != null) {
            return okHttpClient;
        }

        okHttpClient = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).readTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).build();
        cacheClients.put(timeout, okHttpClient);
        return setProxy(okHttpClient);
    }



    public static ResponseBody form(String url,Map<String,Object> params) {
        long start = System.currentTimeMillis();
        try {
            log.info(JSONObject.toJSONString(params));
            RequestBody body = null;

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            params.forEach((key, value) -> {
                if(value instanceof File){
                    File file = (File) value;
                    builder.addFormDataPart(key, file.getName(),
                            RequestBody.create(
                                    MediaType.parse(String.valueOf(params.get("contentType"))), file));
                }else{
                    builder.addFormDataPart(key, String.valueOf(value));
                }
            });

            body = builder.build();

            Request request = new Request.Builder().url(url)
                    .post(body).build();
            OkHttpClient httpClient = getHttpClient(0);

            ResponseBody responseBody = httpClient.newCall(request).execute().body();

            return responseBody;
        } catch (Exception e) {
            log.error("网络请求失败！",e);
        } finally {
            log.info("request url {} ,total time {} ms , params {}", url, (System.currentTimeMillis() - start) , JSONObject.toJSONString(params));
        }
        return null;
    }

    public static ResponseBody post(String url,Map<String,Object> params) {
        long start = System.currentTimeMillis();
        try {
            log.info(JSONObject.toJSONString(params));

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType, JSONObject.toJSONString(params));

            // 封装请求对象
            Request request = new Request.Builder().post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .url(url).build();

            ResponseBody responseBody = getHttpClient(0).newCall(request).execute().body();

            return responseBody;
        } catch (Exception e) {
            log.error("网络请求失败！",e);
        } finally {
            log.info("request url {} ,total time {} ms , params {}", url, (System.currentTimeMillis() - start) , JSONObject.toJSONString(params));
        }
        return null;
    }

    public static ResponseBody get(String url, Map<String, Object> requestParam) {
        long start = System.currentTimeMillis();
        try {
            StringBuffer stringBuffer = new StringBuffer(url);
            if(requestParam!=null){
                if(!url.contains("?")){
                    stringBuffer.append("?");
                }else{
                    stringBuffer.append("&");
                }
                requestParam.keySet().forEach((item)->{
                    stringBuffer.append(item).append("=").append(requestParam.get(item)).append("&");
                });
            }

            Request request = new Request.Builder().url(stringBuffer.toString()).get().build();
            ResponseBody responseBody = getHttpClient(0).newCall(request).execute().body();

            return responseBody;
        } catch (Exception e) {
            log.error("网络请求失败！",e);
        } finally {
            log.info("request url {} ,total time {} ms ", url, (System.currentTimeMillis() - start) );
        }
        return null;
    }

}

