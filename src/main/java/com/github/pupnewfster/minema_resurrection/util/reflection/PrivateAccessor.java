package com.github.pupnewfster.minema_resurrection.util.reflection;

import java.lang.reflect.Field;
import java.util.Optional;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper.UnableToFindFieldException;

public final class PrivateAccessor {

    // These classes might not be able to be loaded by the JVM at this point
    // (Mod classes of which the corresponding mod is not yet loaded)
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static Optional<Field> optifineShadersFrameTimeCounter;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static Optional<Field> oculusShadersFrameTimeCounter;

    @SuppressWarnings("OptionalAssignedToNull")
    private static void lateLoadFrameTimeCounterField() {
        if (optifineShadersFrameTimeCounter == null) {
            optifineShadersFrameTimeCounter = Optional.ofNullable(getAccessibleField("net.optifine.shaders.Shaders", "frameTimeCounter"));
        }
        if (oculusShadersFrameTimeCounter == null) {
            if (ModList.get().isLoaded("oculus")) {
                oculusShadersFrameTimeCounter = Optional.ofNullable(getAccessibleField(SystemTimeUniforms.Timer.class, "frameTimeCounter"));
            } else {
                oculusShadersFrameTimeCounter = Optional.empty();
            }
        }
    }

    public static float getFrameTimeCounter() {
        lateLoadFrameTimeCounterField();

        if (optifineShadersFrameTimeCounter.isPresent()) {
            try {
                // this field is static, just using null as the object
                return optifineShadersFrameTimeCounter.get().getFloat(null);
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        } else if (oculusShadersFrameTimeCounter.isPresent()) {
            return SystemTimeUniforms.TIMER.getFrameTimeCounter();
        }

        // just a default
        return 0;
    }

    public static void setFrameTimeCounter(float frameTimerCounter) {
        lateLoadFrameTimeCounterField();
        if (optifineShadersFrameTimeCounter.isPresent()) {
            try {
                // this field is static, just using null as the object
                optifineShadersFrameTimeCounter.get().setFloat(null, frameTimerCounter);
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        } else if (oculusShadersFrameTimeCounter.isPresent()) {
            try {
                oculusShadersFrameTimeCounter.get().setFloat(SystemTimeUniforms.TIMER, frameTimerCounter);
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }
    }

    private static Field getAccessibleField(String clazz, String name) {
        try {
            return getAccessibleField(Class.forName(clazz), name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Field getAccessibleField(Class<?> clazz, String name) {
        try {
            return ObfuscationReflectionHelper.findField(clazz, name);
        } catch (UnableToFindFieldException e) {
            return null;
        }
    }
}
