package org.oddjob.spring;

import org.junit.Test;
import org.oddjob.spring.config.AppleConfig;
import org.oddjob.spring.config.AppleConfig2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AnnotationConfigTest {

    @Test
    public void testPropertyPlaceholder() {

        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext();

        ctx.register(AppleConfig.class);

        Properties props = new Properties();
        props.setProperty("apple.colour", "red");

        PropertiesPropertySource propertySource
                = new PropertiesPropertySource("Some Props", props);

        ctx.getEnvironment().getPropertySources().addFirst(propertySource);

        ctx.refresh();

        Apple apple = ctx.getBean(Apple.class);

        assertThat(apple.getColour(), is("red"));
    }


    @Test
    public void testPropertyPlaceholder2() {

        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext();

        ctx.register(AppleConfig2.class);

        Properties props = new Properties();
        props.setProperty("today", "monday");

        PropertiesPropertySource propertySource
                = new PropertiesPropertySource("Some Props", props);

        ctx.getEnvironment().getPropertySources().addFirst(propertySource);

        ctx.refresh();

        Apple apple = ctx.getBean(Apple.class);

        assertThat(apple.getColour(), is("green"));
    }
}
