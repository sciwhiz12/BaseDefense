package sciwhiz12.basedefense.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

/**
 * General utility methods.
 * 
 * @author SciWhiz12
 */
public final class Util {
    /**
     * A fix for IntelliJ IDEA's "Constant conditions" inspection. Used for
     * {@link ObjectHolder} and {@link CapabilityInject} fields.
     * 
     * @return {@code null}
     */
    @Nonnull
    public static <T> T Null() {
        return null;
    }

    /**
     * Returns an {@link IWorldPosCallable} containing the given parameters, or
     * {@link IWorldPosCallable#DUMMY} if either parameter is {@code null}.
     * 
     * @param world A {@link World}, or {@code null}
     * @param pos   A {@link BlockPos} or {@code null}
     * @return an {@link IWorldPosCallable}, or {@link IWorldPosCallable#DUMMY}
     */
    public static IWorldPosCallable getOrDummy(@Nullable World world, @Nullable BlockPos pos) {
        if (world != null && pos != null) { return IWorldPosCallable.of(world, pos); }
        return IWorldPosCallable.DUMMY;
    }

    /**
     * If both {@link LazyOptional}s are present, applies their values to
     * {@link BiFunction} and returns the resulting value, otherwise returns the
     * given default value.
     * 
     * @param <F>          The enclosing type of the first {@code LazyOptional}
     * @param <S>          The enclosing type of the second {@code LazyOptional}
     * @param <U>          The type of the return value
     * @param first        The first {@code LazyOptional}
     * @param second       The second {@code LazyOptional}
     * @param defaultValue The default return value
     * @param func         The {@code BiFunction} to be applied
     * @return The result of the {@link BiFunction} if both {@link LazyOptional}s
     *         are present, otherwise the default return value
     */
    public static <F, S, U> U mapIfBothPresent(LazyOptional<F> first, LazyOptional<S> second, U defaultValue,
            BiFunction<F, S, U> func) {
        return first.map((fi) -> second.map((se) -> func.apply(fi, se)).orElse(defaultValue)).orElse(defaultValue);
    }

    /**
     * If both {@link LazyOptional}s are present, applies the values to the given
     * {@link BiConsumer}.*
     * 
     * @param <F>      The enclosing type of the first {@code LazyOptional}
     * @param <S>      The enclosing type of the second {@code LazyOptional}
     * @param first    The first {@code LazyOptional}
     * @param second   The second {@code LazyOptional}
     * @param consumer The {@code BiConsumer} of the values
     */
    public static <F, S> void consumeIfPresent(LazyOptional<F> first, LazyOptional<S> second, BiConsumer<F, S> consumer) {
        first.ifPresent((fi) -> second.ifPresent((se) -> consumer.accept(fi, se)));
    }
}
