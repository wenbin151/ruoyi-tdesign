package org.dromara.common.tenant.annotation;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态租户注解
 * 需要注意的是，多个注解切面优先级不能高于租户切面(-1)，否则无法对该注解生效
 * 注解不支持嵌套使用，否则租户会被提前释放
 *
 * @author hexm
 * @date 2023/07/22 11:59
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicTenant {

    ThreadLocal<String> DYNAMIC_TENANT_AOP = new TransmittableThreadLocal<>();

    /**
     * 使用的租户id。注意，这是一个SpringEL表达式
     */
    @Language("SpEL")
    String value();

    /**
     * 当读取不到租户时将抛出异常，设置为false将阻止异常抛出
     */
    boolean required() default true;
}
