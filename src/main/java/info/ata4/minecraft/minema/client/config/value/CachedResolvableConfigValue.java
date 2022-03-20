package info.ata4.minecraft.minema.client.config.value;

import info.ata4.minecraft.minema.client.config.MinemaConfig;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

/**
 * @param <TYPE> The type this {@link CachedResolvableConfigValue} resolves to
 * @param <REAL> The real type that the {@link ConfigValue} holds
 */
public abstract class CachedResolvableConfigValue<TYPE, REAL> {

    private final ConfigValue<REAL> internal;
    @Nullable
    private TYPE cachedValue;

    protected CachedResolvableConfigValue(MinemaConfig config, ConfigValue<REAL> internal) {
        this.internal = internal;
        config.addCachedValue(this);
    }

    protected abstract TYPE resolve(REAL encoded);

    protected abstract REAL encode(TYPE value);

    @Nonnull
    public TYPE get() {
        if (cachedValue == null) {
            //If we don't have a cached value, resolve it from the actual ConfigValue
            cachedValue = resolve(internal.get());
        }
        return cachedValue;
    }

    public void set(TYPE value) {
        internal.set(encode(value));
        cachedValue = value;
    }

    public void clearCache() {
        cachedValue = null;
    }
}