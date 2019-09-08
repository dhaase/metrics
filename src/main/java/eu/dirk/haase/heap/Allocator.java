package eu.dirk.haase.heap;

import eu.dirk.haase.type.Struct;

import java.nio.ByteBuffer;

public final class Allocator {

    private static final short EMPTY_DATA_SIZE = (short) 0;
    private static final short NO_BLOCK_POSITION = (short) -1;
    private static final short NO_DATA_POSITION = (short) -1;
    private final Block block;
    private final ByteBuffer buffer;
    private final int maximumSize;
    private final int startPosition;


    public Allocator(final int maximumSize) {
        this(0, maximumSize);
    }

    public Allocator(final ByteBuffer buffer, final int startPosition, final int maximumSize) {
        this.startPosition = startPosition;
        this.maximumSize = maximumSize;
        this.buffer = buffer;
        this.block = new Block();
        this.buffer.order(this.block.byteOrder());
    }

    public Allocator(final int startPosition, final int maximumSize) {
        this(ByteBuffer.allocateDirect(startPosition + maximumSize), startPosition, maximumSize);
    }

    public static int calculateNeededSizeOverAll(final int dataSize) {
        return (dataSize + Block.blockSize);
    }

    public static int headerSize() {
        return Block.blockSize;
    }

    public int allocate(final int dataSize) {
        short lastNext = findFreeBlock(dataSize);

        if (lastNext != NO_BLOCK_POSITION) {
            this.block.setStructAbsolutePosition(lastNext);
            return occupyCurrentFreeBlock(dataSize);
        }

        return NO_DATA_POSITION;
    }

    public short findFreeBlock(final int dataSize) {
        block.initByteBuffer(this.buffer, startPosition);

        short lastNext = block.next.get();
        lastNext = (short) (lastNext < startPosition ? startPosition : lastNext);
        if (block.data.get() == EMPTY_DATA_SIZE) {
            // first Block is free
            lastNext = (short) startPosition;
        } else {
            lastNext = findNextFreeBlock(lastNext);
        }

        this.block.setStructAbsolutePosition(lastNext);
        if ((lastNext >= startPosition)
                && (block.data.get() == EMPTY_DATA_SIZE)
                && (this.buffer.limit() >= (this.block.dataPosition() + dataSize))) {
            return lastNext;
        }

        return NO_BLOCK_POSITION;
    }

    private short findNextFreeBlock(short lastNext) {
        short currNext = lastNext;
        while ((currNext > startPosition) && (block.data.get() != EMPTY_DATA_SIZE)) {
            lastNext = currNext;
            this.block.setStructAbsolutePosition(lastNext);
            currNext = block.next.get();
        }
        return lastNext;
    }

    public boolean free(final int dataPosition) {
        final int headerAbsolutePosition = this.block.headerPosition(dataPosition);
        this.block.setStructAbsolutePosition(headerAbsolutePosition);
        if (this.block.data.get() == dataPosition) {
            this.block.data.set(EMPTY_DATA_SIZE);
            return true;
        }
        return false;
    }

    public boolean isAvailable(final int dataSize) {
        return (findFreeBlock(dataSize) != NO_BLOCK_POSITION);
    }

    private int occupyCurrentFreeBlock(final int dataSize) {
        final short dataPosition = (short) this.block.dataPosition();
        final short newNext = (short) this.block.nextHeaderPosition(dataSize);
        if (newNext <= (maximumSize + startPosition)) {
            block.data.set(dataPosition);
            if ((newNext < (maximumSize + startPosition)) && (this.buffer.limit() > (newNext + Block.blockSize))) {
                block.next.set(newNext);
            }
        }
        print();
        return dataPosition;
    }

    public void initByteBuffer(final Struct struct, final int dataPosition) {
        struct.initByteBuffer(this.buffer, dataPosition);
    }

    private void print() {
        System.out.println("Block{data=" + block.data.get() + "; next=" + block.next.get() + "}");
    }
}
