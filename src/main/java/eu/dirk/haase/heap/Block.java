package eu.dirk.haase.heap;

import eu.dirk.haase.type.PrintStruct;
import eu.dirk.haase.type.Struct;

final class Block extends Struct {

    final Bool8 isAllocated = new Bool8();
    final Signed32 next = new Signed32();
    final Signed32 size = new Signed32();

    public Block() throws IllegalAccessException {
        PrintStruct.print(0, this);
    }
}
