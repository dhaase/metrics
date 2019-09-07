package eu.dirk.haase.heap;

import eu.dirk.haase.type.Struct;

final class Block extends Struct {

    final Signed16 data = new Signed16();
    final Signed16 next = new Signed16();

    public Block() {
    }

    public int headerPosition(final int dataPosition) {
        return dataPosition - data.length();
    }

    public int dataPosition() {
        return data.length() + absolutePosition();
    }

    public int nextHeaderPosition(final int dataSize) {
        return size() + absolutePosition() + dataSize;
    }

    public int dataSize() {
        return (data.get() == 0 ? 0 : (next.get() - data.get() - next.length()));
    }
}
