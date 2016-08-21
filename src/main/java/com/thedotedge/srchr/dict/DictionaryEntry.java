package com.thedotedge.srchr.dict;

/**
 * Number of references
 */
public class DictionaryEntry {

    private String name;
    private int referenceCount;

    public DictionaryEntry(String name, int referenceCount) {
        this.name = name;
        this.referenceCount = referenceCount;
    }

    public String getName() {
        return name;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    @Override
    public String toString() {
        return "DictionaryEntry{" +
                "name='" + name + '\'' +
                ", referenceCount=" + referenceCount +
                '}';
    }
}
