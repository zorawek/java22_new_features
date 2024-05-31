package org.example;

import java.util.List;
import java.util.Optional;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class MyGatherer {
    public static void main(String[] args) {
//        windowSlide();
//        windowFixed();
        scan();
//        fold();
    }

    /**
     * *windowSliding* is a stateful many-to-many gatherer which groups input elements into lists of
     * a supplied size. After the first window, each subsequent window is created from a copy
     * of its predecessor by dropping the first element and appending the next element from the input stream
     */
    public static void windowSlide() {
        List<List<Integer>> windows =
                Stream.of(1, 2, 3, 4, 5, 6, 7, 8).gather(Gatherers.windowSliding(2)).toList();

        System.out.println(windows);
    }

    /**
     * *windowFixed* is a stateful many-to-many gatherer which groups input elements into lists of a supplied size, emitting the windows downstream when they are full.
     */
    public static void windowFixed() {
        List<List<Integer>> windows =
                Stream.of(1, 2, 3, 4, 5, 6, 7, 8).gather(Gatherers.windowFixed(3)).toList();
        System.out.println(windows);
    }

    /**
     * *scan* is a stateful one-to-one gatherer which applies a supplied function to the current state and the
     * current element to produce the next element, which it passes downstream.
     */
    public static void scan() {
        // public static <T,R> GathererPREVIEW<T,?,R> scan(Supplier<R> initial,
        // BiFunction<? super R,? super T,? extends R> scanner)
        List<String> numberStrings =
                Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .gather(
                                Gatherers.scan(() -> "", (string, number) -> string + number)
                        )
                        .toList();
        System.out.println(numberStrings);
    }

    /**
     * *fold* is a stateful many-to-one gatherer which constructs an aggregate incrementally and emits
     * that aggregate when no more input elements exist.
     */
    public static void fold() {
        Optional<String> numberString =
                Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .gather(
                                Gatherers.fold(() -> "", (string, number) -> string + number)
                        )
                        .findAny();
        System.out.println(numberString);
    }
}
