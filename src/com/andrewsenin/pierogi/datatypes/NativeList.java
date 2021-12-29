package com.andrewsenin.pierogi.datatypes;

import java.util.*;

public class NativeList implements NativeData {

    private final Deque<NativeData> items;

    public NativeList(Deque<NativeData> items) {
        this.items = items;
    }

    public NativeList cons(NativeData value) {
        // TODO: make this more efficient by homebrewing a linked list, and make sure it even works!
        Deque<NativeData> newItems = items;
        newItems.addFirst(value);
        return new NativeList(newItems);
    }

    @Override
    public boolean equals(NativeData other) {
        if (!(other instanceof NativeList)) {
            return false;
        }
        NativeList otherList = (NativeList) other;
        if (items.size() != otherList.items.size()) {
            return false;
        }
        Iterator<NativeData> iterator = items.iterator(), otherIterator = otherList.items.iterator();
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
