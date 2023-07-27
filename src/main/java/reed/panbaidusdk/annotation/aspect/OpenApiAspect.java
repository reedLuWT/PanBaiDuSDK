package reed.panbaidusdk.annotation.aspect;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reed.panbaidusdk.annotation.OpenApi;
import reed.panbaidusdk.common.OKHttp3Utils3;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class OpenApiAspect {

    @Pointcut("execution(public * reed.panbaidusdk.api.*.*(..))")
    public void Pointcut(){}

    @Around("Pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        Object result = null;
        Map<String,Object> requestParam = new HashMap<>();
        Class<?>[] interfaces = joinPoint.getTarget().getClass().getInterfaces();
        Method method1 = interfaces[0].getMethod(method.getName(), method.getParameterTypes());
        OpenApi annotation = method1.getAnnotation(OpenApi.class);
        Parameter[] parameters = method.getParameters();
        // 请求参数封装
        for(int i=0;i<parameters.length;i++){
            requestParam.put(parameters[i].getName(),args[i]);
        }
        if(args.length==1 && args[0] instanceof Map){
            requestParam = (Map<String, Object>) args[0];
        }
        if(annotation!=null&& !StringUtils.isEmpty(annotation.value())){
            String path = annotation.value();
            String type = annotation.type();
            ResponseBody responseBody = null;

            if("form".equals(type)){
                responseBody = OKHttp3Utils3.form(path, requestParam);
            }
            if("post".equals(type)){
                responseBody = OKHttp3Utils3.post(path, requestParam);
            }
            if("get".equals(type)){
                responseBody = OKHttp3Utils3.get(path,requestParam);
            }

            result = handleResult(method,responseBody);
        }
        return result;
    }

    private Object handleResult(Method method, ResponseBody responseBody) throws Exception {
        Type genericReturnType = method.getGenericReturnType();
        Class<?> returnType = method.getReturnType();
        if(returnType == InputStream.class){
            return responseBody.byteStream();
        }
        String string = responseBody.string();
        log.info("response body {}",string);
        return JSONObject.parseObject(string, genericReturnType);
    }

}
