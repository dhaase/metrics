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
        int absolutePosition1 = allocator.allocate(struct);
        int absolutePosition2 = allocator.allocate(struct);
        int absolutePosition3 = allocator.allocate(struct);
        int absolutePosition4 = allocator.allocate(struct);
        // Then
        assertThat(absolutePosition1).isEqualTo(2 + startOffset);
        assertThat(absolutePosition2).isEqualTo(11 + startOffset);
        assertThat(absolutePosition3).isEqualTo(20 + startOffset);
        assertThat(absolutePosition4).isEqualTo(29 + startOffset);
    }

    @Test
    public void test_that() {
        // Given
        Allocator allocator = new Allocator(2048);
        MyStruct struct = new MyStruct();
        // When
        int absolutePosition1 = allocator.allocate(struct);
        int absolutePosition2 = allocator.allocate(struct);
        int absolutePosition3 = allocator.allocate(struct);
        int absolutePosition4 = allocator.allocate(struct);
        // Then
        assertThat(absolutePosition1).isEqualTo(2);
        assertThat(absolutePosition2).isEqualTo(11);
        assertThat(absolutePosition3).isEqualTo(20);
        assertThat(absolutePosition4).isEqualTo(29);
    }


    static class MyStruct extends Struct {
        final Signed8 m_1_signed08 = new Signed8();
        final Float32 m_3_float32 = new Float32();

        MyStruct() {
            super();
        }

    }


}
