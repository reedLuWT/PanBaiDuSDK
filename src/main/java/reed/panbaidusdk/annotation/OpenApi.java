package reed.panbaidusdk.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})//作用在参数和方法上
@Retention(RetentionPolicy.RUNTIME)//运行时注解
@Documented//表明这个注解应该被 javadoc工具记录
@Inherited// 注解继承
public @interface OpenApi {
    /**
     * 接口路径
     * @return
     */
    String value() default "";

    /**
     * 接口类别
     * @return
     */
    String type() default "get";

}
