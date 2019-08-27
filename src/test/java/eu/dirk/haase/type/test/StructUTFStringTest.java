package eu.dirk.haase.type.test;

import eu.dirk.haase.type.Struct;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@RunWith(BlockJUnit4ClassRunner.class)
public class StructUTFStringTest {

    private void test_struct_that_bitField_values_are_correct_read(MyAbstractBitFieldStruct scalarStruct, ByteBuffer byteBuffer, String utfString, int structOffset) {
        // Given
        byte signed8 = 123 | Byte.MIN_VALUE;
        long signed64 = 1234L | Long.MIN_VALUE;

        byteBuffer.position(0);

        byteBuffer.put(signed8);
        byteBuffer.put(utfString.getBytes());
        byteBuffer.put((byte) 0);
        byteBuffer.putLong(signed64);

        // When
        scalarStruct.initByteBuffer(byteBuffer, structOffset);
        byteBuffer.position(0);

        scalarStruct.m_1_signed08.set(signed8);
        scalarStruct.m_2_utfString.set(utfString);
        scalarStruct.m_3_signed64.set(signed64);

        // Then
        Assertions.assertThat(scalarStruct.byteOrder()).isEqualTo(byteBuffer.order());
        Assertions.assertThat(scalarStruct.m_1_signed08.get()).isEqualTo(signed8);
        Assertions.assertThat(scalarStruct.m_2_utfString.get()).isEqualTo(utfString);
        Assertions.assertThat(scalarStruct.m_3_signed64.get()).isEqualTo(signed64);
    }

    @Test
    public void test_struct_that_utfString_25_chars_are_correct_read_big_endian_with_offset() {
        // Given
        final String UTF_STRING = "Hallo12345678900987654321";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, UTF_STRING, 123);
    }

    @Test
    public void test_struct_that_utfString_25_chars_are_correct_read_big_endian() {
        // Given
        final String UTF_STRING = "Hallo12345678900987654321";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, UTF_STRING, 0);
    }

    @Test
    public void test_struct_that_utfString_25_chars_are_correct_read_little_endian() {
        // Given
        final String UTF_STRING = "Hallo12345678900987654321";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, UTF_STRING, 0);
    }

    @Test
    public void test_struct_that_utfString_5_chars_are_correct_read_big_endian() {
        // Given
        final String UTF_STRING = "Hallo";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructBE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, UTF_STRING, 0);
    }

    @Test
    public void test_struct_that_utfString_5_chars_are_correct_read_little_endian() {
        // Given
        final String UTF_STRING = "Hallo";
        MyAbstractBitFieldStruct scalarStruct = new MyBitFieldStructLE(UTF_STRING.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(scalarStruct.byteOrder());
        // test
        test_struct_that_bitField_values_are_correct_read(scalarStruct, byteBuffer, UTF_STRING, 0);
    }

    static class MyAbstractBitFieldStruct extends Struct {
        final Signed8 m_1_signed08 = new Signed8();
        final UTF8String m_2_utfString;
        final Signed64 m_3_signed64 = new Signed64();

        MyAbstractBitFieldStruct(final int nbrOfChars) {
            super();
            m_2_utfString = new UTF8String(nbrOfChars);
        }

        MyAbstractBitFieldStruct(final ByteOrder byteOrder, final int nbrOfChars) {
            super(byteOrder);
            m_2_utfString = new UTF8String(nbrOfChars);
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
