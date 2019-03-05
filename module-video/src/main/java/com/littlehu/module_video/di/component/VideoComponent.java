package com.littlehu.module_video.di.component;

import com.littlehu.common_lib.di.ApplicationComponent;
import com.littlehu.common_lib.scope.ActivityScope;
import com.littlehu.module_video.di.module.ApiModule;
import com.littlehu.module_video.mvp.ui.fragment.VideoFragment;

import dagger.Component;

@ActivityScope
@Component(modules = ApiModule.class , dependencies = ApplicationComponent.class)
public interface VideoComponent {

    void inject(VideoFragment videoFragment);

}
