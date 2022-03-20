package info.ata4.minecraft.minema.client.config.value;

import info.ata4.minecraft.minema.client.config.MinemaConfig;
import java.util.function.LongSupplier;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CachedLongValue extends CachedPrimitiveValue<Long> implements LongSupplier {

    private long cachedValue;

    private CachedLongValue(MinemaConfig config, ConfigValue<Long> internal) {
        super(config, internal);
    }

    public static CachedLongValue wrap(MinemaConfig config, ConfigValue<Long> internal) {
        return new CachedLongValue(config, internal);
    }

    public long get() {
        if (!resolved) {
            //If we don't have a cached value or need to resolve it again, get it from the actual ConfigValue
            cachedValue = internal.get();
            resolved = true;
        }
        return cachedValue;
    }

    @Override
    public long getAsLong() {
        return get();
    }

    public void set(long value) {
        internal.set(value);
        cachedValue = value;
    }
}