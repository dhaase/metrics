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

    public Allocator(final int startOffset, final int bufferSize) {
        this.startOffset = startOffset;
        this.bufferSize = bufferSize;
        this.buffer = ByteBuffer.allocateDirect(bufferSize);
        this.block = new Block();
        this.buffer.order(this.block.byteOrder());
    }

    public int allocate(final Struct struct) {
        block.initByteBuffer(this.buffer, startOffset);

        short lastNext = (short) startOffset;
        short currNext = block.next.get();
        while ((currNext > startOffset) && (block.data.get() > startOffset)) {
            lastNext = currNext;
            this.block.setStructAbsolutePosition(lastNext);
            currNext = block.next.get();
        }

        final short dataPosition = (short) (block.data.length() + lastNext);
        final short newNext = (short) (block.size() + lastNext + struct.size());
        if (newNext < bufferSize) {
            block.data.set(dataPosition);
            block.next.set(newNext);
        }

        print();
        struct.initByteBuffer(this.buffer, dataPosition);
        return dataPosition;
    }

    public void free(final int dataPosition) {
        final short headerAbsolutePosition = (short) (dataPosition - block.data.length());
        this.block.setStructAbsolutePosition(headerAbsolutePosition);
        this.block.data.set((short) 0);
        print();
    }

    private void print() {
        System.out.println("Block{data=" + block.data.get() + "; next=" + block.next.get() + "}");
    }
}
