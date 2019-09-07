package eu.dirk.haase.heap;

import eu.dirk.haase.type.Struct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class AllocatorTest {

    @Test
    public void test_that() {
        // Given
        Allocator allocator = new Allocator(2048);
        MyStruct struct = new MyStruct();
        // When
        int dataPosition1 = allocator.allocate(struct);
        int dataPosition2 = allocator.allocate(struct);
        int dataPosition3 = allocator.allocate(struct);
        int dataPosition4 = allocator.allocate(struct);
        // Then
        assertThat(dataPosition1).isEqualTo((Allocator.headerSize() * 1));
        assertThat(dataPosition2).isEqualTo((Allocator.headerSize() * 2) + (struct.size() * 1));
        assertThat(dataPosition3).isEqualTo((Allocator.headerSize() * 3) + (struct.size() * 2));
        assertThat(dataPosition4).isEqualTo((Allocator.headerSize() * 4) + (struct.size() * 3));
    }

    @Test
    public void test_that_limit() {
        // Given
        final int startOffset = 123;
        MyStruct struct = new MyStruct();
        final int countStructs = 10;
        int maximumSize =  Allocator.calculateNeededSizeOverAll(struct.size()) * countStructs;
        Allocator allocator = new Allocator(startOffset, maximumSize);
        //
        for (int i = 0; countStructs > i; ++i) {
            // When
            int dataPosition = allocator.allocate(struct);
            struct.m_1_signed08.set((byte) i);
            struct.m_2_float32.set((float) i);
            // Then
            assertThat(dataPosition).isEqualTo(startOffset + (Allocator.headerSize() * (i + 1)) + (struct.size() * i));
        }
        System.out.println(allocator.free(154));
        System.out.println(allocator.allocate(struct));
    }

    @Test
    public void test_that_offset() {
        // Given
        final int startOffset = 123;
        Allocator allocator = new Allocator(startOffset, 2048);
        MyStruct struct = new MyStruct();
        // When
        int dataPosition1 = allocator.allocate(struct);
        int dataPosition2 = allocator.allocate(struct);
        int dataPosition3 = allocator.allocate(struct);
        int dataPosition4 = allocator.allocate(struct);
        // Then
        assertThat(dataPosition1).isEqualTo(startOffset + (Allocator.headerSize() * 1));
        assertThat(dataPosition2).isEqualTo(startOffset + (Allocator.headerSize() * 2) + (struct.size() * 1));
        assertThat(dataPosition3).isEqualTo(startOffset + (Allocator.headerSize() * 3) + (struct.size() * 2));
        assertThat(dataPosition4).isEqualTo(startOffset + (Allocator.headerSize() * 4) + (struct.size() * 3));
    }

    static class MyStruct extends Struct {
        final Signed8 m_1_signed08 = new Signed8();
        final Float32 m_2_float32 = new Float32();

        MyStruct() {
            super();
        }

    }


}
