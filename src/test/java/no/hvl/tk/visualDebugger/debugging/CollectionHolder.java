package no.hvl.tk.visualDebugger.debugging;

import java.util.List;

public class CollectionHolder<T> {
    private final List<T> list;

    public CollectionHolder(List<T> list) {
        this.list = list;
    }
}