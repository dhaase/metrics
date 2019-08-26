/*
 * Javolution - Java(TM) Solution for Real-Time and Embedded Systems
 * Copyright (C) 2012 - Javolution (http://javolution.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package eu.dirk.haase.type;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> Equivalent to a  <code>C/C++ struct</code>; this class confers
 * interoperability between Java classes and C/C++ struct.</p>
 * <p>
 * <p> Unlike <code>C/C++</code>, the storage layout of Java objects is not
 * determined by the compiler. The layout of objects in memory is deferred
 * to run time and determined by the interpreter (or just-in-time compiler).
 * This approach allows for dynamic loading and binding; but also makes
 * interfacing with <code>C/C++</code> code difficult. Hence, this class for
 * which the memory layout is defined by the initialization order of the
 * {@link Struct}'s {@link AbstractMember members} and follows the same wordSize
 * rules as <code>C/C++ structs</code>.</p>
 * <p>
 * <p> This class (as well as the {@link Union} sub-class) facilitates:</p>
 * <ul>
 * <li> Memory sharing between Java applications and native libraries.</li>
 * <li> Direct encoding/decoding of streams for which the structure
 * is defined by legacy C/C++ code.</li>
 * <li> Serialization/deserialization of Java objects (complete control,
 * e.g. no class header)</li>
 * <li> Mapping of Java objects to physical addresses (with JNI).</li>
 * </ul>
 * <p>
 * <p> Because of its one-to-one mapping, it is relatively easy to convert C
 * header files (e.g. OpenGL bindings) to Java {@link Struct}/{@link Union}
 * using simple text macros. Here is an example of C struct:</p>
 * [code]
 * enum Gender{MALE, FEMALE};
 * struct Date {
 * unsigned short year;
 * unsigned byte month;
 * unsigned byte day;
 * };
 * struct Student {
 * enum Gender gender;
 * char        name[64];
 * struct Date birth;
 * float       grades[10];
 * Student*    next;
 * };[/code]
 * <p> and here is the Java equivalent using this class:</p>
 * [code]
 * public enum Gender { MALE, FEMALE };
 * public static class Date extends Struct {
 * public final Unsigned16 year = new Unsigned16();
 * public final Unsigned8 month = new Unsigned8();
 * public final Unsigned8 day   = new Unsigned8();
 * }
 * public static class Student extends Struct {
 * public final Enum32<Gender>       gender = new Enum32<Gender>(Gender.values());
 * public final UTF8String           name   = new UTF8String(64);
 * public final Date                 birth  = inner(new Date());
 * public final Float32[]            grades = array(new Float32[10]);
 * public final Reference32<Student> next   =  new Reference32<Student>();
 * }[/code]
 * <p> Struct's members are directly accessible:
 * {@code
 * Student student = new Student();
 * student.gender.set(Gender.MALE);
 * student.name.set("John Doe"); // Null terminated (C compatible)
 * int age = 2003 - student.birth.year.get();
 * student.grades[2].set(12.5f);
 * student = student.next.get();}</p>
 * <p>
 * <p> Applications can work with the raw {@link #currByteBuffer bytes}
 * directly. The following illustrate how {@link Struct} can be used to
 * decode/encode UDP messages directly:
 * {@code
 * class UDPMessage extends Struct {
 * Unsigned16 xxx = new Unsigned16();
 * ...
 * }
 * public void run() {
 * byte[] bytes = new byte[1024];
 * DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
 * UDPMessage message = new UDPMessage();
 * message.initByteBuffer(ByteBuffer.wrap(bytes), 0);
 * // packet and message are now two different views of the same data.
 * while (isListening) {
 * multicastSocket.receive(packet);
 * int xxx = message.xxx.get();
 * ... // Process message fields directly.
 * }
 * }}</p>
 * <p>
 * <p> It is relatively easy to map instances of this class to any physical
 * address using
 * <a href="http://java.sun.com/docs/books/tutorial/native1.1/index.html">
 * JNI</a>. Here is an example:
 * {@code
 * import java.nio.ByteBuffer;
 * class Clock extends Struct { // Hardware clock mapped to memory.
 * Unsigned16 seconds  = new Unsigned16(5); // unsigned short seconds:5
 * Unsigned16 minutes  = new Unsigned16(5); // unsigned short minutes:5
 * Unsigned16 hours    = new Unsigned16(4); // unsigned short hours:4
 * Clock() {
 * initByteBuffer(Clock.nativeBuffer(), 0);
 * }
 * private static native ByteBuffer nativeBuffer();
 * }}</p>
 * <p> Below is the <code>nativeBuffer()</code> implementation
 * (<code>Clock.c</code>):
 * {@code
 * #include <jni.h>
 * #include "Clock.h" // Generated using javah
 * JNIEXPORT jobject JNICALL Java_Clock_nativeBuffer (JNIEnv *env, jclass) {
 * return (*env)->NewDirectByteBuffer(env, clock_address, buffer_size)
 * }}</p>
 * <p>
 * <p> Bit-fields are supported (see <code>Clock</code> example above).
 * Bit-fields allocation order is defined by the Struct {@link #structByteOrder}
 * return value. Leftmost bit to rightmost bit if
 * <code>BIG_ENDIAN</code> and rightmost bit to leftmost bit if
 * <code>LITTLE_ENDIAN</code> (same layout as Microsoft Visual C++).
 * C/C++ Bit-fields cannot straddle the storage-unit boundary as defined
 * by their base type (padding is inserted at the end of the first bit-field
 * and the second bit-field is put into the next storage unit).
 * It is possible to avoid bit padding by using the {@link BitField}
 * member (or a sub-class). In which case the allocation order is always
 * from the leftmost to the rightmost bit (same as <code>BIG_ENDIAN</code>).
 * </p>
 * <p>
 * <p> Finally, it is possible to change the {@link #initByteBuffer ByteBuffer}
 * and/or the Struct {@link #initByteBuffer(ByteBuffer, int)} in its
 * <code>ByteBuffer</code> to allow for a single {@link Struct} object to
 * encode/decode multiple memory mapped instances.</p>
 * <p>
 * <p><i>Note: Because Struct/Union are basically wrappers around
 * <code>java.nio.ByteBuffer</code>, tutorials/usages for the
 * Java NIO package are directly applicable to Struct/Union.</i></p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.5.1, April 1, 2010
 */
@SuppressWarnings("unchecked")
public abstract class Struct implements PositionUpdatable {

    private static final Class<? extends Bool8[]> ARRAY_BOOL_08 = new Bool8[0].getClass();
    private static final Class<? extends Bool16[]> ARRAY_BOOL_16 = new Bool16[0].getClass();
    private static final Class<? extends Bool32[]> ARRAY_BOOL_32 = new Bool32[0].getClass();
    private static final Class<? extends Bool64[]> ARRAY_BOOL_64 = new Bool64[0].getClass();
    private static final Class<? extends Float32[]> ARRAY_FLOAT_32 = new Float32[0].getClass();
    private static final Class<? extends Float64[]> ARRAY_FLOAT_64 = new Float64[0].getClass();
    private static final Class<? extends Signed8[]> ARRAY_SIGNED_08 = new Signed8[0].getClass();
    private static final Class<? extends Signed16[]> ARRAY_SIGNED_16 = new Signed16[0].getClass();
    private static final Class<? extends Signed32[]> ARRAY_SIGNED_32 = new Signed32[0].getClass();
    private static final Class<? extends Signed64[]> ARRAY_SIGNED_64 = new Signed64[0].getClass();
    private static final Class<? extends Unsigned8[]> ARRAY_UNSIGNED_08 = new Unsigned8[0].getClass();
    private static final Class<? extends Unsigned16[]> ARRAY_UNSIGNED_16 = new Unsigned16[0].getClass();
    private static final Class<? extends Unsigned32[]> ARRAY_UNSIGNED_32 = new Unsigned32[0].getClass();
    //
    //
    private static final char[] HEX_CHAR = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    final ByteOrder structByteOrder;
    private final List<PositionUpdatable> memberList;
    /**
     * Holds the bits used in the word during construction (for bit fields).
     * This is the number of bits used in the last word.
     */
    int currBitsUsed;
    /**
     * Holds the byte buffer backing the struct (top struct).
     */
    ByteBuffer currByteBuffer;
    /**
     * Holds the word size during construction (for bit fields).
     * This is the size of the last word used.
     */
    int currWordSize;
    int innerStructAbsolutePosition;
    /**
     * Holds the outer struct if any.
     */
    Struct outerStruct;
    /**
     * Holds the index position during construction.
     * This is the index a the first unused byte available.
     */
    int structIndex;
    /**
     * Holds this struct's length.
     */
    int structLength;
    /**
     * Holds the offset of this struct relative to the outer struct or
     * to the byte buffer if there is no outer.
     */
    int structOffset;
    /**
     * Indicates if the index has to be reset for each new field (
     * <code>true</code> only for Union subclasses).
     */
    boolean structResetIndex;


    /**
     * Default constructor.
     */
    protected Struct() {
        this(ByteOrder.nativeOrder());
    }

    /**
     * Default constructor.
     */
    protected Struct(final ByteOrder byteOrder) {
        this.structResetIndex = isUnion();
        this.structByteOrder = byteOrder;
        this.memberList = new ArrayList<>();
        registerMember(this);
    }

    @Override
    public final int absolutePosition() {
        return this.innerStructAbsolutePosition;
    }

    /**
     * Defines the specified array of structs as inner structs.
     * The array is populated if necessary using the struct component
     * default constructor (which must be public).
     *
     * @param <S>     Type of the Struct Array
     * @param structs the struct array.
     * @return the specified struct array.
     * @throws IllegalArgumentException if the specified array contains
     *                                  inner structs.
     */
    protected final <S extends Struct> S[] array(final S[] structs) {
        Class<?> structClass = null;
        final boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < structs.length; ) {
            S struct = structs[i];
            if (struct == null) {
                try {
                    if (structClass == null) {
                        final String arrayName = structs.getClass().getName();
                        final String structName = arrayName.substring(2, arrayName.length() - 1);
                        structClass = Class.forName(structName);
                        if (structClass == null) {
                            throw new IllegalArgumentException(
                                    "Struct class: " + structName + " not found");
                        }
                    }
                    struct = (S) structClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e.toString());
                }
            }
            structs[i++] = inner(struct);
        }
        structResetIndex = resetIndexSaved;
        return structs;
    }

    /**
     * Defines the specified two-dimensional array of structs as inner
     * structs. The array is populated if necessary using the struct component
     * default constructor (which must be public).
     *
     * @param <S>     Type of the Struct array
     * @param structs the two dimensional struct array.
     * @return the specified struct array.
     * @throws IllegalArgumentException if the specified array contains
     *                                  inner structs.
     */
    protected final <S extends Struct> S[][] array(final S[][] structs) {
        final boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < structs.length; i++) {
            array(structs[i]);
        }
        structResetIndex = resetIndexSaved;
        return structs;
    }

    /**
     * Defines the specified three dimensional array of structs as inner
     * structs. The array is populated if necessary using the struct component
     * default constructor (which must be public).
     *
     * @param <S>     Type of the Struct Array
     * @param structs the three dimensional struct array.
     * @return the specified struct array.
     * @throws IllegalArgumentException if the specified array contains
     *                                  inner structs.
     */
    protected final <S extends Struct> S[][][] array(final S[][][] structs) {
        final boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < structs.length; i++) {
            array(structs[i]);
        }
        structResetIndex = resetIndexSaved;
        return structs;
    }

    /**
     * Defines the specified array member. For predefined members,
     * the array is populated when empty; custom members should use
     * literal (populated) arrays.
     *
     * @param <M>         Type of the Array Member
     * @param arrayMember the array member.
     * @return the specified array member.
     * @throws UnsupportedOperationException if the specified array
     *                                       is empty and the member type is unknown.
     */
    protected final <M extends AbstractMember> M[] array(final M[] arrayMember) {
        final boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        if (ARRAY_BOOL_08.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Bool8();
            }
        } else if (ARRAY_BOOL_16.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Bool16();
            }
        } else if (ARRAY_BOOL_32.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Bool32();
            }
        } else if (ARRAY_BOOL_64.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Bool64();
            }
        } else if (ARRAY_SIGNED_08.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Signed8();
            }
        } else if (ARRAY_UNSIGNED_08.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Unsigned8();
            }
        } else if (ARRAY_SIGNED_16.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Signed16();
            }
        } else if (ARRAY_UNSIGNED_16.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Unsigned16();
            }
        } else if (ARRAY_SIGNED_32.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Signed32();
            }
        } else if (ARRAY_UNSIGNED_32.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Unsigned32();
            }
        } else if (ARRAY_SIGNED_64.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Signed64();
            }
        } else if (ARRAY_FLOAT_32.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Float32();
            }
        } else if (ARRAY_FLOAT_64.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Float64();
            }
        } else {
            throw new UnsupportedOperationException(
                    "Cannot create member elements, the arrayMember should "
                            + "contain the member instances instead of null");
        }
        structResetIndex = resetIndexSaved;
        return arrayMember;
    }

    /**
     * Defines the specified two-dimensional array member. For predefined
     * members, the array is populated when empty; custom members should use
     * literal (populated) arrays.
     *
     * @param <M>         Type of the Array Member
     * @param arrayMember the two-dimensional array member.
     * @return the specified array member.
     * @throws UnsupportedOperationException if the specified array
     *                                       is empty and the member type is unknown.
     */
    protected final <M extends AbstractMember> M[][] array(final M[][] arrayMember) {
        final boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < arrayMember.length; i++) {
            array(arrayMember[i]);
        }
        structResetIndex = resetIndexSaved;
        return arrayMember;
    }

    /**
     * Defines the specified three-dimensional array member. For predefined
     * members, the array is populated when empty; custom members should use
     * literal (populated) arrays.
     *
     * @param <M>         Type of the Array Member
     * @param arrayMember the three-dimensional array member.
     * @return the specified array member.
     * @throws UnsupportedOperationException if the specified array
     *                                       is empty and the member type is unknown.
     */
    protected final <M extends AbstractMember> M[][][] array(final M[][][] arrayMember) {
        final boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < arrayMember.length; i++) {
            array(arrayMember[i]);
        }
        structResetIndex = resetIndexSaved;
        return arrayMember;
    }

    /**
     * Defines the specified array of UTF-8 strings, all strings having the
     * specified length (convenience method).
     *
     * @param array        the string array.
     * @param stringLength the length of the string elements.
     * @return the specified string array.
     */
    protected final UTF8String[] array(final UTF8String[] array, final int stringLength) {
        final boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = new UTF8String(stringLength);
        }
        structResetIndex = resetIndexSaved;
        return array;
    }

    public final ByteOrder byteOrder() {
        return structByteOrder;
    }

    private final void calcAbsolutePosition() {
        if (outerStruct != null) {
            outerStruct.calcAbsolutePosition();
        } else {
            for (int i = 0; memberList.size() > i; ++i) {
                memberList.get(i).updateAbsolutePosition();
            }
        }
    }

    public final void clear() {
        final int size = size();
        for (int index = structOffset; size > index; ++index) {
            this.currByteBuffer.put(index, (byte) 0);
        }
    }

    /**
     * Returns the absolute byte position of this struct within its associated
     * {@link #currByteBuffer byte buffer}.
     *
     * @return the relative position of this struct (can be an inner struct)
     * in the byte buffer.
     */
    public final int getAbsolutePosition() {
        return (this.outerStruct != null
                ? this.outerStruct.getAbsolutePosition() + this.structOffset
                : this.structOffset);
    }

    public final void setAbsolutePosition(final int offset) {
        if (this.outerStruct != null) {
            this.outerStruct.setAbsolutePosition(offset + this.structOffset);
        } else {
            this.structOffset = offset;
        }
    }

    /**
     * Sets the current byte buffer for this struct.
     * The specified byte buffer can be mapped to memory for direct memory
     * access or can wrap a shared byte array for I/O purpose
     * (e.g. <code>DatagramPacket</code>).
     * The capacity of the specified byte buffer should be at least the
     * {@link Struct#size() size} of this struct plus the offset position.
     *
     * @param byteBuffer the new byte buffer.
     * @param offset     the position of this struct in the specified byte buffer.
     * @return <code>this</code>
     * @throws IllegalArgumentException      if the specified byteBuffer has a
     *                                       different byte order than this struct.
     * @throws UnsupportedOperationException if this struct is an inner struct.
     * @see #structByteOrder
     */
    public final void initByteBuffer(final ByteBuffer byteBuffer, final int offset) {
        if (this.outerStruct != null) {
            this.currByteBuffer = byteBuffer;
            this.outerStruct.initByteBuffer(byteBuffer, offset);
        } else {
            if (byteBuffer.order() != this.structByteOrder) {
                throw new IllegalArgumentException(
                        "The byte order of the specified byte buffer"
                                + " is different from this struct byte order");
            }
            this.structOffset = offset;
            this.currByteBuffer = byteBuffer;
            setAbsolutePosition(offset);
            calcAbsolutePosition();
        }
    }

    /**
     * Defines the specified struct as inner of this struct.
     *
     * @param <S>    Type of the Inner Struct
     * @param struct the inner struct.
     * @return the specified struct.
     * @throws IllegalArgumentException if the specified struct is already
     *                                  an inner struct.
     */
    protected final <S extends Struct> S inner(final S struct) {
        if (struct.outerStruct != null) {
            throw new IllegalArgumentException("struct: Already an inner struct");
        }
        final Member inner = new Member(struct.size() << 3); // Update indexes.
        struct.outerStruct = this;
        struct.structOffset = inner.memberOffset;
        return struct;
    }

    /**
     * Indicates if this struct's members are mapped to the same location
     * in memory (default <code>false</code>). This method is useful for
     * applications extending {@link Struct} with new member types in order to
     * create unions from these new structs. For example:[code]
     * public abstract class FortranStruct extends Struct {
     * public class FortranString extends Member {...}
     * protected FortranString[] array(FortranString[] array, int stringLength) { ... }
     * }
     * public abstract class FortranUnion extends FortranStruct {
     * // Inherits new members and methods.
     * public final isUnion() {
     * return true;
     * }
     * }[/code]
     *
     * @return <code>true</code> if this struct's members are mapped to
     * to the same location in memory; <code>false</code>
     * otherwise.
     * @see Union
     */
    public final boolean isUnion() {
        return (this instanceof Union);
    }

    /**
     * Reads this struct from the specified input stream
     * (convenience method when using Stream I/O). For better performance,
     * use of Block I/O (e.g. <code>java.nio.channels.*</code>) is recommended.
     * This method behaves appropriately when not all of the data is available
     * from the input stream. Incomplete data is extremely common when the
     * input stream is associated with something like a TCP connection.
     * The typical usage pattern in those scenarios is to repeatedly call
     * read() until the entire message is received.
     *
     * @param in the input stream being read from.
     * @return the number of bytes read (typically the {@link #size() size}
     * of this struct.
     * @throws IOException if an I/O error occurs.
     */
    public final int read(final InputStream in) throws IOException {
        final int size = size();
        int remaining = size - this.currByteBuffer.position();
        if (remaining == 0) {
            remaining = size;// at end so move to beginning
        }
        final int alreadyRead = size - remaining; // typically 0
        if (this.currByteBuffer.hasArray()) {
            final int offset = this.currByteBuffer.arrayOffset() + getAbsolutePosition();
            final int bytesRead = in.read(this.currByteBuffer.array(), offset + alreadyRead, remaining);
            this.currByteBuffer.position(getAbsolutePosition()
                    + alreadyRead
                    + bytesRead
                    - offset);
            return bytesRead;
        } else {
            synchronized (this.currByteBuffer) {
                final byte[] _bytes = new byte[size()];
                final int bytesRead = in.read(_bytes, 0, remaining);
                this.currByteBuffer.position(getAbsolutePosition() + alreadyRead);
                this.currByteBuffer.put(_bytes, 0, bytesRead);
                return bytesRead;
            }
        }
    }

    /**
     * Reads the specified bits from this Struct as an long (signed) integer
     * value.
     *
     * @param bitOffset the bit start position in the Struct.
     * @param bitSize   the number of bits.
     * @return the specified bits read as a signed long.
     * @throws IllegalArgumentException if
     *                                  {@code(bitOffset + bitSize - 1) / 8 >= this.size()}
     */
    public final long readBits(final int bitOffset, final int bitSize) {
        if ((bitOffset + bitSize - 1) >> 3 >= this.size()) {
            throw new IllegalArgumentException("Attempt to read outside the Struct");
        }
        final int offset = bitOffset >> 3;
        int bitStart = bitOffset - (offset << 3);
        bitStart = (structByteOrder == ByteOrder.BIG_ENDIAN)
                ? bitStart
                : 64 - bitSize - bitStart;
        final int index = getAbsolutePosition() + offset;
        long value = readByteBufferLong(index);
        value <<= bitStart; // Clears preceding bits.
        value >>= (64 - bitSize); // Signed shift.
        return value;
    }

    private final byte readByte(final int index) {
        return (index < this.currByteBuffer.limit()) ? this.currByteBuffer.get(index) : 0;
    }

    private final long readByteBufferLong(final int fromIndex) {
        int index = fromIndex;
        if (index + 8 < this.currByteBuffer.limit()) {
            return this.currByteBuffer.getLong(index);
        } else if (this.currByteBuffer.order() == ByteOrder.LITTLE_ENDIAN) {
            return (readByte(index) & 0xff)
                    + ((readByte(++index) & 0xff) << 8)
                    + ((readByte(++index) & 0xff) << 16)
                    + ((readByte(++index) & 0xffL) << 24)
                    + ((readByte(++index) & 0xffL) << 32)
                    + ((readByte(++index) & 0xffL) << 40)
                    + ((readByte(++index) & 0xffL) << 48)
                    + ((readByte(++index) & 0xffL) << 56);
        } else {
            return (((long) readByte(index)) << 56)
                    + ((readByte(++index) & 0xffL) << 48)
                    + ((readByte(++index) & 0xffL) << 40)
                    + ((readByte(++index) & 0xffL) << 32)
                    + ((readByte(++index) & 0xffL) << 24)
                    + ((readByte(++index) & 0xff) << 16)
                    + ((readByte(++index) & 0xff) << 8)
                    + (readByte(++index) & 0xffL);
        }
    }

    private void registerMember(final PositionUpdatable positionUpdatable) {
        if (outerStruct != null) {
            outerStruct.registerMember(positionUpdatable);
        } else {
            this.memberList.add(positionUpdatable);
        }
    }

    /**
     * Returns the size in bytes of this struct. The size includes
     * tail padding to satisfy the struct word size requirement
     * (defined by the largest word size of its {@link AbstractMember members}).
     *
     * @return the C/C++ <code>sizeof(this)</code>.
     */
    public final int size() {
        return structLength;
    }

    /**
     * Returns the <code>String</code> representation of this struct
     * in the form of its constituing bytes (hexadecimal). For example:[code]
     * public static class Student extends Struct {
     * Utf8String name  = new Utf8String(16);
     * Unsigned16 year  = new Unsigned16();
     * Float32    grade = new Float32();
     * }
     * Student student = new Student();
     * student.name.set("John Doe");
     * student.year.set(2003);
     * student.grade.set(12.5f);
     * System.out.println(student);
     * <p>
     * 4A 6F 68 6E 20 44 6F 65 00 00 00 00 00 00 00 00
     * 07 D3 00 00 41 48 00 00[/code]
     *
     * @return a hexadecimal representation of the bytes content for this
     * struct.
     */
    public final String toString() {
        final StringBuilder tmp = new StringBuilder();
        final int size = size();
        final int start = getAbsolutePosition();
        for (int i = 0; i < size; i++) {
            int b = this.currByteBuffer.get(start + i) & 0xFF;
            tmp.append(HEX_CHAR[b >> 4]);
            tmp.append(HEX_CHAR[b & 0xF]);
            tmp.append(((i & 0xF) == 0xF) ? '\n' : ' ');
        }
        return tmp.toString();
    }

    @Override
    public final void updateAbsolutePosition() {
        this.innerStructAbsolutePosition = getAbsolutePosition();
    }

    /**
     * Writes this struct to the specified output stream
     * (convenience method when using Stream I/O). For better performance,
     * use of Block I/O (e.g. <code>java.nio.channels.*</code>) is recommended.
     *
     * @param out the output stream to write to.
     * @throws IOException if an I/O error occurs.
     */
    public final void write(final OutputStream out) throws IOException {
        if (this.currByteBuffer.hasArray()) {
            final int offset = this.currByteBuffer.arrayOffset() + getAbsolutePosition();
            out.write(this.currByteBuffer.array(), offset, size());
        } else {
            synchronized (this.currByteBuffer) {
                final byte[] _bytes = new byte[size()];
                this.currByteBuffer.position(getAbsolutePosition());
                this.currByteBuffer.get(_bytes);
                out.write(_bytes);
            }
        }
    }

    /**
     * Writes the specified bits into this Struct.
     *
     * @param bitsValue the bits value as a signed long.
     * @param bitOffset the bit start position in the Struct.
     * @param bitSize   the number of bits.
     * @throws IllegalArgumentException if
     *                                  {@code(bitOffset + bitSize - 1) / 8 >= this.size()}
     */
    public final void writeBits(final long bitsValue, final int bitOffset, final int bitSize) {
        long value = bitsValue;
        if ((bitOffset + bitSize - 1) >> 3 >= this.size()) {
            throw new IllegalArgumentException("Attempt to write outside the Struct");
        }
        final int offset = bitOffset >> 3;
        int bitStart = (structByteOrder == ByteOrder.BIG_ENDIAN)
                ? bitOffset - (offset << 3)
                : 64 - bitSize - (bitOffset - (offset << 3));
        long mask = -1L;
        mask <<= bitStart; // Clears preceding bits
        mask >>>= (64 - bitSize); // Unsigned shift.
        mask <<= 64 - bitSize - bitStart;
        value <<= (64 - bitSize - bitStart);
        value &= mask; // Protects against out of range values.
        final int index = getAbsolutePosition() + offset;
        final long oldValue = readByteBufferLong(index);
        final long resetValue = oldValue & (~mask);
        final long newValue = resetValue | value;
        writeByteBufferLong(index, newValue);
    }

    private void writeByte(final int index, final byte value) {
        if (index < this.currByteBuffer.limit()) {
            this.currByteBuffer.put(index, value);
        }
    }

    private void writeByteBufferLong(final int fromIndex, final long value) {
        int index = fromIndex;
        if (index + 8 < this.currByteBuffer.limit()) {
            this.currByteBuffer.putLong(index, value);
            return;
        } else if (this.currByteBuffer.order() == ByteOrder.LITTLE_ENDIAN) {
            writeByte(index, (byte) value);
            writeByte(++index, (byte) (value >> 8));
            writeByte(++index, (byte) (value >> 16));
            writeByte(++index, (byte) (value >> 24));
            writeByte(++index, (byte) (value >> 32));
            writeByte(++index, (byte) (value >> 40));
            writeByte(++index, (byte) (value >> 48));
            writeByte(++index, (byte) (value >> 56));
        } else {
            writeByte(index, (byte) (value >> 56));
            writeByte(++index, (byte) (value >> 48));
            writeByte(++index, (byte) (value >> 40));
            writeByte(++index, (byte) (value >> 32));
            writeByte(++index, (byte) (value >> 24));
            writeByte(++index, (byte) (value >> 16));
            writeByte(++index, (byte) (value >> 8));
            writeByte(++index, (byte) value);
        }
    }

    /**
     * This inner class represents the base class for all {@link Struct}
     * members. It allows applications to define additional member types.
     * For example:[code]
     * public class MyStruct extends Struct {
     * BitSet bits = new BitSet(256);
     * ...
     * public BitSet extends Member {
     * public BitSet(int nbrBits) {
     * super(nbrBits, 0); // Direct bit access.
     * }
     * public boolean get(int i) { ... }
     * public void set(int i, boolean value) { ...}
     * }
     * }[/code]
     */
    protected abstract class AbstractMember implements PositionUpdatable {

        /**
         * Holds the relative bit offset of this member to its struct offset.
         */
        final int memberBitIndex;
        /**
         * Holds the bit length of this member.
         */
        final int memberBitLength;
        /**
         * Holds the relative offset (in bytes) of this member within its struct.
         */
        final int memberOffset;

        int memberAbsolutePosition;

        /**
         * Base constructor for custom member types.
         * <p>
         * The word size can be zero, in which case the {@link #memberOffset}
         * of the member does not change, only {@link #memberBitIndex} is
         * incremented.
         *
         * @param bitLength the number of bits or <code>0</code>
         *                  to force next member on next word boundary.
         * @param wordSize  the word size in bytes used when accessing
         *                  this member data or <code>0</code> if the data is accessed
         *                  at the bit level.
         */
        protected AbstractMember(final int bitLength, final int wordSize) {
            registerMember(this);

            memberBitLength = bitLength;

            // Resets index if union.
            if (structResetIndex) {
                structIndex = 0;
            }

            // Check if we can merge bitfields (always true if no word boundary).
            if ((wordSize == 0) || (
                    (bitLength != 0)
                            && (wordSize == currWordSize)
                            && ((currBitsUsed + bitLength) <= (wordSize << 3)))) {

                memberOffset = structIndex - currWordSize;
                memberBitIndex = currBitsUsed;
                currBitsUsed += bitLength;

                // Straddling word boundary only possible if (wordSize == 0)
                while (currBitsUsed > (currWordSize << 3)) {
                    structIndex++;
                    currWordSize++;
                    structLength = Math.max(structLength, structIndex);
                }
                return; // Bit field merge done.
            }

            // Sets member indices.
            memberOffset = structIndex;
            memberBitIndex = 0;

            // Update struct indices.
            structIndex += Math.max(wordSize, (bitLength + 7) >> 3);
            currWordSize = wordSize;
            currBitsUsed = bitLength;
            structLength = Math.max(structLength, structIndex);
            // size and index may differ because of {@link Union}
        }

        @Override
        public final int absolutePosition() {
            return this.memberAbsolutePosition;
        }

        /**
         * Returns the number of bits in this member. Can be zero if this
         * member is used to force the next member to the next word boundary.
         *
         * @return the number of bits in the member.
         */
        public final int bitLength() {
            return memberBitLength;
        }

        /**
         * Returns the byte offset of this member in its struct.
         * Equivalent to C/C++ <code>offsetof(struct(), this)</code>
         *
         * @return the offset of this member in the Struct.
         */
        public final int offset() {
            return memberOffset;
        }

        @Override
        public final void updateAbsolutePosition() {
            this.memberAbsolutePosition = getAbsolutePosition() + memberOffset;
        }

    }

    /**
     * This class represents an arbitrary size (unsigned) bit field with
     * no word size constraint (they can straddle words boundaries).
     */
    public final class BitField extends AbstractMember {

        public BitField(final int nbrOfBits) {
            super(nbrOfBits, 0);
        }

        public final byte byteValue() {
            return (byte) longValue();
        }

        public final int intValue() {
            return (int) longValue();
        }

        public final long longValue() {
            long signedValue = readBits(memberBitIndex + (memberOffset << 3), memberBitLength);
            return ~(-1L << memberBitLength) & signedValue;
        }

        public final void set(final long value) {
            writeBits(value, memberBitIndex + (memberOffset << 3), memberBitLength);
        }

        public final short shortValue() {
            return (short) longValue();
        }

        public final String toString() {
            return String.valueOf(longValue());
        }
    }

    /**
     * This class represents a 8 bits boolean with <code>true</code> represented
     * by <code>1</code> and <code>false</code> represented by <code>0</code>.
     */
    public final class Bool16 extends ScalarMember {

        public Bool16() {
            super(16);
        }

        public final boolean get() {
            return getShort() != 0;
        }

        public final void set(final short value) {
            set(value != 0);
        }

        public final void set(final boolean value) {
            final short boolValue = (short) (value ? 1 : 0);
            setShort(boolValue);
        }

        public String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 8 bits boolean with <code>true</code> represented
     * by <code>1</code> and <code>false</code> represented by <code>0</code>.
     */
    public final class Bool32 extends ScalarMember {

        public Bool32() {
            super(32);
        }

        public final boolean get() {
            return getInt() != 0;
        }

        public final void set(final int value) {
            set(value != 0);
        }

        public final void set(final boolean value) {
            final int boolValue = (value ? 1 : 0);
            setInt(boolValue);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 8 bits boolean with <code>true</code> represented
     * by <code>1</code> and <code>false</code> represented by <code>0</code>.
     */
    public final class Bool64 extends ScalarMember {

        public Bool64() {
            super(64);
        }

        public final boolean get() {
            return getLong() != 0;
        }

        public final void set(final long value) {
            set(value != 0);
        }

        public final void set(final boolean value) {
            final long boolValue = (value ? 1L : 0L);
            setLong(boolValue);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 8 bits boolean with <code>true</code> represented
     * by <code>1</code> and <code>false</code> represented by <code>0</code>.
     */
    public final class Bool8 extends ScalarMember {

        public Bool8() {
            super(8);
        }

        public final boolean get() {
            return getByte() != 0;
        }

        public final void set(final byte value) {
            set(value != 0);
        }

        public final void set(final boolean value) {
            final byte boolValue = (byte) (value ? 1 : 0);
            setByte(boolValue);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 16 bits {@link Enum}.
     */
    public final class Enum16<T extends Enum<T>> extends ScalarMember {

        private final T[] _values;

        public Enum16(final T[] values) {
            super(16);
            _values = values;
        }

        public final T get() {
            final short ordinal = getShort();
            return _values[ordinal];
        }

        public final void set(final T e) {
            final int value = e.ordinal();
            setShort((short) value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 32 bits {@link Enum}.
     */
    public final class Enum32<T extends Enum<T>> extends ScalarMember {

        private final T[] _values;

        public Enum32(final T[] values) {
            super(32);
            _values = values;
        }

        public final T get() {
            final int ordinal = getInt();
            return _values[ordinal];
        }

        public final void set(final T e) {
            final int value = e.ordinal();
            setInt(value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 64 bits {@link Enum}.
     */
    public final class Enum64<T extends Enum<T>> extends ScalarMember {

        private final T[] _values;

        public Enum64(final T[] values) {
            super(64);
            _values = values;
        }

        public final T get() {
            final long ordinal = getLong();
            return _values[(int) ordinal];
        }

        public final void set(final T e) {
            final long value = e.ordinal();
            setLong(value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 8 bits {@link Enum}.
     */
    public final class Enum8<T extends Enum<T>> extends ScalarMember {

        private final T[] _values;

        public Enum8(final T[] values) {
            super(8);
            _values = values;
        }

        public final T get() {
            final byte ordinal = getByte();
            return _values[ordinal];
        }

        public final void set(final T e) {
            final int value = e.ordinal();
            setByte((byte) value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 32 bits float (C/C++/Java <code>float</code>).
     */
    public final class Float32 extends ScalarMember {

        public Float32() {
            super(32);
        }

        public final float get() {
            return currByteBuffer.getFloat(this.memberAbsolutePosition);
        }

        public final void set(final float value) {
            currByteBuffer.putFloat(this.memberAbsolutePosition, value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 64 bits float (C/C++/Java <code>double</code>).
     */
    public final class Float64 extends ScalarMember {

        public Float64() {
            super(64);
        }

        public final double get() {
            return currByteBuffer.getDouble(this.memberAbsolutePosition);
        }

        public final void set(final double value) {
            currByteBuffer.putDouble(this.memberAbsolutePosition, value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    protected final class Member extends AbstractMember {

        Member(final int bitLength) {
            super(bitLength, 1);
        }
    }

    protected abstract class ScalarMember extends AbstractMember {
        /**
         * Holds the bit length of this member.
         */
        final int memberMaxBitLength;

        /**
         * Base constructor for custom member types.
         * <p>
         * The word size can be zero, in which case the {@link #memberOffset}
         * of the member does not change, only {@link #memberBitIndex} is
         * incremented.
         *
         * @param bitLength the number of bits or <code>0</code>
         *                  to force next member on next word boundary.
         * @param wordSize  the word size in bytes used when accessing
         *                  this member data or <code>0</code> if the data is accessed
         */
        protected ScalarMember(final int bitLength, final int wordSize) {
            super(bitLength, wordSize);
            this.memberMaxBitLength = wordSize * 8;
        }

        /**
         * Base constructor for custom member types.
         * <p>
         * The word size can be zero, in which case the {@link #memberOffset}
         * of the member does not change, only {@link #memberBitIndex} is
         * incremented.
         *
         * @param bitLength the number of bits or <code>0</code>
         *                  to force next member on next word boundary.
         */
        protected ScalarMember(final int bitLength) {
            this(bitLength, (bitLength / 8));
        }

        final byte getByte() {
            return currByteBuffer.get(this.memberAbsolutePosition);
        }

        final void setByte(final byte value) {
            currByteBuffer.put(this.memberAbsolutePosition, value);
        }

        final char getChar() {
            return currByteBuffer.getChar(this.memberAbsolutePosition);
        }

        final void setChar(final char value) {
            currByteBuffer.putChar(this.memberAbsolutePosition, value);
        }

        final int getInt() {
            return currByteBuffer.getInt(this.memberAbsolutePosition);
        }

        final void setInt(final int value) {
            currByteBuffer.putInt(this.memberAbsolutePosition, value);
        }

        final long getLong() {
            return currByteBuffer.getLong(this.memberAbsolutePosition);
        }

        final void setLong(final long value) {
            currByteBuffer.putLong(this.memberAbsolutePosition, value);
        }

        final short getShort() {
            return currByteBuffer.getShort(this.memberAbsolutePosition);
        }

        final void setShort(final short value) {
            currByteBuffer.putShort(this.memberAbsolutePosition, value);
        }

    }

    /**
     * This class represents a 16 bits signed integer.
     */
    public final class Signed16 extends ScalarMember {

        public Signed16() {
            super(16);
        }

        public final short get() {
            return getShort();
        }

        public final void set(final short value) {
            setShort(value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 32 bits signed integer.
     */
    public final class Signed32 extends ScalarMember {

        public Signed32() {
            super(32);
        }

        public final int get() {
            return getInt();
        }

        public final void set(final int value) {
            setInt(value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 64 bits signed integer.
     */
    public class Signed64 extends ScalarMember {

        public Signed64() {
            super(64);
        }

        public final long get() {
            return getLong();
        }

        public final void set(final long value) {
            setLong(value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 8 bits signed integer.
     */
    public final class Signed8 extends ScalarMember {

        public Signed8() {
            super(8);
        }

        public final byte get() {
            return getByte();
        }

        public final void set(final byte value) {
            setByte(value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a UTF-8 character string, null terminated
     * (for C/C++ compatibility)
     */
    public final class UTF8String extends AbstractMember {

        private final UTF8ByteBufferReader _reader = new UTF8ByteBufferReader();
        private final UTF8ByteBufferWriter _writer = new UTF8ByteBufferWriter();
        private final int length;

        public UTF8String(final int length) {
            super((length + 1) << 3, 1);
            this.length = length + 1; // Takes into account 0 terminator.
        }

        public final CharSequence get() {
            final StringBuilder tmp = new StringBuilder();
            try {
                currByteBuffer.position(this.memberAbsolutePosition);
                _reader.setInput(currByteBuffer);
                for (int i = 0; i < length; i++) {
                    char c = (char) _reader.read();
                    if (c == 0) { // Null terminator.
                        return tmp.toString();
                    } else {
                        tmp.append(c);
                    }
                }
                return tmp.toString();
            } catch (IOException e) { // Should never happen.
                throw new Error(e.getMessage());
            } finally {
                _reader.reset();
            }
        }

        public final int length() {
            return length;
        }

        public final void set(final CharSequence string) {
            try {
                currByteBuffer.position(this.memberAbsolutePosition);
                _writer.setOutput(currByteBuffer);
                if (string.length() < length) {
                    _writer.write(string);
                    _writer.write(0); // Marks end of string.
                } else if (string.length() > length) { // Truncates.
                    _writer.write(string.subSequence(0, length));
                } else { // Exact same length.
                    _writer.write(string);
                }
            } catch (IOException e) { // Should never happen.
                throw new Error(e.getMessage());
            } finally {
                _writer.reset();
            }
        }

        public final String toString() {
            return this.get().toString();
        }
    }

    /**
     * This class represents a 16 bits signed integer.
     */
    public final class UTFChar16 extends ScalarMember {

        public UTFChar16() {
            super(16);
        }

        public final char get() {
            return getChar();
        }

        public final void set(final CharSequence single) {
            set(single.charAt(0));
        }

        public final void set(final char value) {
            setChar(value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 8 bits unsigned integer.
     */
    public final class UTFChar8 extends ScalarMember {

        public UTFChar8() {
            super(8);
        }

        public final char get() {
            return (char) (0xFF & getByte());
        }

        public final void set(final CharSequence single) {
            set(single.charAt(0));
        }

        public final void set(final char value) {
            setByte((byte) value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 16 bits unsigned integer.
     */
    public final class Unsigned16 extends ScalarMember {

        public Unsigned16() {
            super(16);
        }

        public final int get() {
            return 0xFFFF & getShort();
        }

        public final void set(final int value) {
            setShort((short) value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 32 bits unsigned integer.
     */
    public final class Unsigned32 extends ScalarMember {

        public Unsigned32() {
            super(32);
        }

        public final long get() {
            return 0xFFFFFFFFL & getInt();
        }

        public final void set(final long value) {
            setInt((int) value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a 8 bits unsigned integer.
     */
    public final class Unsigned8 extends ScalarMember {

        public Unsigned8() {
            super(8);
        }

        public final short get() {
            return (short) (0xFF & getByte());
        }

        public final void set(final short value) {
            setByte((byte) value);
        }

        public final String toString() {
            return String.valueOf(this.get());
        }
    }
}
