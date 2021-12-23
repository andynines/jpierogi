package com.andrewsenin.pierogi.datatypes;

import java.util.*;

public class NativeList implements NativeType {

    private final Deque<NativeType> items;

    public NativeList(Deque<NativeType> items) {
        this.items = items;
    }

    public NativeList cons(NativeType value) {
        // TODO: make this more efficient by homebrewing a linked list, and make sure it even works!
        Deque<NativeType> newItems = items;
        newItems.addFirst(value);
        return new NativeList(newItems);
    }

    @Override
    public boolean equals(NativeType other) {
        if (!(other instanceof NativeList)) {
            return false;
        }
        NativeList otherList = (NativeList) other;
        if (items.size() != otherList.items.size()) {
            return false;
        }
        Iterator<NativeType> iterator = items.iterator(), otherIterator = otherList.items.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().equals(otherIterator.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String makePrintRepresentation() {
        List<String> itemStrings = new ArrayList<>();
        items.forEach(item -> itemStrings.add(item.makePrintRepresentation()));
        return "[" + String.join(", ", itemStrings) + "]";
    }
}
