package com.gymhub.gymhub.in_memory.custom_data_structure;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

@Getter
@Setter
public class ZSet <T extends Number> {
    TreeMap<T, Set<Long>> treeMap = new TreeMap<>();
    HashMap<Long, T> map = new HashMap<>();

    public ZSet() {}

    boolean newEntity(Long id, T value){
        map.put(id, value);
        if (treeMap.containsKey(value)) {
            treeMap.get(value).add(id);
        }
        else {
            Set<Long> newSet = new HashSet<>();
            newSet.add(id);
            treeMap.put(value, newSet);
        }
        return true;
    }

    boolean incrementOrDecrementValue(Long id, boolean increment){
        T value = map.get(id);
        treeMap.get(value).remove(id);
        T newVal;
        if (increment) {
            newVal = (T) Double.valueOf(value.doubleValue() + 1);
        }
        else {
            newVal = (T) Double.valueOf(value.doubleValue() - 1);
        }
        if (treeMap.containsKey(newVal)) {
            treeMap.get(newVal).add(id);
        }
        else {
            Set<Long> newSet = new HashSet<>();
            newSet.add(id);
            treeMap.put(newVal, newSet);
        }
        map.put(id, newVal);
        return true;


    }
}
