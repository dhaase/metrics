package eu.dirk.haase.heap;

import java.nio.ByteBuffer;

public class MemoryAllocator {

    private final ByteBuffer memory;
    private final Block block;

    private final Predicate hasNext;
    private final Predicate isFree;
    private final Predicate isAllocated;

    private final Position thisHeaderStart;
    private final Position thisHeaderEnd;
    private final Position thisBlockStart;
    private final Position thisBlockEnd;
    private final Position freeMemoryStart;
    private final Position freeMemoryEnd;
    private final Position nextBlockStart;
    private final Position remainingFreeSpace;


    public MemoryAllocator(final int size) throws IllegalAccessException {
        this.memory = ByteBuffer.allocateDirect(size);
        this.block = new Block();
        this.hasNext = () -> (this.block.next.get(this.memory) > 0);
        this.isFree = () -> !this.block.curr.isAllocated.get(this.memory);
        this.isAllocated = () -> this.block.curr.isAllocated.get(this.memory);
        this.thisHeaderStart = (p, s) -> p;
        this.thisHeaderEnd = (p, s) -> p + this.block.size() - 1;
        this.thisBlockStart = (p, s) -> p;
        this.thisBlockEnd = (p, s) -> p + this.block.size() + s - 1;
        this.freeMemoryStart = (p, s) -> this.thisHeaderEnd.calc(p, s) + 1;
        this.freeMemoryEnd = this.thisBlockEnd;
        this.nextBlockStart = (p, s) -> this.freeMemoryEnd.calc(p, s) + 1;
        this.remainingFreeSpace = (p, s) -> this.memory.capacity() - this.freeMemoryEnd.calc(p, s);
    }

    public ByteBuffer allocate(final int size) {
        int thisBlockStart = findFirstFreeBlockStart(size);
        if (thisBlockStart <= 0) {
            thisBlockStart = findFirstUnusedMemory(size);
        }
        if (thisBlockStart >= 0) {
            this.memory.position(thisBlockStart);
            final int thisBlockEnd = this.thisBlockEnd.calc(thisBlockStart, size);
            if (thisBlockEnd <= this.memory.capacity()) {
                final int freeMemoryStart = this.freeMemoryStart.calc(thisBlockStart, size);
                final int freeMemoryEnd = this.freeMemoryEnd.calc(thisBlockStart, size);
                final int nextBlockStart = this.nextBlockStart.calc(thisBlockStart, size);
                System.out.println("allocate              size: " + size);
                System.out.println("allocate this.block.size(): " + this.block.size());
                System.out.println("allocate    thisBlockStart: " + thisBlockStart);
                System.out.println("allocate   freeMemoryStart: " + freeMemoryStart);
                System.out.println("allocate     freeMemoryEnd: " + freeMemoryEnd);
                System.out.println("allocate      thisBlockEnd: " + thisBlockEnd);
                if (nextBlockStart >= this.memory.capacity()) {
                    this.block.next.set(this.memory, 0);
                } else {
                    System.out.println("allocate    nextBlockStart: " + nextBlockStart);
                    this.block.next.set(this.memory, nextBlockStart);
                }
                this.block.size.set(this.memory, size);
                this.block.curr.position.set(this.memory, freeMemoryStart);
                System.out.println("allocate remainingFreeSpace: " + remainingFreeSpace.calc(thisBlockStart, size));
                System.out.println("allocate ---------------- ");
                return this.memory.duplicate();
            } else {
                return null;
            }
        }
        return null;
    }

    private int findFirstFreeBlockStart(final int size) {
        while (hasNext.check() && isAllocated.check()) {
            final int nextBlockStart = this.block.next.get(this.memory);
            this.memory.position(nextBlockStart);
        }
        int thisBlockStart = this.memory.position();
        final int availableBlockSpace = this.block.size() - size;
        if ((hasNext.check() && isFree.check()) && (availableBlockSpace >= 0)) {
            return thisBlockStart;
        }
        return -1;
    }

    private int findFirstUnusedMemory(final int size) {
        this.memory.position(0);
        if (!hasNext.check()) {
            return 0;
        }
        while (hasNext.check()) {
            final int nextBlockStart = this.block.next.get(this.memory);
            this.memory.position(nextBlockStart);
        }
        final int remainingFreeSpace = this.remainingFreeSpace.calc(this.memory.position(), size);
        if (remainingFreeSpace >= 0) {
            return this.memory.position();
        }
        return -1;
    }


    public void free(final int position) {
        this.memory.position(position);
        this.block.curr.isAllocated.set(this.memory, false);
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
                    System.out.println("defragement nextBlockStart: " + nextBlockStart);
                    System.out.println("defragement nextMemorySize: " + nextMemorySize);
                    this.memory.position(firstBlockStart);
                    int firstMemorySize = this.block.size.get(this.memory);
                    final int freeMemorySize = nextMemorySize + firstMemorySize + this.block.size();
                    System.out.println("defragement firstMemorySize: " + firstMemorySize);
                    System.out.println("defragement freeMemorySize: " + freeMemorySize);
                    this.block.size.set(this.memory, freeMemorySize);
                    this.block.next.set(this.memory, nextBlockStart);
                    this.block.curr.isAllocated.set(this.memory, false);
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
            System.out.println(count + ": this.memory.position: " + this.memory.position());
            System.out.println(count + ": position:     " + this.block.curr.position.get(this.memory));
            System.out.println(count + ": is Allocated: " + this.block.curr.isAllocated.get(this.memory));
            System.out.println(count + ":     size:     " + this.block.size.get(this.memory));
            System.out.println(count + ":     next:     " + this.block.next.get(this.memory));
            System.out.println("-----------------");
            this.memory.position(this.block.next.get(this.memory));
        }
        System.out.println("this.memory.position:  " + this.memory.position());
        System.out.println("this.block.next.get(this.memory): " + this.block.next.get(this.memory));
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
    interface Predicate {
        boolean check();
    }

    @FunctionalInterface
    interface Position {
        int calc(int position, int size);
    }
}
