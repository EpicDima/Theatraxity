package com.epicdima.theatraxity;

import com.epicdima.lib.di.annotations.Component;
import com.epicdima.theatraxity.dal.di.DalModule;
import com.epicdima.theatraxity.di.WebModule;

/**
 * @author EpicDima
 */
@Component(modules = {DalModule.class, WebModule.class})
public interface AppComponent {
}
