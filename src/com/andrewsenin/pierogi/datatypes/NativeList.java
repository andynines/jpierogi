package com.andrewsenin.pierogi.datatypes;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

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
    public String makePrintRepresentation() {
        List<String> itemStrings = new ArrayList<>();
        items.forEach(item -> itemStrings.add(item.makePrintRepresentation()));
        return "[" + String.join(", ", itemStrings) + "]";
    }
}
