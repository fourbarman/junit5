package com.fourbarman.junit.extension;

import com.fourbarman.junit.service.UserService;
import lombok.Getter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class PostProcessingExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) throws Exception {
        System.out.println("post processing extension");
        var declaredFields = o.getClass().getDeclaredFields();
        for(Field declaredField : declaredFields) {
            //don't do that, because @Getter is RetentionPolicy.Source and won't be seen at Runtime!
            if (declaredField.isAnnotationPresent(Getter.class)) {
                declaredField.set(o, new UserService(null));
            }
        }
    }
}
