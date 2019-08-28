package eu.dirk.haase.type.test;

import eu.dirk.haase.type.Struct;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

@RunWith(BlockJUnit4ClassRunner.class)
public class StructBitFieldTest {

    @Test
    public void test_struct_that_bitField_128_values_are_correct_read_big_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE(128);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 128, 0);
    }

    @Test
    public void test_struct_that_bitField_128_values_are_correct_read_little_endian_with_offset() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(128);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 128, 23);
    }

    @Test
    public void test_struct_that_bitField_128_values_are_correct_read_little_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(128);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 128, 0);
    }

    @Test
    public void test_struct_that_bitField_64_values_are_correct_read_big_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE(64);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 64, 0);
    }

    @Test
    public void test_struct_that_bitField_64_values_are_correct_read_little_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(64);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 64, 0);
    }

    @Test
    public void test_struct_that_bitField_70_values_are_correct_read_big_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE(72);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 72, 0);
    }

    @Test
    public void test_struct_that_bitField_70_values_are_correct_read_little_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(72);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 72, 0);
    }

    private void test_struct_that_bitField_values_are_correct_read(MyAbstractBitFieldStruct scalarStruct, ByteBuffer byteBuffer, final int nbrOfBits, int structOffset) {
        // Given
        BitSet bitSet = new BitSet();
        bitSet.set(2);
        bitSet.set(9);
        bitSet.set(62);
        bitSet.set(nbrOfBits - 4);

        byte signed8 = 123 | Byte.MIN_VALUE;
        byte[] bitFieldBytes = bitSet.toByteArray();
        long signed64 = 1234L | Long.MIN_VALUE;

        byteBuffer.position(structOffset);

        byteBuffer.put(signed8);
        byteBuffer.put(bitFieldBytes);
        byteBuffer.putLong(signed64);

        // When
        scalarStruct.initByteBuffer(byteBuffer, structOffset);
        byteBuffer.position(structOffset);
//
//        scalarStruct.m_1_signed08.set(signed8);
//        scalarStruct.m_2_bitField.set(bitFieldBytes);
//        scalarStruct.m_3_signed64.set(signed64);

        // Then
        Assertions.assertThat(scalarStruct.byteOrder()).isEqualTo(byteBuffer.order());
        Assertions.assertThat(scalarStruct.m_1_signed08.get()).isEqualTo(signed8);
        Assertions.assertThat(scalarStruct.m_2_bitField.toByteArray()).isEqualTo(bitFieldBytes);
        Assertions.assertThat(scalarStruct.m_3_signed64.get()).isEqualTo(signed64);
        Assertions.assertThat(scalarStruct.size()).isEqualTo(1 + bitFieldBytes.length + 8);
    }

    private void test_struct_that_bitField_values_are_correct_written(MyAbstractBitFieldStruct scalarStruct, ByteBuffer byteBuffer, final int nbrOfBits, int structOffset) {
        // Given
        BitSet bitSet = new BitSet();
        bitSet.set(2);
        bitSet.set(9);
        bitSet.set(62);
        bitSet.set(nbrOfBits - 4);

        byte signed8 = 123 | Byte.MIN_VALUE;
        byte[] bitFieldBytes = bitSet.toByteArray();
        long signed64 = 1234L | Long.MIN_VALUE;

        byteBuffer.position(0);

        byteBuffer.put(signed8);
        byteBuffer.put(bitFieldBytes);
        byteBuffer.putLong(signed64);

        // When
        scalarStruct.initByteBuffer(byteBuffer, structOffset);
        byteBuffer.position(0);

        scalarStruct.m_1_signed08.set(signed8);
        scalarStruct.m_2_bitField.set(bitFieldBytes);
        scalarStruct.m_3_signed64.set(signed64);

        // Then
        Assertions.assertThat(scalarStruct.byteOrder()).isEqualTo(byteBuffer.order());
        Assertions.assertThat(scalarStruct.m_1_signed08.get()).isEqualTo(signed8);
        Assertions.assertThat(scalarStruct.m_2_bitField.toByteArray()).isEqualTo(bitFieldBytes);
        Assertions.assertThat(scalarStruct.m_3_signed64.get()).isEqualTo(signed64);
        Assertions.assertThat(scalarStruct.size()).isEqualTo(1 + bitFieldBytes.length + 8);
    }


    static class MyAbstractBitFieldStruct extends Struct {
        final Signed8 m_1_signed08 = new Signed8();
        final BitField m_2_bitField;
        final Signed64 m_3_signed64 = new Signed64();

        MyAbstractBitFieldStruct(final int nbrOfBits) {
            super();
            m_2_bitField = new BitField(nbrOfBits);
        }

        MyAbstractBitFieldStruct(final ByteOrder byteOrder, final int nbrOfBits) {
            super(byteOrder);
            m_2_bitField = new BitField(nbrOfBits);
        }
    }


    static class MyBitFieldStruct extends MyAbstractBitFieldStruct {


        public MyBitFieldStruct(final int nbrOfBits) {
            super(nbrOfBits);
        }

    }

    static class MyBitFieldStructBE extends MyAbstractBitFieldStruct {

        public MyBitFieldStructBE(final int nbrOfBits) {
            super(ByteOrder.BIG_ENDIAN, nbrOfBits);
        }


    }

    static class MyBitFieldStructLE extends MyAbstractBitFieldStruct {

        public MyBitFieldStructLE(final int nbrOfBits) {
            super(ByteOrder.LITTLE_ENDIAN, nbrOfBits);
        }


    }


}
