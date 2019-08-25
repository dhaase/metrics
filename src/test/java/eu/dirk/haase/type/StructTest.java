package eu.dirk.haase.type;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

@RunWith(BlockJUnit4ClassRunner.class)
public class StructTest {


    @Test
    public void test_packet_struct_that_member_values_are_correct_read() {
        // Given
        MyScalarPackedStruct scalarStruct = new MyScalarPackedStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());

        byte bool8 = 1;
        short bool16 = 0;
        int bool32 = 1;
        long bool64 = 0;
        float float32 = Float.MAX_VALUE - 123f;
        double float64 = Double.MAX_VALUE - 123d;
        byte signed8 = 123 | Byte.MIN_VALUE;
        short signed16 = 1234 | Short.MIN_VALUE;
        int signed32 = 1234 | Integer.MIN_VALUE;
        long signed64 = 1234L | Long.MIN_VALUE;
        short unsigned8 = Byte.MAX_VALUE - 1;
        int unsigned16 = Short.MAX_VALUE - 1;
        long unsigned32 = Integer.MAX_VALUE - 1;
        byte utfChar8 = 'A';
        char utfChar16 = 'A';

        byteBuffer.put(bool8);
        byteBuffer.putShort(bool16);
        byteBuffer.putInt(bool32);
        byteBuffer.putLong(bool64);
        byteBuffer.put((byte)MyEnum.eins.ordinal());
        byteBuffer.putShort((short)MyEnum.zwei.ordinal());
        byteBuffer.putInt(MyEnum.drei.ordinal());
        byteBuffer.putLong(MyEnum.zwei.ordinal());
        byteBuffer.putFloat(float32);
        byteBuffer.putDouble(float64);
        byteBuffer.put(signed8);
        byteBuffer.putShort(signed16);
        byteBuffer.putInt(signed32);
        byteBuffer.putLong(signed64);
        byteBuffer.put((byte)unsigned8);
        byteBuffer.putShort((short)unsigned16);
        byteBuffer.putInt((int)unsigned32);
        byteBuffer.put(utfChar8);
        byteBuffer.putChar(utfChar16);

        // When
        scalarStruct.setByteBuffer(byteBuffer, 0);
        scalarStruct.bool8.set(bool8);
        scalarStruct.bool16.set(bool16);
        scalarStruct.bool32.set(bool32);
        scalarStruct.bool64.set(bool64);
        scalarStruct.enum8.set(MyEnum.eins);
        scalarStruct.enum16.set(MyEnum.zwei);
        scalarStruct.enum32.set(MyEnum.drei);
        scalarStruct.enum64.set(MyEnum.zwei);
        scalarStruct.float32.set(float32);
        scalarStruct.float64.set(float64);
        scalarStruct.signed8.set(signed8);
        scalarStruct.signed16.set(signed16);
        scalarStruct.signed32.set(signed32);
        scalarStruct.signed64.set(signed64);
        scalarStruct.unsigned8.set(unsigned8);
        scalarStruct.unsigned16.set(unsigned16);
        scalarStruct.unsigned32.set(unsigned32);
        scalarStruct.utfChar8.set((char)utfChar8);
        scalarStruct.utfChar16.set(utfChar16);

        // Then
        assertThat(scalarStruct.bool8.get()).isEqualTo(bool8 != 0);
        assertThat(scalarStruct.bool16.get()).isEqualTo(bool16 != 0);
        assertThat(scalarStruct.bool32.get()).isEqualTo(bool32 != 0);
        assertThat(scalarStruct.bool64.get()).isEqualTo(bool64 != 0);
        assertThat(scalarStruct.enum8.get()).isEqualTo(MyEnum.eins);
        assertThat(scalarStruct.enum16.get()).isEqualTo(MyEnum.zwei);
        assertThat(scalarStruct.enum32.get()).isEqualTo(MyEnum.drei);
        assertThat(scalarStruct.enum64.get()).isEqualTo(MyEnum.zwei);
        assertThat(scalarStruct.float32.get()).isEqualTo(float32);
        assertThat(scalarStruct.float64.get()).isEqualTo(float64);
        assertThat(scalarStruct.signed8.get()).isEqualTo(signed8);
        assertThat(scalarStruct.signed16.get()).isEqualTo(signed16);
        assertThat(scalarStruct.signed32.get()).isEqualTo(signed32);
        assertThat(scalarStruct.signed64.get()).isEqualTo(signed64);
        assertThat(scalarStruct.unsigned8.get()).isEqualTo(unsigned8);
        assertThat(scalarStruct.unsigned16.get()).isEqualTo(unsigned16);
        assertThat(scalarStruct.unsigned32.get()).isEqualTo(unsigned32);
        assertThat(scalarStruct.utfChar8.get()).isEqualTo((char)utfChar8);
        assertThat(scalarStruct.utfChar16.get()).isEqualTo(utfChar16);
    }


    @Test
    public void test_packet_struct_that_member_values_are_correct_written() {
        // Given
        MyScalarPackedStruct scalarStruct = new MyScalarPackedStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());

        byte bool8 = 1;
        short bool16 = 0;
        int bool32 = 1;
        long bool64 = 0;
        float float32 = Float.MAX_VALUE - 123f;
        double float64 = Double.MAX_VALUE - 123d;
        byte signed8 = 123 | Byte.MIN_VALUE;
        short signed16 = 1234 | Short.MIN_VALUE;
        int signed32 = 1234 | Integer.MIN_VALUE;
        long signed64 = 1234L | Long.MIN_VALUE;
        short unsigned8 = Byte.MAX_VALUE - 1;
        int unsigned16 = Short.MAX_VALUE - 1;
        long unsigned32 = Integer.MAX_VALUE - 1;
        byte utfChar8 = 'A';
        char utfChar16 = 'A';

        // When
        scalarStruct.setByteBuffer(byteBuffer, 0);
        scalarStruct.bool8.set(bool8);
        scalarStruct.bool16.set(bool16);
        scalarStruct.bool32.set(bool32);
        scalarStruct.bool64.set(bool64);
        scalarStruct.enum8.set(MyEnum.eins);
        scalarStruct.enum16.set(MyEnum.zwei);
        scalarStruct.enum32.set(MyEnum.drei);
        scalarStruct.enum64.set(MyEnum.zwei);
        scalarStruct.float32.set(float32);
        scalarStruct.float64.set(float64);
        scalarStruct.signed8.set(signed8);
        scalarStruct.signed16.set(signed16);
        scalarStruct.signed32.set(signed32);
        scalarStruct.signed64.set(signed64);
        scalarStruct.unsigned8.set(unsigned8);
        scalarStruct.unsigned16.set(unsigned16);
        scalarStruct.unsigned32.set(unsigned32);
        scalarStruct.utfChar8.set((char)utfChar8);
        scalarStruct.utfChar16.set(utfChar16);

        // Then
        assertThat(byteBuffer.get()).isEqualTo(bool8);
        assertThat(byteBuffer.getShort()).isEqualTo(bool16);
        assertThat(byteBuffer.getInt()).isEqualTo(bool32);
        assertThat(byteBuffer.getLong()).isEqualTo(bool64);
        assertThat(byteBuffer.get()).isEqualTo((byte)MyEnum.eins.ordinal());
        assertThat(byteBuffer.getShort()).isEqualTo((short)MyEnum.zwei.ordinal());
        assertThat(byteBuffer.getInt()).isEqualTo(MyEnum.drei.ordinal());
        assertThat(byteBuffer.getLong()).isEqualTo(MyEnum.zwei.ordinal());
        assertThat(byteBuffer.getFloat()).isEqualTo(float32);
        assertThat(byteBuffer.getDouble()).isEqualTo(float64);
        assertThat(byteBuffer.get()).isEqualTo(signed8);
        assertThat(byteBuffer.getShort()).isEqualTo(signed16);
        assertThat(byteBuffer.getInt()).isEqualTo(signed32);
        assertThat(byteBuffer.getLong()).isEqualTo(signed64);
        assertThat(byteBuffer.get()).isEqualTo((byte)unsigned8);
        assertThat(byteBuffer.getShort()).isEqualTo((short)unsigned16);
        assertThat(byteBuffer.getInt()).isEqualTo((int)unsigned32);
        assertThat(byteBuffer.get()).isEqualTo(utfChar8);
        assertThat(byteBuffer.getChar()).isEqualTo(utfChar16);
    }


    @Test
    public void test_packet_struct_that_member_values_are_correct_with_roundtrip() {
        // Given
        MyScalarPackedStruct scalarStruct = new MyScalarPackedStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());

        boolean bool8 = true;
        boolean bool16 = false;
        boolean bool32 = true;
        boolean bool64 = false;
        float float32 = Float.MAX_VALUE - 123f;
        double float64 = Double.MAX_VALUE - 123d;
        byte signed8 = 123 | Byte.MIN_VALUE;
        short signed16 = 1234 | Short.MIN_VALUE;
        int signed32 = 1234 | Integer.MIN_VALUE;
        long signed64 = 1234L | Long.MIN_VALUE;
        short unsigned8 = Byte.MAX_VALUE - 1;
        int unsigned16 = Short.MAX_VALUE - 1;
        long unsigned32 = Integer.MAX_VALUE - 1;
        byte utfChar8 = 'A';
        char utfChar16 = 'A';

        // When
        scalarStruct.setByteBuffer(byteBuffer, 0);
        scalarStruct.bool8.set(bool8);
        scalarStruct.bool16.set(bool16);
        scalarStruct.bool32.set(bool32);
        scalarStruct.bool64.set(bool64);
        scalarStruct.enum8.set(MyEnum.eins);
        scalarStruct.enum16.set(MyEnum.zwei);
        scalarStruct.enum32.set(MyEnum.drei);
        scalarStruct.enum64.set(MyEnum.zwei);
        scalarStruct.float32.set(float32);
        scalarStruct.float64.set(float64);
        scalarStruct.signed8.set(signed8);
        scalarStruct.signed16.set(signed16);
        scalarStruct.signed32.set(signed32);
        scalarStruct.signed64.set(signed64);
        scalarStruct.unsigned8.set(unsigned8);
        scalarStruct.unsigned16.set(unsigned16);
        scalarStruct.unsigned32.set(unsigned32);
        scalarStruct.utfChar8.set((char)utfChar8);
        scalarStruct.utfChar16.set(utfChar16);

        // Then
        assertThat(scalarStruct.bool8.get()).isEqualTo(bool8);
        assertThat(scalarStruct.bool16.get()).isEqualTo(bool16);
        assertThat(scalarStruct.bool32.get()).isEqualTo(bool32);
        assertThat(scalarStruct.bool64.get()).isEqualTo(bool64);
        assertThat(scalarStruct.enum8.get()).isEqualTo(MyEnum.eins);
        assertThat(scalarStruct.enum16.get()).isEqualTo(MyEnum.zwei);
        assertThat(scalarStruct.enum32.get()).isEqualTo(MyEnum.drei);
        assertThat(scalarStruct.enum64.get()).isEqualTo(MyEnum.zwei);
        assertThat(scalarStruct.float32.get()).isEqualTo(float32);
        assertThat(scalarStruct.float64.get()).isEqualTo(float64);
        assertThat(scalarStruct.signed8.get()).isEqualTo(signed8);
        assertThat(scalarStruct.signed16.get()).isEqualTo(signed16);
        assertThat(scalarStruct.signed32.get()).isEqualTo(signed32);
        assertThat(scalarStruct.signed64.get()).isEqualTo(signed64);
        assertThat(scalarStruct.unsigned8.get()).isEqualTo(unsigned8);
        assertThat(scalarStruct.unsigned16.get()).isEqualTo(unsigned16);
        assertThat(scalarStruct.unsigned32.get()).isEqualTo(unsigned32);
        assertThat(scalarStruct.utfChar8.get()).isEqualTo((char)utfChar8);
        assertThat(scalarStruct.utfChar16.get()).isEqualTo(utfChar16);
    }


    @Test
    public void test_packet_struct_that_of_the_member_positions_are_in_sync_with_byte_buffer() {
        // Given
        MyScalarPackedStruct scalarStruct = new MyScalarPackedStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // When
        // Then
        int structOffset = 0;
        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.bool8.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.bool8.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.bool16.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 2 Byte weiter
        byteBuffer.getShort();
        structOffset += scalarStruct.bool16.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.bool32.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 4 Byte weiter
        byteBuffer.getInt();
        structOffset += scalarStruct.bool32.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.bool64.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 8 Byte weiter
        byteBuffer.getLong();
        structOffset += scalarStruct.bool64.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.enum8.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.enum8.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.enum16.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 2 Byte weiter
        byteBuffer.getShort();
        structOffset += scalarStruct.enum16.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.enum32.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 4 Byte weiter
        byteBuffer.getInt();
        structOffset += scalarStruct.enum32.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.enum64.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 8 Byte weiter
        byteBuffer.getLong();
        structOffset += scalarStruct.enum64.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.float32.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 4 Byte weiter
        byteBuffer.getInt();
        structOffset += scalarStruct.float32.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.float64.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 8 Byte weiter
        byteBuffer.getLong();
        structOffset += scalarStruct.float64.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.signed8.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.signed8.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.signed16.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 2 Byte weiter
        byteBuffer.getShort();
        structOffset += scalarStruct.signed16.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.signed32.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 4 Byte weiter
        byteBuffer.getInt();
        structOffset += scalarStruct.signed32.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.signed64.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 8 Byte weiter
        byteBuffer.getLong();
        structOffset += scalarStruct.signed64.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.unsigned8.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.unsigned8.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.unsigned16.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 2 Byte weiter
        byteBuffer.getShort();
        structOffset += scalarStruct.unsigned16.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.unsigned32.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 4 Byte weiter
        byteBuffer.getInt();
        structOffset += scalarStruct.unsigned32.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.utfChar8.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.utfChar8.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.utfChar16.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.getShort();
        structOffset += scalarStruct.utfChar16.bitLength() / 8;

        assertThat(scalarStruct.size()).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.size()).isEqualTo(structOffset);
    }


    static class MyAbstractScalarStruct extends Struct {
        final Bool8 bool8 = new Bool8();
        final Bool16 bool16 = new Bool16();
        final Bool32 bool32 = new Bool32();
        final Bool64 bool64 = new Bool64();
        final Enum8 enum8 = new Enum8(MyEnum.class.getEnumConstants());
        final Enum16 enum16 = new Enum16(MyEnum.class.getEnumConstants());
        final Enum32 enum32 = new Enum32(MyEnum.class.getEnumConstants());
        final Enum64 enum64 = new Enum64(MyEnum.class.getEnumConstants());
        final Float32 float32 = new Float32();
        final Float64 float64 = new Float64();
        final Signed8 signed8 = new Signed8();
        final Signed16 signed16 = new Signed16();
        final Signed32 signed32 = new Signed32();
        final Signed64 signed64 = new Signed64();
        final Unsigned8 unsigned8 = new Unsigned8();
        final Unsigned16 unsigned16 = new Unsigned16();
        final Unsigned32 unsigned32 = new Unsigned32();
        final UTFChar8 utfChar8 = new UTFChar8();
        final UTFChar16 utfChar16 = new UTFChar16();

    }


    static class MyScalarPackedStruct extends MyAbstractScalarStruct {

        public boolean isPacked() {
            return true;
        }

    }

    enum MyEnum {
        eins, zwei, drei;
    }
}
