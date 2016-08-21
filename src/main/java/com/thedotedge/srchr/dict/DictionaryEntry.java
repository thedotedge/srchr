package com.thedotedge.srchr.dict;

/**
 * Number of references
 */
public class DictionaryEntry {

    private String name;
    private Long referenceCount;

    public DictionaryEntry(String name, Long referenceCount) {
        this.name = name;
        this.referenceCount = referenceCount;
    }

    public String getName() {
        return name;
    }

    public Long getReferenceCount() {
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
