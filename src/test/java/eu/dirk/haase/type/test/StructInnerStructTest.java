package eu.dirk.haase.type.test;

import eu.dirk.haase.type.Struct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class StructInnerStructTest {


    private void test_packet_struct_that_member_values_are_correct_with_roundtrip(MyAbstractStructInnerStruct scalarStruct, ByteBuffer byteBuffer, int offset) {
        // Given
        float value_1 = 123f;
        double value_2 = 321d;
        float value_3 = 234f;
        double value_4 = 543d;
        byte value_5 = 12;
        short value_6 = 433;
        byte value_7 = 24;
        short value_8 = 231;
        // When
        scalarStruct.initByteBuffer(byteBuffer, offset);
        scalarStruct.m_1_float32.set(value_1);
        scalarStruct.m_2_float64.set(value_2);

        scalarStruct.m_3_innerStruct.m_1_float32.set(value_3);
        scalarStruct.m_3_innerStruct.m_2_float64.set(value_4);
        scalarStruct.m_3_innerStruct.m_3_signed08.set(value_5);
        scalarStruct.m_3_innerStruct.m_4_signed16.set(value_6);

        scalarStruct.m_4_signed08.set(value_7);
        scalarStruct.m_5_signed16.set(value_8);

        // Then
        assertThat(scalarStruct.m_1_float32.get()).isEqualTo(value_1);
        assertThat(scalarStruct.m_2_float64.get()).isEqualTo(value_2);

        assertThat(scalarStruct.m_3_innerStruct.m_1_float32.get()).isEqualTo(value_3);
        assertThat(scalarStruct.m_3_innerStruct.m_2_float64.get()).isEqualTo(value_4);
        assertThat(scalarStruct.m_3_innerStruct.m_3_signed08.get()).isEqualTo(value_5);
        assertThat(scalarStruct.m_3_innerStruct.m_4_signed16.get()).isEqualTo(value_6);

        assertThat(scalarStruct.m_4_signed08.get()).isEqualTo(value_7);
        assertThat(scalarStruct.m_5_signed16.get()).isEqualTo(value_8);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_with_roundtrip_native_order() {
        // Given
        MyAbstractStructInnerStruct scalarStruct = new MyStructInnerStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_packet_struct_that_member_values_are_correct_with_roundtrip(scalarStruct, byteBuffer, 0);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_with_roundtrip_native_order_with_offset() {
        // Given
        MyAbstractStructInnerStruct scalarStruct = new MyStructInnerStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_packet_struct_that_member_values_are_correct_with_roundtrip(scalarStruct, byteBuffer, 123);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_written_native_order() {
        // Given
        MyAbstractStructInnerStruct scalarStruct = new MyStructInnerStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_struct_that_of_the_member_positions_are_correct(scalarStruct, byteBuffer, 0);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_written_native_order_with_offset() {
        // Given
        MyAbstractStructInnerStruct scalarStruct = new MyStructInnerStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_struct_that_of_the_member_positions_are_correct(scalarStruct, byteBuffer, 123);
    }

    private void test_struct_that_of_the_member_positions_are_correct(MyAbstractStructInnerStruct scalarStruct, ByteBuffer byteBuffer, int offset) {
        // When
        scalarStruct.initByteBuffer(byteBuffer, offset);
        // Then
        assertThat(scalarStruct.m_1_float32.offset()).isEqualTo(0);
        assertThat(scalarStruct.m_2_float64.offset()).isEqualTo(4);

        assertThat(scalarStruct.m_3_innerStruct.m_1_float32.offset()).isEqualTo(4 + 8);
        assertThat(scalarStruct.m_3_innerStruct.m_2_float64.offset()).isEqualTo((4 + 8) + 4);
        assertThat(scalarStruct.m_3_innerStruct.m_3_signed08.offset()).isEqualTo((4 + 8) + 4 + 8);
        assertThat(scalarStruct.m_3_innerStruct.m_4_signed16.offset()).isEqualTo((4 + 8) + 4 + 8 + 1);

        assertThat(scalarStruct.m_4_signed08.offset()).isEqualTo(((4 + 8) + 4 + 8 + 1 + 2));
        assertThat(scalarStruct.m_5_signed16.offset()).isEqualTo(((4 + 8) + 4 + 8 + 1 + 2) + 1);

        assertThat(scalarStruct.m_1_float32.absolutePosition()).isEqualTo(offset);
        assertThat(scalarStruct.m_2_float64.absolutePosition()).isEqualTo((4) + offset);

        assertThat(scalarStruct.m_3_innerStruct.m_1_float32.absolutePosition()).isEqualTo((4 + 8) + offset);
        assertThat(scalarStruct.m_3_innerStruct.m_2_float64.absolutePosition()).isEqualTo(((4 + 8) + 4) + offset);
        assertThat(scalarStruct.m_3_innerStruct.m_3_signed08.absolutePosition()).isEqualTo(((4 + 8) + 4 + 8) + offset);
        assertThat(scalarStruct.m_3_innerStruct.m_4_signed16.absolutePosition()).isEqualTo(((4 + 8) + 4 + 8 + 1) + offset);

        assertThat(scalarStruct.m_4_signed08.absolutePosition()).isEqualTo((((4 + 8) + 4 + 8 + 1 + 2)) + offset);
        assertThat(scalarStruct.m_5_signed16.absolutePosition()).isEqualTo((((4 + 8) + 4 + 8 + 1 + 2) + 1) + offset);
    }

    static class MyAbstractStructInnerStruct extends Struct {
        final Float32 m_1_float32 = new Float32();
        final Float64 m_2_float64 = new Float64();
        final MyInnerStruct m_3_innerStruct = inner(new MyInnerStruct(m_2_float64));
        final Signed8 m_4_signed08 = new Signed8();
        final Signed16 m_5_signed16 = new Signed16();

        MyAbstractStructInnerStruct() {
            super();
        }

        MyAbstractStructInnerStruct(final ByteOrder byteOrder) {
            super(byteOrder);
        }
    }


    static class MyInnerStruct extends Struct {
        final Float32 m_1_float32 = new Float32();
        final Float64 m_2_float64 = new Float64();
        final Signed8 m_3_signed08 = new Signed8();
        final Signed16 m_4_signed16 = new Signed16();

        MyInnerStruct(final AbstractMember afterMember) {
            super(afterMember);
        }

    }

    static class MyStructInnerStruct extends MyAbstractStructInnerStruct {


    }

    static class MyStructInnerStructBE extends MyAbstractStructInnerStruct {

        public MyStructInnerStructBE() {
            super(ByteOrder.BIG_ENDIAN);
        }


    }

    static class MyStructInnerStructLE extends MyAbstractStructInnerStruct {

        public MyStructInnerStructLE() {
            super(ByteOrder.LITTLE_ENDIAN);
        }


    }

}
