package lolens.cases.screens.core;

import io.wispforest.owo.ui.core.Easing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class SpinEasing {
    private static final List<Easing> EASINGS = List.of(

            // EASE IN-OUT
            x -> (float) (-(Math.cos(Math.PI * x) - 1) / 2), // IN_OUT_SINE
            x -> x < 0.5 ? 4 * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 3) / 2), // IN_OUT_CUBIC
            x -> x < 0.5 ? 16 * x * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 5) / 2), // IN_OUT_QUINT
            x -> (float) (x < 0.5
                    ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
                    : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2), // IN_OUT_CIRC
            x -> x < 0.5 ? 2 * x * x : (float) (1 - Math.pow(-2 * x + 2, 2) / 2), // IN_OUT_QUAD
            x -> x < 0.5 ? 8 * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 4) / 2), // IN_OUT_QUART

            // EASE OUT
            x -> (float) Math.sin((x * Math.PI) / 2), // OUT_SINE
            x -> 1 - (1 - x) * (1 - x), // OUT_QUAD
            x -> (float) (1 - Math.pow(1 - x, 4)), // OUT_QUART
            x -> {
                final double c1 = 1.4;
                final double c3 = c1 + 1;
                return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2)); // OUT_BACK
            }
    );

    private static final Random RANDOM = new Random();

    private static final List<String> EASING_NAMES = List.of(
            "IN_OUT_SINE",
            "IN_OUT_CUBIC",
            "IN_OUT_QUINT",
            "IN_OUT_CIRC",
            "IN_OUT_QUAD",
            "IN_OUT_QUART",
            "OUT_SINE",
            "OUT_QUAD",
            "OUT_QUART",
            "OUT_BACK" // todo make it work better
    );

    public static int getIndexByName(String name) {
        return EASING_NAMES.indexOf(name.toUpperCase());
    }

    // get easing by id
    public static Easing get(int id) {
        return EASINGS.get(id);
    }

    // get id by easing
    public static int getId(Easing easing) {
        return EASINGS.indexOf(easing);
    }

    public static Easing random() {
        return EASINGS.get(RANDOM.nextInt(EASINGS.size()));
    }

    // removes easings by id from easing list and gets random. If all removed then using IN_OUT_SINE
    public static Easing randomExcluding(int... excludedIds) {
        List<Easing> available = new ArrayList<>(EASINGS);
        Arrays.stream(excludedIds).forEach(id -> available.remove(EASINGS.get(id)));
        return available.isEmpty() ? EASINGS.get(0) : available.get(RANDOM.nextInt(available.size()));
    }
}