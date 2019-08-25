package eu.dirk.haase.type;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class StructTest {


    private void test_packet_struct_that_member_values_are_correct_read(MyAbstractScalarStruct scalarStruct, ByteBuffer byteBuffer, int position, int structOffset) {
        // Given
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

        byteBuffer.position(position + structOffset);

        byteBuffer.put(bool8);
        byteBuffer.putShort(bool16);
        byteBuffer.putInt(bool32);
        byteBuffer.putLong(bool64);
        byteBuffer.put((byte) MyEnum.eins.ordinal());
        byteBuffer.putShort((short) MyEnum.zwei.ordinal());
        byteBuffer.putInt(MyEnum.drei.ordinal());
        byteBuffer.putLong(MyEnum.zwei.ordinal());
        byteBuffer.putFloat(float32);
        byteBuffer.putDouble(float64);
        byteBuffer.put(signed8);
        byteBuffer.putShort(signed16);
        byteBuffer.putInt(signed32);
        byteBuffer.putLong(signed64);
        byteBuffer.put((byte) unsigned8);
        byteBuffer.putShort((short) unsigned16);
        byteBuffer.putInt((int) unsigned32);
        byteBuffer.put(utfChar8);
        byteBuffer.putChar(utfChar16);

        // When
        scalarStruct.setByteBuffer(byteBuffer, structOffset);
        byteBuffer.position(position);

        scalarStruct.bool08.set(bool8);
        scalarStruct.bool16.set(bool16);
        scalarStruct.bool32.set(bool32);
        scalarStruct.bool64.set(bool64);
        scalarStruct.enum08.set(MyEnum.eins);
        scalarStruct.enum16.set(MyEnum.zwei);
        scalarStruct.enum32.set(MyEnum.drei);
        scalarStruct.enum64.set(MyEnum.zwei);
        scalarStruct.float32.set(float32);
        scalarStruct.float64.set(float64);
        scalarStruct.signed08.set(signed8);
        scalarStruct.signed16.set(signed16);
        scalarStruct.signed32.set(signed32);
        scalarStruct.signed64.set(signed64);
        scalarStruct.unsigned08.set(unsigned8);
        scalarStruct.unsigned16.set(unsigned16);
        scalarStruct.unsigned32.set(unsigned32);
        scalarStruct.utfChar08.set((char) utfChar8);
        scalarStruct.utfChar16.set(utfChar16);

        // Then
        assertThat(scalarStruct.byteOrder()).isEqualTo(byteBuffer.order());
        assertThat(scalarStruct.bool08.get()).isEqualTo(bool8 != 0);
        assertThat(scalarStruct.bool16.get()).isEqualTo(bool16 != 0);
        assertThat(scalarStruct.bool32.get()).isEqualTo(bool32 != 0);
        assertThat(scalarStruct.bool64.get()).isEqualTo(bool64 != 0);
        assertThat(scalarStruct.enum08.get()).isEqualTo(MyEnum.eins);
        assertThat(scalarStruct.enum16.get()).isEqualTo(MyEnum.zwei);
        assertThat(scalarStruct.enum32.get()).isEqualTo(MyEnum.drei);
        assertThat(scalarStruct.enum64.get()).isEqualTo(MyEnum.zwei);
        assertThat(scalarStruct.float32.get()).isEqualTo(float32);
        assertThat(scalarStruct.float64.get()).isEqualTo(float64);
        assertThat(scalarStruct.signed08.get()).isEqualTo(signed8);
        assertThat(scalarStruct.signed16.get()).isEqualTo(signed16);
        assertThat(scalarStruct.signed32.get()).isEqualTo(signed32);
        assertThat(scalarStruct.signed64.get()).isEqualTo(signed64);
        assertThat(scalarStruct.unsigned08.get()).isEqualTo(unsigned8);
        assertThat(scalarStruct.unsigned16.get()).isEqualTo(unsigned16);
        assertThat(scalarStruct.unsigned32.get()).isEqualTo(unsigned32);
        assertThat(scalarStruct.utfChar08.get()).isEqualTo((char) utfChar8);
        assertThat(scalarStruct.utfChar16.get()).isEqualTo(utfChar16);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_read_big_endian() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // test
        test_packet_struct_that_member_values_are_correct_read(scalarStruct, byteBuffer, 0, 0);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_read_big_endian_on_position() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // test
        test_packet_struct_that_member_values_are_correct_read(scalarStruct, byteBuffer, 23, 0);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_read_big_endian_on_position_and_offset() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // test
        test_packet_struct_that_member_values_are_correct_read(scalarStruct, byteBuffer, 23, 42);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_read_little_endian() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        // test
        test_packet_struct_that_member_values_are_correct_read(scalarStruct, byteBuffer, 0, 0);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_read_native_order() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // test
        test_packet_struct_that_member_values_are_correct_read(scalarStruct, byteBuffer, 0, 0);
    }

    private void test_packet_struct_that_member_values_are_correct_with_roundtrip(MyAbstractScalarStruct scalarStruct, ByteBuffer byteBuffer, int position, int structOffset) {
        // Given
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

        final byte prefix = 12;
        byteBuffer.position(0);
        for (int i = 0; position > i; ++i) {
            byteBuffer.put(prefix);
        }
        assertThat(byteBuffer.position()).isEqualTo(position);

        // When
        scalarStruct.setByteBuffer(byteBuffer, structOffset);

        scalarStruct.bool08.set(bool8);
        scalarStruct.bool16.set(bool16);
        scalarStruct.bool32.set(bool32);
        scalarStruct.bool64.set(bool64);
        scalarStruct.enum08.set(MyEnum.eins);
        scalarStruct.enum16.set(MyEnum.zwei);
        scalarStruct.enum32.set(MyEnum.drei);
        scalarStruct.enum64.set(MyEnum.zwei);
        scalarStruct.float32.set(float32);
        scalarStruct.float64.set(float64);
        scalarStruct.signed08.set(signed8);
        scalarStruct.signed16.set(signed16);
        scalarStruct.signed32.set(signed32);
        scalarStruct.signed64.set(signed64);
        scalarStruct.unsigned08.set(unsigned8);
        scalarStruct.unsigned16.set(unsigned16);
        scalarStruct.unsigned32.set(unsigned32);
        scalarStruct.utfChar08.set((char) utfChar8);
        scalarStruct.utfChar16.set(utfChar16);

        // Then

        byteBuffer.position(0);
        for (int i = 0; position > i; ++i) {
            assertThat(byteBuffer.get()).isEqualTo(prefix);
        }
        assertThat(byteBuffer.position()).isEqualTo(position);
        byteBuffer.position(position);

        assertThat(scalarStruct.byteOrder()).isEqualTo(byteBuffer.order());
        assertThat(scalarStruct.bool08.get()).isEqualTo(bool8);
        assertThat(scalarStruct.bool16.get()).isEqualTo(bool16);
        assertThat(scalarStruct.bool32.get()).isEqualTo(bool32);
        assertThat(scalarStruct.bool64.get()).isEqualTo(bool64);
        assertThat(scalarStruct.enum08.get()).isEqualTo(MyEnum.eins);
        assertThat(scalarStruct.enum16.get()).isEqualTo(MyEnum.zwei);
        assertThat(scalarStruct.enum32.get()).isEqualTo(MyEnum.drei);
        assertThat(scalarStruct.enum64.get()).isEqualTo(MyEnum.zwei);
        assertThat(scalarStruct.float32.get()).isEqualTo(float32);
        assertThat(scalarStruct.float64.get()).isEqualTo(float64);
        assertThat(scalarStruct.signed08.get()).isEqualTo(signed8);
        assertThat(scalarStruct.signed16.get()).isEqualTo(signed16);
        assertThat(scalarStruct.signed32.get()).isEqualTo(signed32);
        assertThat(scalarStruct.signed64.get()).isEqualTo(signed64);
        assertThat(scalarStruct.unsigned08.get()).isEqualTo(unsigned8);
        assertThat(scalarStruct.unsigned16.get()).isEqualTo(unsigned16);
        assertThat(scalarStruct.unsigned32.get()).isEqualTo(unsigned32);
        assertThat(scalarStruct.utfChar08.get()).isEqualTo((char) utfChar8);
        assertThat(scalarStruct.utfChar16.get()).isEqualTo(utfChar16);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_with_roundtrip_big_endian_on_position_and_offset() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // test
        test_packet_struct_that_member_values_are_correct_with_roundtrip(scalarStruct, byteBuffer, 54, 76);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_with_roundtrip_big_endian_on_position() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // test
        test_packet_struct_that_member_values_are_correct_with_roundtrip(scalarStruct, byteBuffer, 54, 0);
    }


    @Test
    public void test_packet_struct_that_member_values_are_correct_with_roundtrip_big_endian() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // test
        test_packet_struct_that_member_values_are_correct_with_roundtrip(scalarStruct, byteBuffer, 0, 0);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_with_roundtrip_little_endian() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        // test
        test_packet_struct_that_member_values_are_correct_with_roundtrip(scalarStruct, byteBuffer, 0, 0);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_with_roundtrip_native_order() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // test
        test_packet_struct_that_member_values_are_correct_with_roundtrip(scalarStruct, byteBuffer, 0, 0);
    }

    private void test_packet_struct_that_member_values_are_correct_written(MyAbstractScalarStruct scalarStruct, ByteBuffer byteBuffer, int position, int structOffset) {
        // Given
        byte bool8 = 1;
        short bool16 = 0;
        int bool32 = 1;
        long bool64 = 0;
        byte enum8 = (byte) MyEnum.eins.ordinal();
        short enum16 = (short) MyEnum.zwei.ordinal();
        int enum32 = MyEnum.drei.ordinal();
        long enum64 = MyEnum.zwei.ordinal();
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

        final byte prefix = 12;
        byteBuffer.position(0);
        for (int i = 0; position > i; ++i) {
            byteBuffer.put(prefix);
        }
        assertThat(byteBuffer.position()).isEqualTo(position);

        // When
        scalarStruct.setByteBuffer(byteBuffer, structOffset);
        byteBuffer.position(position);

        scalarStruct.bool08.set(bool8);
        scalarStruct.bool16.set(bool16);
        scalarStruct.bool32.set(bool32);
        scalarStruct.bool64.set(bool64);
        scalarStruct.enum08.set(MyEnum.eins);
        scalarStruct.enum16.set(MyEnum.zwei);
        scalarStruct.enum32.set(MyEnum.drei);
        scalarStruct.enum64.set(MyEnum.zwei);
        scalarStruct.float32.set(float32);
        scalarStruct.float64.set(float64);
        scalarStruct.signed08.set(signed8);
        scalarStruct.signed16.set(signed16);
        scalarStruct.signed32.set(signed32);
        scalarStruct.signed64.set(signed64);
        scalarStruct.unsigned08.set(unsigned8);
        scalarStruct.unsigned16.set(unsigned16);
        scalarStruct.unsigned32.set(unsigned32);
        scalarStruct.utfChar08.set((char) utfChar8);
        scalarStruct.utfChar16.set(utfChar16);

        // Then

        byteBuffer.position(0);
        for (int i = 0; position > i; ++i) {
            assertThat(byteBuffer.get()).isEqualTo(prefix);
        }
        assertThat(byteBuffer.position()).isEqualTo(position);
        byteBuffer.position(position + structOffset);

        assertThat(scalarStruct.byteOrder()).isEqualTo(byteBuffer.order());
        assertThat(byteBuffer.get()).isEqualTo(bool8);
        assertThat(byteBuffer.getShort()).isEqualTo(bool16);
        assertThat(byteBuffer.getInt()).isEqualTo(bool32);
        assertThat(byteBuffer.getLong()).isEqualTo(bool64);
        assertThat(byteBuffer.get()).isEqualTo(enum8);
        assertThat(byteBuffer.getShort()).isEqualTo(enum16);
        assertThat(byteBuffer.getInt()).isEqualTo(enum32);
        assertThat(byteBuffer.getLong()).isEqualTo(enum64);
        assertThat(byteBuffer.getFloat()).isEqualTo(float32);
        assertThat(byteBuffer.getDouble()).isEqualTo(float64);
        assertThat(byteBuffer.get()).isEqualTo(signed8);
        assertThat(byteBuffer.getShort()).isEqualTo(signed16);
        assertThat(byteBuffer.getInt()).isEqualTo(signed32);
        assertThat(byteBuffer.getLong()).isEqualTo(signed64);
        assertThat(byteBuffer.get()).isEqualTo((byte) unsigned8);
        assertThat(byteBuffer.getShort()).isEqualTo((short) unsigned16);
        assertThat(byteBuffer.getInt()).isEqualTo((int) unsigned32);
        assertThat(byteBuffer.get()).isEqualTo(utfChar8);
        assertThat(byteBuffer.getChar()).isEqualTo(utfChar16);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_written_big_endian_on_position_and_offset() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // Test
        test_packet_struct_that_member_values_are_correct_written(scalarStruct, byteBuffer, 43, 23);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_written_big_endian_on_position() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // Test
        test_packet_struct_that_member_values_are_correct_written(scalarStruct, byteBuffer, 43, 0);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_written_big_endian() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructBE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // Test
        test_packet_struct_that_member_values_are_correct_written(scalarStruct, byteBuffer, 0, 0);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_written_little_endian() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStructLE();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        // Test
        test_packet_struct_that_member_values_are_correct_written(scalarStruct, byteBuffer, 0, 0);
    }

    @Test
    public void test_packet_struct_that_member_values_are_correct_written_native_order() {
        // Given
        MyAbstractScalarStruct scalarStruct = new MyScalarPackedStruct();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Test
        test_packet_struct_that_member_values_are_correct_written(scalarStruct, byteBuffer, 0, 0);
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
        assertThat(scalarStruct.bool08.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.bool08.bitLength() / 8;

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
        assertThat(scalarStruct.enum08.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.enum08.bitLength() / 8;

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
        assertThat(scalarStruct.signed08.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.signed08.bitLength() / 8;

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
        assertThat(scalarStruct.unsigned08.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.unsigned08.bitLength() / 8;

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
        assertThat(scalarStruct.utfChar08.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.get();
        structOffset += scalarStruct.utfChar08.bitLength() / 8;

        assertThat(structOffset).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.utfChar16.offset()).isEqualTo(structOffset);
        // bewege die Position jeweils um 1 Byte weiter
        byteBuffer.getShort();
        structOffset += scalarStruct.utfChar16.bitLength() / 8;

        assertThat(scalarStruct.size()).isEqualTo(byteBuffer.position());
        assertThat(scalarStruct.size()).isEqualTo(structOffset);
    }


    enum MyEnum {
        eins, zwei, drei;
    }

    static class MyAbstractScalarStruct extends Struct {
        final Bool8 bool08 = new Bool8();
        final Bool16 bool16 = new Bool16();
        final Bool32 bool32 = new Bool32();
        final Bool64 bool64 = new Bool64();
        final Enum8 enum08 = new Enum8(MyEnum.class.getEnumConstants());
        final Enum16 enum16 = new Enum16(MyEnum.class.getEnumConstants());
        final Enum32 enum32 = new Enum32(MyEnum.class.getEnumConstants());
        final Enum64 enum64 = new Enum64(MyEnum.class.getEnumConstants());
        final Float32 float32 = new Float32();
        final Float64 float64 = new Float64();
        final Signed8 signed08 = new Signed8();
        final Signed16 signed16 = new Signed16();
        final Signed32 signed32 = new Signed32();
        final Signed64 signed64 = new Signed64();
        final Unsigned8 unsigned08 = new Unsigned8();
        final Unsigned16 unsigned16 = new Unsigned16();
        final Unsigned32 unsigned32 = new Unsigned32();
        final UTFChar8 utfChar08 = new UTFChar8();
        final UTFChar16 utfChar16 = new UTFChar16();

        MyAbstractScalarStruct() {
            super();
        }

        MyAbstractScalarStruct(final ByteOrder byteOrder) {
            super(byteOrder);
        }

    }

    static class MyScalarPackedStruct extends MyAbstractScalarStruct {

        public boolean isPacked() {
            return true;
        }

    }

    static class MyScalarPackedStructBE extends MyAbstractScalarStruct {

        public MyScalarPackedStructBE() {
            super(ByteOrder.BIG_ENDIAN);
        }

        public boolean isPacked() {
            return true;
        }

    }

    static class MyScalarPackedStructLE extends MyAbstractScalarStruct {

        public MyScalarPackedStructLE() {
            super(ByteOrder.LITTLE_ENDIAN);
        }

        public boolean isPacked() {
            return true;
        }

    }

}
