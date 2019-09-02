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


    private void test_struct_that_member_and_inner_struct_positions_are_correct(MyAbstractStructInnerStruct nestedStruct, ByteBuffer byteBuffer, int offset) {
        // When
        nestedStruct.initByteBuffer(byteBuffer, offset);
        // Then Assert Offset
        assertThat(nestedStruct.m_1_float32.offset()).isEqualTo(0);
        assertThat(nestedStruct.m_2_float64.offset()).isEqualTo(4);

        assertThat(nestedStruct.m_3_innerStruct.m_1_float32.offset()).isEqualTo(4 + 8);
        assertThat(nestedStruct.m_3_innerStruct.m_2_float64.offset()).isEqualTo((4 + 8) + 4);
        assertThat(nestedStruct.m_3_innerStruct.m_3_signed08.offset()).isEqualTo((4 + 8) + 4 + 8);
        assertThat(nestedStruct.m_3_innerStruct.m_4_signed16.offset()).isEqualTo((4 + 8) + 4 + 8 + 1);

        assertThat(nestedStruct.m_4_signed08.offset()).isEqualTo(((4 + 8) + 4 + 8 + 1 + 2));
        assertThat(nestedStruct.m_5_signed16.offset()).isEqualTo(((4 + 8) + 4 + 8 + 1 + 2) + 1);

        assertThat(nestedStruct.m_6_innerStruct.m_1_float32.offset()).isEqualTo((((4 + 8) + 4 + 8 + 1 + 2) + 1 + 2));
        assertThat(nestedStruct.m_6_innerStruct.m_2_float64.offset()).isEqualTo((((4 + 8) + 4 + 8 + 1 + 2) + 1 + 2) + 4);
        assertThat(nestedStruct.m_6_innerStruct.m_3_signed08.offset()).isEqualTo((((4 + 8) + 4 + 8 + 1 + 2) + 1 + 2) + 4 + 8);
        assertThat(nestedStruct.m_6_innerStruct.m_4_signed16.offset()).isEqualTo((((4 + 8) + 4 + 8 + 1 + 2) + 1 + 2) + 4 + 8 + 1);

        assertThat(nestedStruct.m_7_signed08.offset()).isEqualTo((((4 + 8) + 4 + 8 + 1 + 2) + 1 + 2) + 4 + 8 + 1 + 2);

        // Then Assert Absolut Position
        assertThat(nestedStruct.m_1_float32.absolutePosition()).isEqualTo(offset);
        assertThat(nestedStruct.m_2_float64.absolutePosition()).isEqualTo((4) + offset);

        assertThat(nestedStruct.m_3_innerStruct.m_1_float32.absolutePosition()).isEqualTo((4 + 8) + offset);
        assertThat(nestedStruct.m_3_innerStruct.m_2_float64.absolutePosition()).isEqualTo(((4 + 8) + 4) + offset);
        assertThat(nestedStruct.m_3_innerStruct.m_3_signed08.absolutePosition()).isEqualTo(((4 + 8) + 4 + 8) + offset);
        assertThat(nestedStruct.m_3_innerStruct.m_4_signed16.absolutePosition()).isEqualTo(((4 + 8) + 4 + 8 + 1) + offset);

        assertThat(nestedStruct.m_4_signed08.absolutePosition()).isEqualTo((((4 + 8) + 4 + 8 + 1 + 2)) + offset);
        assertThat(nestedStruct.m_5_signed16.absolutePosition()).isEqualTo((((4 + 8) + 4 + 8 + 1 + 2) + 1) + offset);

        assertThat(nestedStruct.m_6_innerStruct.m_1_float32.absolutePosition()).isEqualTo(((((4 + 8) + 4 + 8 + 1 + 2) + 1 + 2)) + offset);
        assertThat(nestedStruct.m_6_innerStruct.m_2_float64.absolutePosition()).isEqualTo(((((4 + 8) + 4 + 8 + 1 + 2) + 1 + 2) + 4) + offset);
        assertThat(nestedStruct.m_6_innerStruct.m_3_signed08.absolutePosition()).isEqualTo(((((4 + 8) + 4 + 8 + 1 + 2) + 1 + 2) + 4 + 8) + offset);
        assertThat(nestedStruct.m_6_innerStruct.m_4_signed16.absolutePosition()).isEqualTo(((((4 + 8) + 4 + 8 + 1 + 2) + 1 + 2) + 4 + 8 + 1) + offset);

        assertThat(nestedStruct.m_7_signed08.absolutePosition()).isEqualTo(((((4 + 8) + 4 + 8 + 1 + 2) + 1 + 2) + 4 + 8 + 1 + 2) + offset);
    }

    @Test
    public void test_struct_that_member_and_inner_struct_positions_are_correct_native_order() {
        // Given
        MyAbstractStructInnerStruct nestedStruct = new MyStructInnerStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_struct_that_member_and_inner_struct_positions_are_correct(nestedStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_member_and_inner_struct_positions_are_correct_native_order_with_offset() {
        // Given
        MyAbstractStructInnerStruct nestedStruct = new MyStructInnerStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_struct_that_member_and_inner_struct_positions_are_correct(nestedStruct, byteBuffer, 123);
    }

    private void test_struct_that_member_and_inner_struct_values_are_correct_with_roundtrip(MyAbstractStructInnerStruct nestedStruct, ByteBuffer byteBuffer, int offset) {
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
        nestedStruct.initByteBuffer(byteBuffer, offset);
        nestedStruct.m_1_float32.set(value_1);
        nestedStruct.m_2_float64.set(value_2);

        nestedStruct.m_3_innerStruct.m_1_float32.set(value_3);
        nestedStruct.m_3_innerStruct.m_2_float64.set(value_4);
        nestedStruct.m_3_innerStruct.m_3_signed08.set(value_5);
        nestedStruct.m_3_innerStruct.m_4_signed16.set(value_6);

        nestedStruct.m_4_signed08.set(value_7);
        nestedStruct.m_5_signed16.set(value_8);

        nestedStruct.m_6_innerStruct.m_1_float32.set(value_3);
        nestedStruct.m_6_innerStruct.m_2_float64.set(value_4);
        nestedStruct.m_6_innerStruct.m_3_signed08.set(value_5);
        nestedStruct.m_6_innerStruct.m_4_signed16.set(value_6);

        nestedStruct.m_7_signed08.set(value_7);

        // Then
        assertThat(nestedStruct.m_1_float32.get()).isEqualTo(value_1);
        assertThat(nestedStruct.m_2_float64.get()).isEqualTo(value_2);

        assertThat(nestedStruct.m_3_innerStruct.m_1_float32.get()).isEqualTo(value_3);
        assertThat(nestedStruct.m_3_innerStruct.m_2_float64.get()).isEqualTo(value_4);
        assertThat(nestedStruct.m_3_innerStruct.m_3_signed08.get()).isEqualTo(value_5);
        assertThat(nestedStruct.m_3_innerStruct.m_4_signed16.get()).isEqualTo(value_6);

        assertThat(nestedStruct.m_4_signed08.get()).isEqualTo(value_7);
        assertThat(nestedStruct.m_5_signed16.get()).isEqualTo(value_8);

        assertThat(nestedStruct.m_6_innerStruct.m_1_float32.get()).isEqualTo(value_3);
        assertThat(nestedStruct.m_6_innerStruct.m_2_float64.get()).isEqualTo(value_4);
        assertThat(nestedStruct.m_6_innerStruct.m_3_signed08.get()).isEqualTo(value_5);
        assertThat(nestedStruct.m_6_innerStruct.m_4_signed16.get()).isEqualTo(value_6);

        assertThat(nestedStruct.m_7_signed08.get()).isEqualTo(value_7);
    }

    @Test
    public void test_struct_that_member_and_inner_struct_values_are_correct_with_roundtrip_native_order() {
        // Given
        MyAbstractStructInnerStruct nestedStruct = new MyStructInnerStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_struct_that_member_and_inner_struct_values_are_correct_with_roundtrip(nestedStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_member_and_inner_struct_values_are_correct_with_roundtrip_native_order_with_offset() {
        // Given
        MyAbstractStructInnerStruct nestedStruct = new MyStructInnerStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_struct_that_member_and_inner_struct_values_are_correct_with_roundtrip(nestedStruct, byteBuffer, 123);
    }

    private void test_struct_that_nested_inner_struct_positions_are_correct(MyStructWithInnerInnerStruct nestedStruct, int offset) {
        // When
        nestedStruct.setStructAbsolutePosition(offset);
        // Then
        assertThat(nestedStruct.m_1_float32.offset()).isEqualTo(0);
        assertThat(nestedStruct.m_2_float64.offset()).isEqualTo(4);

        assertThat(nestedStruct.m_3_innerStruct.m_1_float32.offset()).isEqualTo(4 + 8);
        assertThat(nestedStruct.m_3_innerStruct.m_2_innerStruct.m_1_float32.offset()).isEqualTo(4 + 8 + 4);
        assertThat(nestedStruct.m_3_innerStruct.m_2_innerStruct.m_2_signed16.offset()).isEqualTo((4 + 8 + 4) + 4);
        assertThat(nestedStruct.m_3_innerStruct.m_3_signed16.offset()).isEqualTo((4 + 8 + 4) + 4 + 2);

        assertThat(nestedStruct.m_4_signed08.offset()).isEqualTo((4 + 8 + 4) + 4 + 2 + 2);

        // Then Assert Absolut Position
        assertThat(nestedStruct.m_1_float32.absolutePosition()).isEqualTo(offset);
        assertThat(nestedStruct.m_2_float64.absolutePosition()).isEqualTo((4) + offset);

        assertThat(nestedStruct.m_3_innerStruct.m_1_float32.absolutePosition()).isEqualTo((4 + 8) + offset);
        assertThat(nestedStruct.m_3_innerStruct.m_2_innerStruct.m_1_float32.absolutePosition()).isEqualTo((4 + 8 + 4) + offset);
        assertThat(nestedStruct.m_3_innerStruct.m_2_innerStruct.m_2_signed16.absolutePosition()).isEqualTo(((4 + 8 + 4) + 4) + offset);
        assertThat(nestedStruct.m_3_innerStruct.m_3_signed16.absolutePosition()).isEqualTo(((4 + 8 + 4) + 4 + 2) + offset);

        assertThat(nestedStruct.m_4_signed08.absolutePosition()).isEqualTo(((4 + 8 + 4) + 4 + 2 + 2) + offset);
    }

    @Test
    public void test_struct_that_nested_inner_struct_positions_are_correct_native_order() {
        // Given
        MyStructWithInnerInnerStruct nestedStruct = new MyStructWithInnerInnerStruct();
        // Test
        test_struct_that_nested_inner_struct_positions_are_correct(nestedStruct, 0);
    }

    @Test
    public void test_struct_that_nested_inner_struct_positions_are_correct_native_order_with_offset() {
        // Given
        MyStructWithInnerInnerStruct nestedStruct = new MyStructWithInnerInnerStruct();
        // Test
        test_struct_that_nested_inner_struct_positions_are_correct(nestedStruct, 123);
    }

    static class MyAbstractStructInnerStruct extends Struct {
        final Float32 m_1_float32 = new Float32();
        final Float64 m_2_float64 = new Float64();
        final MyInnerStruct m_3_innerStruct = inner(new MyInnerStruct(m_2_float64));
        final Signed8 m_4_signed08 = new Signed8();
        final Signed16 m_5_signed16 = new Signed16();
        final MyInnerStruct m_6_innerStruct = inner(new MyInnerStruct(m_5_signed16));
        final Signed8 m_7_signed08 = new Signed8();

        MyAbstractStructInnerStruct() {
            super();
        }

        MyAbstractStructInnerStruct(final ByteOrder byteOrder) {
            super(byteOrder);
        }
    }

    static class MyForInnerInnerStruct extends Struct {
        final Float32 m_1_float32 = new Float32();
        final Signed16 m_2_signed16 = new Signed16();

        MyForInnerInnerStruct(final AbstractMember afterMember) {
            super(afterMember);
        }

    }

    static class MyInnerInnerStruct extends Struct {
        final Float32 m_1_float32 = new Float32();
        final MyForInnerInnerStruct m_2_innerStruct = inner(new MyForInnerInnerStruct(m_1_float32));
        final Signed16 m_3_signed16 = new Signed16();

        MyInnerInnerStruct(final AbstractMember afterMember) {
            super(afterMember);
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

    static class MyStructWithInnerInnerStruct extends Struct {
        final Float32 m_1_float32 = new Float32();
        final Float64 m_2_float64 = new Float64();
        final MyInnerInnerStruct m_3_innerStruct = inner(new MyInnerInnerStruct(m_2_float64));
        final Signed8 m_4_signed08 = new Signed8();

        MyStructWithInnerInnerStruct() {
            super();
        }

    }

}
