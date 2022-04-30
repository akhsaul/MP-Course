package ikhsan.maulana.tugas;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.LinkedHashSet;

public class CheckedSet<E> extends LinkedHashSet<E> {
    /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element <tt>e</tt> to this set if
     * this set contains no element <tt>e2</tt> such that
     * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns <tt>false</tt>.
     *
     * @param e element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     * @throws NullPointerException if e is NULL
     *                              element
     */
    @Override
    public boolean add(E e) {
        return add(e, false);
    }

    public boolean add(E e, boolean ignore) {
        if (e != null) {
            return super.add(e);
        } else {
            if (ignore) {
                return false;
            } else {
                throw new NullPointerException("Null does not permitted");
            }
        }
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        return addAll(c, false);
    }

    public boolean addAll(@NonNull Collection<? extends E> c, boolean ignore) {
        boolean modified = false;
        for (E e : c) {
            if (add(e, ignore)) {
                modified = true;
            }
        }
        return modified;
    }
}
