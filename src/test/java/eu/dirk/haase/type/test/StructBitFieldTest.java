package eu.dirk.haase.type.test;

import eu.dirk.haase.type.Struct;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class StructBitFieldTest {

    @Test
    public void test_struct_that_of_the_member_positions_are_in_sync_with_byte_buffer() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_of_the_member_positions_are_in_sync_with_byte_buffer(0);
    }

    private void test_struct_that_of_the_member_positions_are_in_sync_with_byte_buffer(int offset) {
        // Given
        int nbrOfBits = 72;
        BitSet bitSet = new BitSet();
        bitSet.set(2);
        bitSet.set(9);
        bitSet.set(62);
        bitSet.set(nbrOfBits - 4);
        byte[] bitFieldBytes = bitSet.toByteArray();

        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStruct();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        scalarStruct.initByteBuffer(byteBuffer, offset);
        byteBuffer.position(offset);
        // When
        // Then
        int structOffset = offset;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.m_1_signed08.offset()).isEqualTo(structOffset - offset);
        assertThat(structOffset).isEqualTo(scalarStruct.m_1_signed08.absolutePosition());
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.m_1_signed08.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.m_2_bitField.offset()).isEqualTo(structOffset - offset);
        assertThat(structOffset).isEqualTo(scalarStruct.m_2_bitField.absolutePosition());
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.position(byteBuffer.position() + bitFieldBytes.length);
        structOffset += scalarStruct.m_2_bitField.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.m_3_signed64.offset()).isEqualTo(structOffset - offset);
        assertThat(structOffset).isEqualTo(scalarStruct.m_3_signed64.absolutePosition());
        // bewege die Position jeweils um 8 Byte weiter
        byteBuffer.getLong();
        structOffset += scalarStruct.m_3_signed64.bitLength() / 8;

        Assertions.assertThat(scalarStruct.size()).isEqualTo(byteBuffer.position() - offset);
        Assertions.assertThat(scalarStruct.size()).isEqualTo(structOffset - offset);
    }

    @Test
    public void test_struct_that_bitField_128_values_are_correct_read_big_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_bitField_128_values_are_correct_read_little_endian_with_offset() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 23);
    }

    @Test
    public void test_struct_that_bitField_128_values_are_correct_read_little_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_bitField_64_values_are_correct_read_big_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_bitField_64_values_are_correct_read_little_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_bitField_70_values_are_correct_read_big_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_bitField_70_values_are_correct_read_little_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_bitField_70_values_are_correct_written_big_endian_offset() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_written(scalarStruct, byteBuffer, 234);
    }

    @Test
    public void test_struct_that_bitField_70_values_are_correct_written_big_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_written(scalarStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_bitField_70_values_are_correct_written_little_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_written(scalarStruct, byteBuffer, 0);
    }

    private void test_struct_that_bitField_values_are_correct_written(MyAbstractBitFieldStruct scalarStruct, ByteBuffer byteBuffer, int structOffset) {
        // Given
        BitSet bitSet = new BitSet();
        bitSet.set(2);
        bitSet.set(9);
        bitSet.set(62);
        bitSet.set(72 - 4);
        byte[] bitFieldBytes = bitSet.toByteArray();
        byte[] buffer = new byte[bitFieldBytes.length];

        byte signed8 = 123 + Byte.MIN_VALUE;
        long signed64 = 1234L + Long.MIN_VALUE;

        byteBuffer.position(structOffset);
        scalarStruct.initByteBuffer(byteBuffer, structOffset);

        scalarStruct.m_1_signed08.set(signed8);
        scalarStruct.m_2_bitField.set(bitFieldBytes);
        scalarStruct.m_3_signed64.set(signed64);

        // When
        byteBuffer.position(structOffset);

        // Then
        Assertions.assertThat(scalarStruct.byteOrder()).isEqualTo(byteBuffer.order());
        Assertions.assertThat(byteBuffer.get()).isEqualTo(signed8);
        byteBuffer.get(buffer);
        Assertions.assertThat(buffer).isEqualTo(bitFieldBytes);
        Assertions.assertThat(byteBuffer.getLong()).isEqualTo(signed64);
        Assertions.assertThat(scalarStruct.size()).isEqualTo(1 + bitFieldBytes.length + 8);
    }

    @Test
    public void test_struct_that_bitField_values_are_correct_with_roundtrip_big_endian_with_offset() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_with_roundtrip(scalarStruct, byteBuffer, 231);
    }

    @Test
    public void test_struct_that_bitField_values_are_correct_with_roundtrip_big_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_with_roundtrip(scalarStruct, byteBuffer, 0);
    }

    @Test
    public void test_struct_that_bitField_values_are_correct_with_roundtrip_little_endian() {
        // Given
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_with_roundtrip(scalarStruct, byteBuffer, 0);
    }

    private void test_struct_that_bitField_values_are_correct_with_roundtrip(MyAbstractBitFieldStruct scalarStruct, ByteBuffer byteBuffer, int structOffset) {
        // Given
        BitSet bitSet = new BitSet();
        bitSet.set(2);
        bitSet.set(9);
        bitSet.set(62);
        bitSet.set(72 - 4);
        byte[] bitFieldBytes = bitSet.toByteArray();
        byte[] buffer = new byte[bitFieldBytes.length];

        byte signed8 = 123 + Byte.MIN_VALUE;
        long signed64 = 1234L + Long.MIN_VALUE;

        byteBuffer.position(structOffset);
        scalarStruct.initByteBuffer(byteBuffer, structOffset);

        scalarStruct.m_1_signed08.set(signed8);
        scalarStruct.m_2_bitField.set(bitFieldBytes);
        scalarStruct.m_3_signed64.set(signed64);

        // When
        byteBuffer.position(structOffset);

        // Then
        Assertions.assertThat(scalarStruct.byteOrder()).isEqualTo(byteBuffer.order());
        Assertions.assertThat(scalarStruct.m_1_signed08.get()).isEqualTo(signed8);
        Assertions.assertThat(scalarStruct.m_2_bitField.toByteArray()).isEqualTo(bitFieldBytes);
        Assertions.assertThat(scalarStruct.m_3_signed64.get()).isEqualTo(signed64);
        Assertions.assertThat(scalarStruct.size()).isEqualTo(1 + bitFieldBytes.length + 8);
    }

    private void test_struct_that_bitField_values_are_correct_read(MyAbstractBitFieldStruct scalarStruct, ByteBuffer byteBuffer, int structOffset) {
        // Given
        BitSet bitSet = new BitSet();
        bitSet.set(2);
        bitSet.set(9);
        bitSet.set(62);
        bitSet.set(72 - 4);
        byte[] bitFieldBytes = bitSet.toByteArray();

        byte signed8 = 123 + Byte.MIN_VALUE;
        long signed64 = 1234L + Long.MIN_VALUE;

        byteBuffer.position(structOffset);

        byteBuffer.put(signed8);
        byteBuffer.put(bitFieldBytes);
        byteBuffer.putLong(signed64);

        // When
        scalarStruct.initByteBuffer(byteBuffer, structOffset);
        byteBuffer.position(structOffset);

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
        final BitField m_2_bitField = new BitField(72);
        final Signed64 m_3_signed64 = new Signed64();

        MyAbstractBitFieldStruct() {
            super();
        }

        MyAbstractBitFieldStruct(final ByteOrder byteOrder) {
            super(byteOrder);
        }
    }


    static class MyBitFieldStruct extends MyAbstractBitFieldStruct {


        public MyBitFieldStruct() {
            super();
        }

    }

    static class MyBitFieldStructBE extends MyAbstractBitFieldStruct {

        public MyBitFieldStructBE() {
            super(ByteOrder.BIG_ENDIAN);
        }


    }

    static class MyBitFieldStructLE extends MyAbstractBitFieldStruct {

        public MyBitFieldStructLE() {
            super(ByteOrder.LITTLE_ENDIAN);
        }


    }


}
