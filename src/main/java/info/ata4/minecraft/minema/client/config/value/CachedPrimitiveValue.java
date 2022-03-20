package info.ata4.minecraft.minema.client.config.value;

import info.ata4.minecraft.minema.client.config.MinemaConfig;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CachedPrimitiveValue<T> {

    protected final ConfigValue<T> internal;
    protected boolean resolved;

    protected CachedPrimitiveValue(MinemaConfig config, ConfigValue<T> internal) {
        this.internal = internal;
        config.addCachedValue(this);
    }

    public void clearCache() {
        resolved = false;
    }
}