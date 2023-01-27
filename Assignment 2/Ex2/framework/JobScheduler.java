package framework;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class JobScheduler<K, V> {

    /**
     * Entry point of the framework.
     * It is a frozen spot, since the user should not change the order of the
     * phases.
     */
    public final void executePhases() {
        output(collect(compute(emit())));
    }

    /**
     * Executes the jobs received from emit by invoking execute on them.
     * 
     * @return A single stream of key/value pairs obtained by concatenating the
     *         output of the jobs.
     */
    protected abstract Stream<AJob<K, V>> emit();

    public final Stream<Pair<K, V>> compute(Stream<AJob<K, V>> stream) {
        return stream.flatMap(job -> job.execute());
    }

    /**
     * Groups the output of compute by key, keeping a list of all the values.
     * 
     * @param computed Output of emit.
     * @return A stream of pairs grouped as described.
     */
    public final Stream<Pair<K, List<V>>> collect(Stream<Pair<K, V>> computed) {
        return computed
                .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())))
                .entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue()));
    }

    /**
     * Outputs the result of collect.
     * @param out Output of collect.
     */
    protected abstract void output(Stream<Pair<K, List<V>>> out);
}