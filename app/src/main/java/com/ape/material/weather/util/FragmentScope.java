package com.ape.material.weather.util;

/**
 * Created by android on 16-11-25.
 */

import com.ape.material.weather.AppComponent;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * In Dagger, an unscoped component cannot depend on a scoped component. As {@link AppComponent} is
 * a scoped component ({@code @Singleton}, we create a custom scope to be used by all fragment
 * components. Additionally, a component with a specific scope cannot have a sub component with the
 * same scope.
 */
@Scope
@Documented
@Retention(value = RUNTIME)
public @interface FragmentScope {
}
