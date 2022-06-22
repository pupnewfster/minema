package com.github.pupnewfster.minema_resurrection.config.value;

import com.github.pupnewfster.minema_resurrection.config.MinemaConfig;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CachedStringValue extends CachedResolvableConfigValue<String, String> {

    private CachedStringValue(MinemaConfig config, ConfigValue<String> internal) {
        super(config, internal);
    }

    public static CachedStringValue wrap(MinemaConfig config, ConfigValue<String> internal) {
        return new CachedStringValue(config, internal);
    }

    @Override
    protected String resolve(String encoded) {
        return encoded;
    }

    @Override
    protected String encode(String value) {
        return value;
    }
}