package eu.dirk.haase.type.test;

import eu.dirk.haase.type.Struct;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class StructUTFStringTest {

    @Test
    public void test_struct_that_of_the_member_positions_are_in_sync_with_byte_buffer() {
        test_struct_that_of_the_member_positions_are_in_sync_with_byte_buffer(0);
    }

    private void test_struct_that_of_the_member_positions_are_in_sync_with_byte_buffer(int offset) {
        // Given
        String UTF_STRING = "Hallo12345678900987654321";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE(UTF_STRING.length());

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
        assertThat(scalarStruct.m_2_utfString.offset()).isEqualTo(structOffset - offset);
        assertThat(structOffset).isEqualTo(scalarStruct.m_2_utfString.absolutePosition());
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.position(byteBuffer.position() + scalarStruct.m_2_utfString.length());
        structOffset += scalarStruct.m_2_utfString.bitLength() / 8;

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
    public void test_struct_that_of_the_member_positions_are_in_sync_with_byte_buffer_with_offset() {
        test_struct_that_of_the_member_positions_are_in_sync_with_byte_buffer(123);
    }

    @Test
    public void test_struct_that_utfString_25_chars_are_correct_read_big_endian() {
        // Given
        final String UTF_STRING = "Hallo12345678900987654321";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_utf_string_values_are_correct_read(scalarStruct, byteBuffer, UTF_STRING, 0);
    }

    @Test
    public void test_struct_that_utfString_25_chars_are_correct_read_big_endian_with_offset() {
        // Given
        final String UTF_STRING = "Hallo21";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_utf_string_values_are_correct_read(scalarStruct, byteBuffer, UTF_STRING, 123);
    }

    @Test
    public void test_struct_that_utfString_25_chars_are_correct_read_little_endian() {
        // Given
        final String UTF_STRING = "Hallo1234987654321";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_utf_string_values_are_correct_read(scalarStruct, byteBuffer, UTF_STRING, 0);
    }

    @Test
    public void test_struct_that_utfString_5_chars_are_correct_read_big_endian() {
        // Given
        final String UTF_STRING = "Hallo";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_utf_string_values_are_correct_read(scalarStruct, byteBuffer, UTF_STRING, 0);
    }

    @Test
    public void test_struct_that_utfString_5_chars_are_correct_read_little_endian() {
        // Given
        final String UTF_STRING = "Hallo";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_utf_string_values_are_correct_read(scalarStruct, byteBuffer, UTF_STRING, 0);
    }

    @Test
    public void test_struct_that_utfString_5_chars_are_correct_written_big_endian() {
        // Given
        final String UTF_STRING = "Hallo";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_utf_string_values_are_correct_written(scalarStruct, byteBuffer, UTF_STRING, 0);
    }

    @Test
    public void test_struct_that_utfString_5_chars_are_correct_written_big_endian_with_offset() {
        // Given
        final String UTF_STRING = "Hallo";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_utf_string_values_are_correct_written(scalarStruct, byteBuffer, UTF_STRING, 230);
    }

    @Test
    public void test_struct_that_utfString_5_chars_are_correct_written_little_endian() {
        // Given
        final String UTF_STRING = "Hallo";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_utf_string_values_are_correct_written(scalarStruct, byteBuffer, UTF_STRING, 0);
    }

    private void test_struct_that_utf_string_values_are_correct_written(MyAbstractBitFieldStruct scalarStruct, ByteBuffer byteBuffer, String utfString, int structOffset) {
        // Given
        byte signed8 = 123 + Byte.MIN_VALUE;
        long signed64 = 1234L + Long.MIN_VALUE;

        byteBuffer.position(structOffset);

        // When
        scalarStruct.initByteBuffer(byteBuffer, structOffset);
        scalarStruct.m_1_signed08.set(signed8);
        scalarStruct.m_2_utfString.set(utfString);
        scalarStruct.m_3_signed64.set(signed64);

        // Then
        assertThat(scalarStruct.byteOrder()).isEqualTo(byteBuffer.order());
        assertThat(scalarStruct.size()).isEqualTo(1 + scalarStruct.m_2_utfString.length() + 8);
        assertThat(scalarStruct.getAbsolutePosition()).isEqualTo(structOffset);

        assertThat(byteBuffer.get()).isEqualTo(signed8);
        byte[] byteChar = new byte[scalarStruct.m_2_utfString.length()];
        byteBuffer.get(byteChar);
        assertThat(new String(byteChar,0,utfString.length())).isEqualTo(utfString);
        assertThat(byteBuffer.getLong()).isEqualTo(signed64);
    }

    private void test_struct_that_utf_string_values_are_correct_read(MyAbstractBitFieldStruct scalarStruct, ByteBuffer byteBuffer, String utfString, int structOffset) {
        // Given
        byte signed8 = 123 + Byte.MIN_VALUE;
        long signed64 = 1234L + Long.MIN_VALUE;

        byteBuffer.position(structOffset);
        byteBuffer.put(signed8);
        for(int i=0; utfString.length() > i; ++i) {
            byteBuffer.put((byte)utfString.charAt(i));
        }
        for(int i=utfString.length(); scalarStruct.m_2_utfString.length() > i; ++i) {
            byteBuffer.put((byte)0);
        }
        byteBuffer.putLong(signed64);

        // When
        scalarStruct.initByteBuffer(byteBuffer, structOffset);

        // Then
        assertThat(scalarStruct.byteOrder()).isEqualTo(byteBuffer.order());
        assertThat(scalarStruct.size()).isEqualTo(1 + scalarStruct.m_2_utfString.length() + 8);
        assertThat(scalarStruct.getAbsolutePosition()).isEqualTo(structOffset);

        assertThat(scalarStruct.m_1_signed08.get()).isEqualTo(signed8);
        assertThat(scalarStruct.m_2_utfString.get().toString()).isEqualTo(utfString);
        assertThat(scalarStruct.m_3_signed64.get()).isEqualTo(signed64);
    }

    static class MyAbstractBitFieldStruct extends Struct {

        final Signed8 m_1_signed08 = new Signed8();
        final Utf8String m_2_utfString = new Utf8String(27);
        final Signed64 m_3_signed64 = new Signed64();

        MyAbstractBitFieldStruct(final int nbrOfChars) {
            super();
        }

        MyAbstractBitFieldStruct(final ByteOrder byteOrder, final int nbrOfChars) {
            super(byteOrder);
        }
    }


    static class MyBitFieldStruct extends MyAbstractBitFieldStruct {


        public MyBitFieldStruct(final int nbrOfChars) {
            super(nbrOfChars);
        }

    }

    static class MyBitFieldStructBE extends MyAbstractBitFieldStruct {

        public MyBitFieldStructBE(final int nbrOfChars) {
            super(ByteOrder.BIG_ENDIAN, nbrOfChars);
        }


    }

    static class MyBitFieldStructLE extends MyAbstractBitFieldStruct {

        public MyBitFieldStructLE(final int nbrOfChars) {
            super(ByteOrder.LITTLE_ENDIAN, nbrOfChars);
        }


    }


}
