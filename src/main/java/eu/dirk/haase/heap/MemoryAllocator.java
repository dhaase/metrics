package eu.dirk.haase.heap;

import java.nio.ByteBuffer;
import java.util.function.Predicate;

public class MemoryAllocator {

    private final ByteBuffer memory;
    private final Block block;

    private final Predicate0 hasNext;
    private final Predicate0 isFree;
    private final Predicate0 isAllocated;
    private final Predicate<Integer> isEnoughSpace;

    private final Position thisHeaderStartAbsolutPos;
    private final Position thisHeaderEndAbsolutPos;
    private final Position thisBlockStartAbsolutPos;
    private final Position thisBlockEndAbsolutPos;
    private final Position reservedMemoryStartAbsolutPos;
    private final Position reservedMemoryEndAbsolutPos;
    private final Position nextBlockStartAbsolutPos;
    private final Size remainingBlockSize;
    private final Position remainingAbsolutSize;


    public MemoryAllocator(final int size) throws IllegalAccessException {
        this.memory = ByteBuffer.allocateDirect(size);
        this.block = new Block();
        this.hasNext = () -> (this.block.next.get(this.memory) > 0);
        this.isFree = () -> !this.block.isAllocated.get(this.memory);
        this.isAllocated = () -> this.block.isAllocated.get(this.memory);
        this.thisHeaderStartAbsolutPos = (p, s) -> p;
        this.thisHeaderEndAbsolutPos = (p, s) -> p + this.block.size() - 1;
        this.thisBlockStartAbsolutPos = thisHeaderStartAbsolutPos;
        this.thisBlockEndAbsolutPos = (p, s) -> p + this.block.size() + s - 1;
        this.reservedMemoryStartAbsolutPos = (p, s) -> this.thisHeaderEndAbsolutPos.calc(p, s) + 1;
        this.reservedMemoryEndAbsolutPos = this.thisBlockEndAbsolutPos;
        this.nextBlockStartAbsolutPos = (p, s) -> this.reservedMemoryEndAbsolutPos.calc(p, s) + 1;
        this.remainingBlockSize = (s) -> this.block.size.get(this.memory) - s;
        this.remainingAbsolutSize = (p, s) -> this.memory.capacity() - this.reservedMemoryEndAbsolutPos.calc(p, s);
        this.isEnoughSpace = (s) -> (this.block.size.get(this.memory) - s) >= 0;
    }

    public ByteBuffer allocate(final int size) {
        int thisBlockStart = findFirstFreeBlockStart(size);
        if (thisBlockStart < 0) {
            thisBlockStart = findFirstUnusedMemory(size);
        }
        if (thisBlockStart >= 0) {
            this.memory.position(thisBlockStart);
            final int thisBlockEnd = this.thisBlockEndAbsolutPos.calc(thisBlockStart, size);
            if (thisBlockEnd <= this.memory.capacity()) {
                final int reservedMemoryStart = this.reservedMemoryStartAbsolutPos.calc(thisBlockStart, size);
                final int reservedMemoryEnd = this.reservedMemoryEndAbsolutPos.calc(thisBlockStart, size);
                final int lastNextBlockStart = this.block.next.get(this.memory);
                final int currNextBlockStart = this.nextBlockStartAbsolutPos.calc(thisBlockStart, size);
                System.out.println("allocate                  size: " + size);
                System.out.println("allocate    this.header.size(): " + this.block.size());
                System.out.println("allocate        thisBlockStart: " + thisBlockStart);
                System.out.println("allocate   reservedMemoryStart: " + reservedMemoryStart);
                System.out.println("allocate     reservedMemoryEnd: " + reservedMemoryEnd);
                System.out.println("allocate          thisBlockEnd: " + thisBlockEnd);
                if (currNextBlockStart == this.memory.capacity()) {
                    this.block.next.set(this.memory, 0);
                } else if (currNextBlockStart < this.memory.capacity()) {
                    System.out.println("allocate lastNextBlockStart: " + lastNextBlockStart);
                    System.out.println("allocate currNextBlockStart: " + currNextBlockStart);
                    final int remainingBlockSize = 0;//this.remainingBlockSize.calc(thisBlockStart, size);
                    System.out.println("allocate remainingBlockSize: " + remainingBlockSize);
                    this.memory.position(currNextBlockStart);
                    if (remainingBlockSize > this.block.size()) {
                        this.block.size.set(this.memory, remainingBlockSize);
                        this.block.isAllocated.set(this.memory, false);
                    } else {
                        this.block.next.set(this.memory, 0);
                    }
                }
                final int lastReservedMemorySize = this.block.size.get(this.memory);
                System.out.println("allocate last reserved size: " + lastReservedMemorySize);
                this.block.size.set(this.memory, size);
                this.block.isAllocated.set(this.memory, true);
                System.out.println("allocate remainingFreeSpace: " + remainingAbsolutSize.calc(thisBlockStart, size));
                System.out.println("allocate ---------------- ");
                return this.memory.duplicate();
            } else {
                return null;
            }
        }
        return null;
    }

    private int findFirstFreeBlockStart(final int size) {
        this.memory.position(0);
        System.out.println(" - - findFirstFreeBlockStart - - " + size);
        while (hasNext.check() && isAllocated.check()) {
            final int nextBlockStart = this.block.next.get(this.memory);
            this.memory.position(nextBlockStart);
        }
        int thisBlockStart = this.memory.position();
        if (hasNext.check() && isFree.check() && this.isEnoughSpace.test(size)) {
            return thisBlockStart;
        }
        return -1;
    }

    private int findFirstUnusedMemory(final int size) {
        System.out.println(" - - findFirstUnusedMemory - - " + size);
        this.memory.position(0);
        if (!hasNext.check()) {
            return 0;
        }
        while (hasNext.check()) {
            final int nextBlockStart = this.block.next.get(this.memory);
            this.memory.position(nextBlockStart);
        }
        final int remainingFreeSpace = this.remainingAbsolutSize.calc(this.memory.position(), size);
        if (remainingFreeSpace >= 0) {
            return this.memory.position();
        }
        return -1;
    }


    public void free(final int position) {
        this.memory.position(position);
        this.block.isAllocated.set(this.memory, false);
        defragement();
    }

    private void defragement() {
        int firstBlockStart = 0;
        boolean isFree = false;
        this.memory.position(0);
        while (hasNext.check()) {
            if (isFree) {
                isFree = this.isFree.check();
                if (isFree) {
                    int nextBlockStart = this.block.next.get(this.memory);
                    int nextMemorySize = this.block.size.get(this.memory);
                    System.out.println("defragement firstBlockStart: " + firstBlockStart);
                    System.out.println("defragement  nextBlockStart: " + nextBlockStart);
                    System.out.println("defragement  nextMemorySize: " + nextMemorySize);
                    this.memory.position(firstBlockStart);
                    int firstMemorySize = this.block.size.get(this.memory);
                    final int freeMemorySize = nextMemorySize + firstMemorySize + this.block.size();
                    System.out.println("defragement firstMemorySize: " + firstMemorySize);
                    System.out.println("defragement  freeMemorySize: " + freeMemorySize);
                    this.block.size.set(this.memory, freeMemorySize);
                    this.block.next.set(this.memory, nextBlockStart);
                    this.block.isAllocated.set(this.memory, false);
                    printBlock(0);
                }
            } else {
                isFree = this.isFree.check();
                if (isFree) {
                    firstBlockStart = this.memory.position();
                }
            }
            this.memory.position(this.block.next.get(this.memory));
        }
    }

    public void printBlocks() {
        this.memory.position(0);
        int count = 0;
        while (hasNext.check()) {
            ++count;
            System.out.println("-----------------");
            printBlock(count);
            System.out.println("-----------------");
            this.memory.position(this.block.next.get(this.memory));
        }
        System.out.println("this.memory.position:  " + this.memory.position());
        System.out.println("this.block.next.get(this.memory): " + this.block.next.get(this.memory));
    }

    private void printBlock(int count) {
        System.out.println(count + ":                 at position: " + this.memory.position());
        System.out.println(count + ": this.block.curr.isAllocated: " + this.block.isAllocated.get(this.memory));
        System.out.println(count + ":             this.block.size: " + this.block.size.get(this.memory));
        System.out.println(count + ":             this.block.next: " + this.block.next.get(this.memory));
    }

    public static void main(String... args) throws IllegalAccessException {
        MemoryAllocator malloc = new MemoryAllocator(50000);
        ByteBuffer buffer1 = alloc(malloc, 5);
        ByteBuffer buffer2 = alloc(malloc, 6);
        ByteBuffer buffer3 = alloc(malloc, 7);
        ByteBuffer buffer4 = alloc(malloc, 8);
        malloc.free(buffer2.position());
        malloc.free(buffer3.position());
        ByteBuffer buffer5 = alloc(malloc, 18);
//        ByteBuffer buffer6 = alloc(malloc, 5);
        malloc.printBlocks();
    }

    private static ByteBuffer alloc(MemoryAllocator malloc, int size) {
        ByteBuffer buffer1 = malloc.allocate(size);
        if (buffer1 == null) {
            System.out.println("buffer == null");
        }
        return buffer1;
    }

    @FunctionalInterface
    interface Predicate0 {
        boolean check();
    }

    @FunctionalInterface
    interface Position {
        int calc(int position, int size);
    }

    @FunctionalInterface
    interface Size {
        int calc(int size);
    }

}
