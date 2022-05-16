package cool.dingstock.lib_base.util;

import java.util.Objects;

public class Triple<F, S, T> {

    public final F first;
    public final S second;
    public final T third;

    /**
     * Constructor for a Three Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     * @param third
     */
    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Convenience method for creating an appropriately typed pair.
     *
     * @param a the first object in the Pair
     * @param b the second object in the pair
     * @param c the third object in the pair
     * @return a Pair that is templatized with the types of a , b and c
     */
    public static <A, B, C> Triple<A, B, C> create(A a, B b, C c) {
        return new Triple<A, B, C>(a, b, c);
    }

    /**
     * Checks the two objects for equality by delegating to their respective
     * {@link Object#equals(Object)} methods.
     *
     * @return true if the underlying objects of the Pair are both considered
     * equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Triple)) {
            return false;
        }
        Triple<?, ?, ?> p = (Triple<?, ?, ?>) o;
        return Objects.equals(p.first, first)
                && Objects.equals(p.second, second)
                && Objects.equals(p.third, third);
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the Pair
     */
    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode())
                ^ (second == null ? 0 : second.hashCode())
                ^ (third == null ? 0 : third.hashCode());
    }

}
