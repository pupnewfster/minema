package info.ata4.minecraft.minema.client.config.value;

import info.ata4.minecraft.minema.client.config.MinemaConfig;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CachedConfigValue<T> extends CachedResolvableConfigValue<T, T> {

    protected CachedConfigValue(MinemaConfig config, ConfigValue<T> internal) {
        super(config, internal);
    }

    public static <T> CachedConfigValue<T> wrap(MinemaConfig config, ConfigValue<T> internal) {
        return new CachedConfigValue<>(config, internal);
    }

    @Override
    protected T resolve(T encoded) {
        return encoded;
    }

    @Override
    protected T encode(T value) {
        return value;
    }
}