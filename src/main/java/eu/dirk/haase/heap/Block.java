package eu.dirk.haase.heap;

import eu.dirk.haase.type.PrintStruct;
import eu.dirk.haase.type.Struct;
import eu.dirk.haase.type.Union;

final class Block extends Struct {

    final Signed32 size = new Signed32();
    final PositionUnion curr = inner(new PositionUnion());
    final Signed32 next = new Signed32();

    static class PositionUnion extends Union {
        final Signed32 position = new Signed32();
        final Bool32 isAllocated = new Bool32();
    }

    public Block() throws IllegalAccessException {
        PrintStruct.print(0, this);
    }
}
