package com.littlehu.common_lib.config;

import android.app.Application;
import android.support.annotation.Nullable;

import com.littlehu.common_lib.ILoader;

public class ModuleLifecyleConfig {

    private static class SingletonHolder{
        public static ModuleLifecyleConfig instance = new ModuleLifecyleConfig();
    }

    public static ModuleLifecyleConfig getInstance(){
        return SingletonHolder.instance;
    }

    private ModuleLifecyleConfig(){

    }

    public void initModule(@Nullable Application application){
        for(String moduleInitPath : ModuleLifecylePath.initModuleNames){
            try {
                Class<?> clazz = Class.forName(moduleInitPath);
                ILoader init = (ILoader) clazz.newInstance();
                //调用初始化方法
                init.loader(application);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
