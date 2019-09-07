package eu.dirk.haase.heap;

import eu.dirk.haase.type.Struct;

import java.nio.ByteBuffer;

public class Allocator {

    private final Block block;
    private final ByteBuffer buffer;
    private final int bufferSize;
    private final int startOffset;


    public Allocator(final int bufferSize) {
        this(0, bufferSize);
    }

    public Allocator(final ByteBuffer buffer, final int startOffset, final int bufferSize) {
        this.startOffset = startOffset;
        this.bufferSize = bufferSize;
        this.buffer = buffer;
        this.block = new Block();
        this.buffer.order(this.block.byteOrder());
    }

    public Allocator(final int startOffset, final int bufferSize) {
        this(ByteBuffer.allocateDirect(bufferSize), startOffset, bufferSize);
    }

    public int allocate(final Struct struct) {
        block.initByteBuffer(this.buffer, startOffset);

        short lastNext;
        short currNext = block.next.get();
        while ((currNext > startOffset) && (block.data.get() > startOffset)) {
            lastNext = currNext;
            this.block.setStructAbsolutePosition(lastNext);
            currNext = block.next.get();
        }

        final short dataPosition = (short) this.block.dataPosition();
        final short newNext = (short) this.block.nextHeaderPosition(struct.size());
        if (newNext < bufferSize) {
            block.data.set(dataPosition);
            block.next.set(newNext);
        }

        print();
        struct.initByteBuffer(this.buffer, dataPosition);
        return dataPosition;
    }

    public void free(final int dataPosition) {
        final int headerAbsolutePosition = this.block.headerPosition(dataPosition);
        this.block.setStructAbsolutePosition(headerAbsolutePosition);
        this.block.data.set((short) 0);
        print();
    }

    private void print() {
        System.out.println("Block{data=" + block.data.get() + "; next=" + block.next.get() + "}");
    }
}
