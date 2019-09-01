package eu.dirk.haase.type.test;

import eu.dirk.haase.type.Struct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class StructArrayTest {

    @Test
    public void test_struct_that_1_dimension_array_member_positions_are_correct() {
        // Given
        MyArrayStruct arrayStruct = new MyArrayStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_struct_that_1_dimension_array_member_positions_are_correct(arrayStruct, byteBuffer, 0);
    }

    private void test_struct_that_1_dimension_array_member_positions_are_correct(MyArrayStruct arrayStruct, ByteBuffer byteBuffer, int offset) {
        // When
        arrayStruct.initByteBuffer(byteBuffer, offset);
        // Then Assert Offsets
        assertThat(arrayStruct.m_1_signed08.offset()).isEqualTo(0);

        assertThat(arrayStruct.m_2_signed64[0].offset()).isEqualTo(1 + (0 * 8));
        assertThat(arrayStruct.m_2_signed64[1].offset()).isEqualTo(1 + (1 * 8));
        assertThat(arrayStruct.m_2_signed64[2].offset()).isEqualTo(1 + (2 * 8));
        assertThat(arrayStruct.m_2_signed64[3].offset()).isEqualTo(1 + (3 * 8));
        assertThat(arrayStruct.m_2_signed64[4].offset()).isEqualTo(1 + (4 * 8));
        assertThat(arrayStruct.m_2_signed64[5].offset()).isEqualTo(1 + (5 * 8));
        assertThat(arrayStruct.m_2_signed64[6].offset()).isEqualTo(1 + (6 * 8));
        assertThat(arrayStruct.m_2_signed64[7].offset()).isEqualTo(1 + (7 * 8));
        assertThat(arrayStruct.m_2_signed64[8].offset()).isEqualTo(1 + (8 * 8));
        assertThat(arrayStruct.m_2_signed64[9].offset()).isEqualTo(1 + (9 * 8));
        assertThat(arrayStruct.m_2_signed64[10].offset()).isEqualTo(1 + (10 * 8));
        assertThat(arrayStruct.m_2_signed64[11].offset()).isEqualTo(1 + (11 * 8));

        assertThat(arrayStruct.m_3_float32.offset()).isEqualTo(1 + (12 * 8));

        // Then Assert Absolut Position
        assertThat(arrayStruct.m_1_signed08.absolutePosition()).isEqualTo(offset);

        assertThat(arrayStruct.m_2_signed64[0].absolutePosition()).isEqualTo((1 + (0 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1].absolutePosition()).isEqualTo((1 + (1 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2].absolutePosition()).isEqualTo((1 + (2 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[3].absolutePosition()).isEqualTo((1 + (3 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[4].absolutePosition()).isEqualTo((1 + (4 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[5].absolutePosition()).isEqualTo((1 + (5 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[6].absolutePosition()).isEqualTo((1 + (6 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[7].absolutePosition()).isEqualTo((1 + (7 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[8].absolutePosition()).isEqualTo((1 + (8 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[9].absolutePosition()).isEqualTo((1 + (9 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[10].absolutePosition()).isEqualTo((1 + (10 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[11].absolutePosition()).isEqualTo((1 + (11 * 8)) + offset);

        assertThat(arrayStruct.m_3_float32.absolutePosition()).isEqualTo((1 + (12 * 8)) + offset);
    }

    @Test
    public void test_struct_that_1_dimension_array_member_positions_are_correct_with_offset() {
        // Given
        MyArrayStruct arrayStruct = new MyArrayStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_struct_that_1_dimension_array_member_positions_are_correct(arrayStruct, byteBuffer, 123);
    }

    private void test_struct_that_2_dimension_array_member_positions_are_correct(My2DimArrayStruct arrayStruct, ByteBuffer byteBuffer, int offset) {
        // When
        arrayStruct.initByteBuffer(byteBuffer, offset);
        // Then Assert Offsets
        assertThat(arrayStruct.m_1_signed08.offset()).isEqualTo(0);

        assertThat(arrayStruct.m_2_signed64[0][0].offset()).isEqualTo(1 + (0));
        assertThat(arrayStruct.m_2_signed64[0][1].offset()).isEqualTo(1 + (1 * 1 * 8));
        assertThat(arrayStruct.m_2_signed64[0][2].offset()).isEqualTo(1 + (1 * 2 * 8));
        assertThat(arrayStruct.m_2_signed64[0][3].offset()).isEqualTo(1 + (1 * 3 * 8));
        assertThat(arrayStruct.m_2_signed64[0][4].offset()).isEqualTo(1 + (1 * 4 * 8));
        assertThat(arrayStruct.m_2_signed64[0][5].offset()).isEqualTo(1 + (1 * 5 * 8));
        assertThat(arrayStruct.m_2_signed64[0][6].offset()).isEqualTo(1 + (1 * 6 * 8));
        assertThat(arrayStruct.m_2_signed64[0][7].offset()).isEqualTo(1 + (1 * 7 * 8));
        assertThat(arrayStruct.m_2_signed64[0][8].offset()).isEqualTo(1 + (1 * 8 * 8));
        assertThat(arrayStruct.m_2_signed64[0][9].offset()).isEqualTo(1 + (1 * 9 * 8));
        assertThat(arrayStruct.m_2_signed64[0][10].offset()).isEqualTo(1 + (1 * 10 * 8));
        assertThat(arrayStruct.m_2_signed64[0][11].offset()).isEqualTo(1 + (1 * 11 * 8));

        assertThat(arrayStruct.m_2_signed64[1][0].offset()).isEqualTo(1 + (0) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][1].offset()).isEqualTo(1 + (1 * 1 * 8) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][2].offset()).isEqualTo(1 + (1 * 2 * 8) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][3].offset()).isEqualTo(1 + (1 * 3 * 8) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][4].offset()).isEqualTo(1 + (1 * 4 * 8) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][5].offset()).isEqualTo(1 + (1 * 5 * 8) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][6].offset()).isEqualTo(1 + (1 * 6 * 8) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][7].offset()).isEqualTo(1 + (1 * 7 * 8) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][8].offset()).isEqualTo(1 + (1 * 8 * 8) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][9].offset()).isEqualTo(1 + (1 * 9 * 8) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][10].offset()).isEqualTo(1 + (1 * 10 * 8) + (12 * 8));
        assertThat(arrayStruct.m_2_signed64[1][11].offset()).isEqualTo(1 + (1 * 11 * 8) + (12 * 8));

        assertThat(arrayStruct.m_2_signed64[2][0].offset()).isEqualTo(1 + (0) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][1].offset()).isEqualTo(1 + (1 * 1 * 8) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][2].offset()).isEqualTo(1 + (1 * 2 * 8) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][3].offset()).isEqualTo(1 + (1 * 3 * 8) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][4].offset()).isEqualTo(1 + (1 * 4 * 8) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][5].offset()).isEqualTo(1 + (1 * 5 * 8) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][6].offset()).isEqualTo(1 + (1 * 6 * 8) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][7].offset()).isEqualTo(1 + (1 * 7 * 8) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][8].offset()).isEqualTo(1 + (1 * 8 * 8) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][9].offset()).isEqualTo(1 + (1 * 9 * 8) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][10].offset()).isEqualTo(1 + (1 * 10 * 8) + (2 * 12 * 8));
        assertThat(arrayStruct.m_2_signed64[2][11].offset()).isEqualTo(1 + (1 * 11 * 8) + (2 * 12 * 8));

        assertThat(arrayStruct.m_3_float32.offset()).isEqualTo(1 + (3 * 12 * 8));

        // Then Assert Absolute Position
        assertThat(arrayStruct.m_1_signed08.absolutePosition()).isEqualTo(offset);

        assertThat(arrayStruct.m_2_signed64[0][0].absolutePosition()).isEqualTo((1 + (0)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][1].absolutePosition()).isEqualTo((1 + (1 * 1 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][2].absolutePosition()).isEqualTo((1 + (1 * 2 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][3].absolutePosition()).isEqualTo((1 + (1 * 3 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][4].absolutePosition()).isEqualTo((1 + (1 * 4 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][5].absolutePosition()).isEqualTo((1 + (1 * 5 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][6].absolutePosition()).isEqualTo((1 + (1 * 6 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][7].absolutePosition()).isEqualTo((1 + (1 * 7 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][8].absolutePosition()).isEqualTo((1 + (1 * 8 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][9].absolutePosition()).isEqualTo((1 + (1 * 9 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][10].absolutePosition()).isEqualTo((1 + (1 * 10 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[0][11].absolutePosition()).isEqualTo((1 + (1 * 11 * 8)) + offset);

        assertThat(arrayStruct.m_2_signed64[1][0].absolutePosition()).isEqualTo((1 + (0) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][1].absolutePosition()).isEqualTo((1 + (1 * 1 * 8) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][2].absolutePosition()).isEqualTo((1 + (1 * 2 * 8) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][3].absolutePosition()).isEqualTo((1 + (1 * 3 * 8) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][4].absolutePosition()).isEqualTo((1 + (1 * 4 * 8) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][5].absolutePosition()).isEqualTo((1 + (1 * 5 * 8) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][6].absolutePosition()).isEqualTo((1 + (1 * 6 * 8) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][7].absolutePosition()).isEqualTo((1 + (1 * 7 * 8) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][8].absolutePosition()).isEqualTo((1 + (1 * 8 * 8) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][9].absolutePosition()).isEqualTo((1 + (1 * 9 * 8) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][10].absolutePosition()).isEqualTo((1 + (1 * 10 * 8) + (12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[1][11].absolutePosition()).isEqualTo((1 + (1 * 11 * 8) + (12 * 8)) + offset);

        assertThat(arrayStruct.m_2_signed64[2][0].absolutePosition()).isEqualTo((1 + (0) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][1].absolutePosition()).isEqualTo((1 + (1 * 1 * 8) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][2].absolutePosition()).isEqualTo((1 + (1 * 2 * 8) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][3].absolutePosition()).isEqualTo((1 + (1 * 3 * 8) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][4].absolutePosition()).isEqualTo((1 + (1 * 4 * 8) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][5].absolutePosition()).isEqualTo((1 + (1 * 5 * 8) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][6].absolutePosition()).isEqualTo((1 + (1 * 6 * 8) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][7].absolutePosition()).isEqualTo((1 + (1 * 7 * 8) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][8].absolutePosition()).isEqualTo((1 + (1 * 8 * 8) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][9].absolutePosition()).isEqualTo((1 + (1 * 9 * 8) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][10].absolutePosition()).isEqualTo((1 + (1 * 10 * 8) + (2 * 12 * 8)) + offset);
        assertThat(arrayStruct.m_2_signed64[2][11].absolutePosition()).isEqualTo((1 + (1 * 11 * 8) + (2 * 12 * 8)) + offset);

        assertThat(arrayStruct.m_3_float32.absolutePosition()).isEqualTo((1 + (3 * 12 * 8)) + offset);


        // Then Assert Absolut Position
        assertThat(arrayStruct.m_1_signed08.absolutePosition()).isEqualTo(offset);

    }

    @Test
    public void test_struct_that_2_dimension_array_member_positions_are_correct() {
        // Given
        My2DimArrayStruct arrayStruct = new My2DimArrayStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_struct_that_2_dimension_array_member_positions_are_correct(arrayStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_2_dimension_array_member_positions_are_correct_with_offset() {
        // Given
        My2DimArrayStruct arrayStruct = new My2DimArrayStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_struct_that_2_dimension_array_member_positions_are_correct(arrayStruct, byteBuffer, 123);
    }

    static class My2DimArrayStruct extends Struct {
        final Signed8 m_1_signed08 = new Signed8();
        final Signed64[][] m_2_signed64 = array(new Signed64[3][12]);
        final Float32 m_3_float32 = new Float32();

        My2DimArrayStruct() {
            super();
        }

    }

    static class MyArrayStruct extends Struct {
        final Signed8 m_1_signed08 = new Signed8();
        final Signed64[] m_2_signed64 = array(new Signed64[12]);
        final Float32 m_3_float32 = new Float32();

        MyArrayStruct() {
            super();
        }

    }

}
