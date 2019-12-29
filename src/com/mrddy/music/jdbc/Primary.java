package com.mrddy.music.jdbc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//当前注解使用的第时间点是在运行的时候
@Retention(RetentionPolicy.RUNTIME)
//我写的这个注解只能在类上面声明
@Target(ElementType.FIELD)
public @interface Primary {

}
