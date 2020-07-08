package info.ata4.minecraft.minema.client.config;

import java.util.ArrayList;
import java.util.List;
import info.ata4.minecraft.minema.client.config.value.CachedPrimitiveValue;
import info.ata4.minecraft.minema.client.config.value.CachedResolvableConfigValue;

public abstract class BaseMekanismConfig implements IMekanismConfig {

    private final List<CachedResolvableConfigValue<?, ?>> cachedConfigValues = new ArrayList<>();
    private final List<CachedPrimitiveValue<?>> cachedPrimitiveValues = new ArrayList<>();

    @Override
    public void clearCache() {
        cachedConfigValues.forEach(CachedResolvableConfigValue::clearCache);
        cachedPrimitiveValues.forEach(CachedPrimitiveValue::clearCache);
    }

    @Override
    public <T, R> void addCachedValue(CachedResolvableConfigValue<T, R> configValue) {
        cachedConfigValues.add(configValue);
    }

    @Override
    public <T> void addCachedValue(CachedPrimitiveValue<T> configValue) {
        cachedPrimitiveValues.add(configValue);
    }
}