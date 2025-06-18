package io.lonmstalker.tgkit.observability;

public interface Tag extends Comparable<Tag> {
    String key();

    String value();

    static Tag of(String key, String value) {
        return new ImmutableTag(key, value);
    }

    default int compareTo(Tag o) {
        return this.key().compareTo(o.key());
    }
}
