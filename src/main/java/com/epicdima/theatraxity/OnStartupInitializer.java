package com.epicdima.theatraxity;

import com.epicdima.lib.controller.Controller;
import com.epicdima.lib.di.ObjectInjector;
import com.epicdima.theatraxity.helpers.JsonConverter;
import com.epicdima.theatraxity.services.LifecycleService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author EpicDima
 */
@WebListener
public final class OnStartupInitializer implements ServletContextListener {

    private static void onStart() {
        ObjectInjector.setComponentClass(AppComponent.class);
        Controller.setDefaultConverter(ObjectInjector.createOrReturn(JsonConverter.class));
        ObjectInjector.createOrReturn(LifecycleService.class).onStart();
    }

    private static void onStop() {
        ObjectInjector.createOrReturn(LifecycleService.class).onStop();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        onStart();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        onStop();
    }

    public static void main(String[] args) {
        onStart();
    }
}
