package dev.array21.dutchycore.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterModule {
	String name();
	String version();
	String author();
	String infoUrl() default "https://github.com/DutchyPlugins/";
}