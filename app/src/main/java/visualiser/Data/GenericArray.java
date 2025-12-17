package visualiser.Data;

import java.util.Iterator;

// memory leak the class
public class GenericArray<T> implements Iterable<T> {
    private class GenIterator implements Iterator<T>{
        int currentIndex = 0;
        public boolean hasNext(){
            return currentIndex<array.length;
        };

        @SuppressWarnings("unchecked")
        public T next(){
            currentIndex+=1;
            return (T) array[currentIndex-1];
        };
    }
    private Object[] array;

    public GenericArray(int size) {
        this.array = new Object[size];
    }

    public void set(int index, T value) {
        array[index] = value;
    }

    public int length() {
        return array.length;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        return (T) array[index]; // Unchecked cast
    }

    public Iterator<T> iterator(){
        return new GenIterator();
    };

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     */
    public int indexOf(Object o) {
        return indexOfRange(o, 0, array.length);
    }

    int indexOfRange(Object o, int start, int end) {
        Object[] es = array;
        if (o == null) {
            for (int i = start; i < end; i++) {
                if (es[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (o.equals(es[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
}
