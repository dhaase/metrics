package eu.dirk.haase.heap;

import eu.dirk.haase.type.Struct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class AllocatorTest {

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
        assertThat(dataPosition1).isEqualTo(4 + startOffset);
        assertThat(dataPosition2).isEqualTo(13 + startOffset);
        assertThat(dataPosition3).isEqualTo(22 + startOffset);
        assertThat(dataPosition4).isEqualTo(31 + startOffset);
    }

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
        assertThat(dataPosition1).isEqualTo(4);
        assertThat(dataPosition2).isEqualTo(13);
        assertThat(dataPosition3).isEqualTo(22);
        assertThat(dataPosition4).isEqualTo(31);
    }


    @Test
    public void test_that_limit() {
        // Given
        MyStruct struct = new MyStruct();
        final int countStructs = 10;
        int maximumSize = Allocator.calculateNeededSizeOverAll(struct.size()) * countStructs;
        System.out.println(Allocator.calculateNeededSizeOverAll(struct.size()));
        Allocator allocator = new Allocator(maximumSize);
        //
        for(int i=0; countStructs > i; ++i) {
            // When
            int dataPosition = allocator.allocate(struct);
            struct.m_1_signed08.set((byte)i);
            struct.m_2_float32.set((float) i);
            // Then
            System.out.println(dataPosition);
        }
        int dataPosition = allocator.allocate(struct);
        System.out.println(dataPosition);
    }

    static class MyStruct extends Struct {
        final Signed8 m_1_signed08 = new Signed8();
        final Float32 m_2_float32 = new Float32();

        MyStruct() {
            super();
        }

    }


}
