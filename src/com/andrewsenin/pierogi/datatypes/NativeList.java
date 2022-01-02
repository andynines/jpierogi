package com.andrewsenin.pierogi.datatypes;

import java.util.*;

public class NativeList implements NativeData {

    private final Deque<NativeData> items;

    public NativeList(Deque<NativeData> items) {
        this.items = items;
    }

    public Deque<NativeData> getItems() {
        return items;
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
    public String makeValueRepresentation() {
        List<String> itemStrings = new ArrayList<>();
        items.forEach(item -> itemStrings.add(item.makeValueRepresentation()));
        return "[" + String.join(", ", itemStrings) + "]";
    }

    @Override
    public String makePrintRepresentation() {
        return makeValueRepresentation();
    }
}
