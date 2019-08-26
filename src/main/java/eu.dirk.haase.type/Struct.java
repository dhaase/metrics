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
 * <p> Applications can work with the raw {@link #getByteBuffer() bytes}
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
 * message.setByteBuffer(ByteBuffer.wrap(bytes), 0);
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
 * setByteBuffer(Clock.nativeBuffer(), 0);
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
 * <p> Finally, it is possible to change the {@link #setByteBuffer ByteBuffer}
 * and/or the Struct {@link #setByteBufferPosition position} in its
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

    private static final Class<? extends Bool16[]> BOOL_16 = new Bool16[0].getClass();
    private static final Class<? extends Bool32[]> BOOL_32 = new Bool32[0].getClass();
    private static final Class<? extends Bool8[]> BOOL_8 = new Bool8[0].getClass();
    private static final Class<? extends Float32[]> FLOAT_32 = new Float32[0]
            .getClass();
    private static final Class<? extends Float64[]> FLOAT_64 = new Float64[0]
            .getClass();
    private static final char[] HEXA = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final Class<? extends Signed16[]> SIGNED_16 = new Signed16[0]
            .getClass();
    private static final Class<? extends Signed32[]> SIGNED_32 = new Signed32[0]
            .getClass();
    private static final Class<? extends Signed64[]> SIGNED_64 = new Signed64[0]
            .getClass();
    private static final Class<? extends Signed8[]> SIGNED_8 = new Signed8[0]
            .getClass();
    private static final Class<? extends Unsigned16[]> UNSIGNED_16 = new Unsigned16[0]
            .getClass();
    private static final Class<? extends Unsigned32[]> UNSIGNED_32 = new Unsigned32[0]
            .getClass();
    private static final Class<? extends Unsigned8[]> UNSIGNED_8 = new Unsigned8[0]
            .getClass();

    final List<PositionUpdatable> memberList;

    private final ByteOrder structByteOrder;
    /**
     * Holds the bits used in the word during construction (for bit fields).
     * This is the number of bits used in the last word.
     */
    int currBitsUsed;
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
     * Holds this struct alignment in bytes (largest word size of its members).
     */
    int structAlignment = 1;
    /**
     * Holds the byte buffer backing the struct (top struct).
     */
    ByteBuffer structByteBuffer;
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

    private static byte readByte(final int index, final ByteBuffer byteBuffer) {
        return (index < byteBuffer.limit()) ? byteBuffer.get(index) : 0;
    }

    private static void writeByte(final int index, final ByteBuffer byteBuffer, final byte value) {
        if (index < byteBuffer.limit()) {
            byteBuffer.put(index, value);
        }
    }

    @Override
    public int absolutePosition() {
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
    protected <S extends Struct> S[] array(final S[] structs) {
        Class<?> structClass = null;
        boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < structs.length; ) {
            S struct = structs[i];
            if (struct == null) {
                try {
                    if (structClass == null) {
                        String arrayName = structs.getClass().getName();
                        String structName = arrayName.substring(2,
                                arrayName.length() - 1);
                        structClass = Class.forName(structName);
                        if (structClass == null) {
                            throw new IllegalArgumentException(
                                    "Struct class: " + structName + " not found");
                        }
                    }
                    struct = (S) structClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            structs[i++] = inner(struct);
        }
        structResetIndex = resetIndexSaved;
        return (S[]) structs;
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
    protected <S extends Struct> S[][] array(final S[][] structs) {
        boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < structs.length; i++) {
            array(structs[i]);
        }
        structResetIndex = resetIndexSaved;
        return (S[][]) structs;
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
    protected <S extends Struct> S[][][] array(final S[][][] structs) {
        boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < structs.length; i++) {
            array(structs[i]);
        }
        structResetIndex = resetIndexSaved;
        return (S[][][]) structs;
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
    protected <M extends AbstractMember> M[] array(final M[] arrayMember) {
        boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        if (BOOL_8.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Bool8();
            }
        } else if (BOOL_16.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Bool16();
            }
        } else if (BOOL_32.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Bool32();
            }
        } else if (SIGNED_8.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Signed8();
            }
        } else if (UNSIGNED_8.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Unsigned8();
            }
        } else if (SIGNED_16.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Signed16();
            }
        } else if (UNSIGNED_16.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Unsigned16();
            }
        } else if (SIGNED_32.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Signed32();
            }
        } else if (UNSIGNED_32.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Unsigned32();
            }
        } else if (SIGNED_64.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Signed64();
            }
        } else if (FLOAT_32.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Float32();
            }
        } else if (FLOAT_64.isInstance(arrayMember)) {
            for (int i = 0; i < arrayMember.length; ) {
                arrayMember[i++] = (M) this.new Float64();
            }
        } else {
            throw new UnsupportedOperationException(
                    "Cannot create member elements, the arrayMember should "
                            + "contain the member instances instead of null");
        }
        structResetIndex = resetIndexSaved;
        return (M[]) arrayMember;
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
    protected <M extends AbstractMember> M[][] array(final M[][] arrayMember) {
        boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < arrayMember.length; i++) {
            array(arrayMember[i]);
        }
        structResetIndex = resetIndexSaved;
        return (M[][]) arrayMember;
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
    protected <M extends AbstractMember> M[][][] array(final M[][][] arrayMember) {
        boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            structIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < arrayMember.length; i++) {
            array(arrayMember[i]);
        }
        structResetIndex = resetIndexSaved;
        return (M[][][]) arrayMember;
    }

    /**
     * Defines the specified array of UTF-8 strings, all strings having the
     * specified length (convenience method).
     *
     * @param array        the string array.
     * @param stringLength the length of the string elements.
     * @return the specified string array.
     */
    protected UTF8String[] array(final UTF8String[] array, final int stringLength) {
        boolean resetIndexSaved = structResetIndex;
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

    private void calcAbsolutePosition() {
        if (outerStruct != null) {
            outerStruct.calcAbsolutePosition();
        } else {
            for (int i = 0; memberList.size() > i; ++i) {
                memberList.get(i).updateAbsolutePosition();
            }
        }
    }

    public final void clear() {
        clear(getByteBuffer());
    }

    public final void clear(final ByteBuffer byteBuffer) {
        final int size = size();
        for (int index = getAbsolutePosition(byteBuffer); size > index; ++index) {
            byteBuffer.put(index, (byte) 0);
        }
    }

    /**
     * Returns the absolute byte position of this struct within its associated
     * {@link #getByteBuffer byte buffer}.
     *
     * @return the absolute position of this struct (can be an inner struct)
     * in the byte buffer.
     */
    public final int getAbsolutePosition(final ByteBuffer byteBuffer) {
        return getRelativePosition() + byteBuffer.position();
    }

    /**
     * Returns the byte buffer for this struct. This method will allocate
     * a new <b>direct</b> buffer if none has been set.
     * <p>
     * <p> Changes to the buffer's content are visible in this struct,
     * and vice versa.</p>
     * <p> The buffer of an inner struct is the same as its parent struct.</p>
     * <p> If no byte buffer has been {@link Struct#setByteBuffer set},
     * a direct buffer is allocated with a capacity equals to this
     * struct's {@link Struct#size() size}.</p>
     *
     * @return the current byte buffer or a new direct buffer if none set.
     * @see #setByteBuffer
     */
    public final ByteBuffer getByteBuffer() {
        if (outerStruct != null) return outerStruct.getByteBuffer();
        return (structByteBuffer != null) ? structByteBuffer : newBuffer();
    }

    /**
     * Returns the relative byte position of this struct within its associated
     * {@link #getByteBuffer byte buffer}.
     *
     * @return the relative position of this struct (can be an inner struct)
     * in the byte buffer.
     */
    public final int getRelativePosition() {
        return (outerStruct != null
                ? outerStruct.getRelativePosition() + structOffset
                : structOffset);
    }

    public final void setRelativePosition(final int offset) {
        if (outerStruct != null) {
            outerStruct.setRelativePosition(offset + structOffset);
        } else {
            structOffset = offset;
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
    protected <S extends Struct> S inner(final S struct) {
        if (struct.outerStruct != null) {
            throw new IllegalArgumentException("struct: Already an inner struct");
        }
        final Member inner = new Member(struct.size() << 3, struct.structAlignment); // Update indexes.
        struct.outerStruct = this;
        struct.structOffset = inner.memberOffset;
        return struct;
    }

    /**
     * Indicates if this struct is packed (configurable).
     * By default, {@link AbstractMember members} of a struct are aligned on the
     * boundary corresponding to the member base type; padding is performed
     * if necessary. This directive is <b>not</b> inherited by inner structs.
     * Sub-classes may change the packing directive by overriding this method.
     * For example:[code]
     * public class MyStruct extends Struct {
     * ... // Members initialization.
     * public boolean isPacked() {
     * return true; // MyStruct is packed.
     * }
     * }}[/code]
     *
     * @return <code>true</code> if word size requirements are ignored.
     * <code>false</code> otherwise (default).
     */
    public boolean isPacked() {
        return false;
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
    public boolean isUnion() {
        return false;
    }

    private synchronized ByteBuffer newBuffer() {
        if (structByteBuffer != null) {
            return structByteBuffer; // Synchronized check.
        }
        final ByteBuffer bf = ByteBuffer.allocateDirect(size());
        bf.order(structByteOrder);
        setByteBuffer(bf, 0);
        return structByteBuffer;
    }

    /**
     * Returns the outer of this struct or <code>null</code> if this struct
     * is not an inner struct.
     *
     * @return the outer struct or <code>null</code>.
     */
    public Struct outer() {
        return outerStruct;
    }

    public int outerAbsolutePosition() {
        return (outerStruct != null
                ? outerStruct.getRelativePosition()
                : structOffset);
    }

    public int read(final InputStream in) throws IOException {
        return read(getByteBuffer(), in);
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
    public int read(final ByteBuffer byteBuffer, final InputStream in) throws IOException {
        final int size = size();
        int remaining = size - byteBuffer.position();
        if (remaining == 0) {
            remaining = size;// at end so move to beginning
        }
        final int alreadyRead = size - remaining; // typically 0
        if (byteBuffer.hasArray()) {
            final int offset = byteBuffer.arrayOffset() + getAbsolutePosition(byteBuffer);
            final int bytesRead = in.read(byteBuffer.array(), offset + alreadyRead, remaining);
            byteBuffer.position(getAbsolutePosition(byteBuffer)
                    + alreadyRead
                    + bytesRead
                    - offset);
            return bytesRead;
        } else {
            synchronized (byteBuffer) {
                final byte[] _bytes = new byte[size()];
                final int bytesRead = in.read(_bytes, 0, remaining);
                byteBuffer.position(getAbsolutePosition(byteBuffer) + alreadyRead);
                byteBuffer.put(_bytes, 0, bytesRead);
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
    public long readBits(final ByteBuffer byteBuffer, final int bitOffset, final int bitSize) {
        if ((bitOffset + bitSize - 1) >> 3 >= this.size()) {
            throw new IllegalArgumentException("Attempt to read outside the Struct");
        }
        final int offset = bitOffset >> 3;
        int bitStart = bitOffset - (offset << 3);
        bitStart = (structByteOrder == ByteOrder.BIG_ENDIAN)
                ? bitStart
                : 64 - bitSize - bitStart;
        final int index = getAbsolutePosition(byteBuffer) + offset;
        long value = readByteBufferLong(byteBuffer, index);
        value <<= bitStart; // Clears preceding bits.
        value >>= (64 - bitSize); // Signed shift.
        return value;
    }

    private long readByteBufferLong(final ByteBuffer byteBuffer, final int fromIndex) {
        int index = fromIndex;
        if (index + 8 < byteBuffer.limit()) {
            return byteBuffer.getLong(index);
        } else if (byteBuffer.order() == ByteOrder.LITTLE_ENDIAN) {
            return (readByte(index, byteBuffer) & 0xff)
                    + ((readByte(++index, byteBuffer) & 0xff) << 8)
                    + ((readByte(++index, byteBuffer) & 0xff) << 16)
                    + ((readByte(++index, byteBuffer) & 0xffL) << 24)
                    + ((readByte(++index, byteBuffer) & 0xffL) << 32)
                    + ((readByte(++index, byteBuffer) & 0xffL) << 40)
                    + ((readByte(++index, byteBuffer) & 0xffL) << 48)
                    + ((readByte(++index, byteBuffer) & 0xffL) << 56);
        } else {
            return (((long) readByte(index, byteBuffer)) << 56)
                    + ((readByte(++index, byteBuffer) & 0xffL) << 48)
                    + ((readByte(++index, byteBuffer) & 0xffL) << 40)
                    + ((readByte(++index, byteBuffer) & 0xffL) << 32)
                    + ((readByte(++index, byteBuffer) & 0xffL) << 24)
                    + ((readByte(++index, byteBuffer) & 0xff) << 16)
                    + ((readByte(++index, byteBuffer) & 0xff) << 8)
                    + (readByte(++index, byteBuffer) & 0xffL);
        }
    }

    void registerMember(final PositionUpdatable positionUpdatable) {
        if (outerStruct != null) {
            outerStruct.registerMember(positionUpdatable);
        } else {
            this.memberList.add(positionUpdatable);
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
    public final void setByteBuffer(final ByteBuffer byteBuffer, final int offset) {
        if (byteBuffer.order() != structByteOrder) {
            throw new IllegalArgumentException(
                    "The byte order of the specified byte buffer"
                            + " is different from this struct byte order");
        }
        if (outerStruct != null) {
            outerStruct.setByteBuffer(byteBuffer, offset);
        }
        structOffset = offset;
        structByteBuffer = byteBuffer;
        setRelativePosition(offset);
        calcAbsolutePosition();
    }

    /**
     * Returns the size in bytes of this struct. The size includes
     * tail padding to satisfy the struct word size requirement
     * (defined by the largest word size of its {@link AbstractMember members}).
     *
     * @return the C/C++ <code>sizeof(this)</code>.
     */
    public final int size() {
        return (structAlignment <= 1)
                ? structLength
                : ((structLength + structAlignment - 1) / structAlignment) * structAlignment;
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
    public String toString() {
        final StringBuilder tmp = new StringBuilder();
        final int size = size();
        final ByteBuffer byteBuffer = getByteBuffer();
        final int start = getAbsolutePosition(byteBuffer);
        for (int i = 0; i < size; i++) {
            int b = byteBuffer.get(start + i) & 0xFF;
            tmp.append(HEXA[b >> 4]);
            tmp.append(HEXA[b & 0xF]);
            tmp.append(((i & 0xF) == 0xF) ? '\n' : ' ');
        }
        return tmp.toString();
    }

    @Override
    public void updateAbsolutePosition() {
        this.innerStructAbsolutePosition = getRelativePosition();
    }

    public void write(final OutputStream out) throws IOException {
        write(getByteBuffer(), out);
    }

    /**
     * Writes this struct to the specified output stream
     * (convenience method when using Stream I/O). For better performance,
     * use of Block I/O (e.g. <code>java.nio.channels.*</code>) is recommended.
     *
     * @param out the output stream to write to.
     * @throws IOException if an I/O error occurs.
     */
    public void write(final ByteBuffer byteBuffer, final OutputStream out) throws IOException {
        if (byteBuffer.hasArray()) {
            final int offset = byteBuffer.arrayOffset() + getAbsolutePosition(byteBuffer);
            out.write(byteBuffer.array(), offset, size());
        } else {
            synchronized (byteBuffer) {
                final byte[] _bytes = new byte[size()];
                byteBuffer.position(getAbsolutePosition(byteBuffer));
                byteBuffer.get(_bytes);
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
    public void writeBits(final ByteBuffer byteBuffer, final long bitsValue, final int bitOffset, final int bitSize) {
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
        final int index = getAbsolutePosition(byteBuffer) + offset;
        final long oldValue = readByteBufferLong(byteBuffer, index);
        final long resetValue = oldValue & (~mask);
        final long newValue = resetValue | value;
        writeByteBufferLong(byteBuffer, index, newValue);
    }

    private void writeByteBufferLong(final ByteBuffer byteBuffer, final int fromIndex, final long value) {
        int index = fromIndex;
        if (index + 8 < byteBuffer.limit()) {
            byteBuffer.putLong(index, value);
            return;
        } else if (byteBuffer.order() == ByteOrder.LITTLE_ENDIAN) {
            writeByte(index, byteBuffer, (byte) value);
            writeByte(++index, byteBuffer, (byte) (value >> 8));
            writeByte(++index, byteBuffer, (byte) (value >> 16));
            writeByte(++index, byteBuffer, (byte) (value >> 24));
            writeByte(++index, byteBuffer, (byte) (value >> 32));
            writeByte(++index, byteBuffer, (byte) (value >> 40));
            writeByte(++index, byteBuffer, (byte) (value >> 48));
            writeByte(++index, byteBuffer, (byte) (value >> 56));
        } else {
            writeByte(index, byteBuffer, (byte) (value >> 56));
            writeByte(++index, byteBuffer, (byte) (value >> 48));
            writeByte(++index, byteBuffer, (byte) (value >> 40));
            writeByte(++index, byteBuffer, (byte) (value >> 32));
            writeByte(++index, byteBuffer, (byte) (value >> 24));
            writeByte(++index, byteBuffer, (byte) (value >> 16));
            writeByte(++index, byteBuffer, (byte) (value >> 8));
            writeByte(++index, byteBuffer, (byte) value);
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

            // Check alignment.
            if (!isPacked()) {

                // Updates struct's alignment constraint, based on largest word size.
                if ((structAlignment < wordSize)) {
                    structAlignment = wordSize;
                }

                // Adds padding if misaligned.
                final int misaligned = structIndex % wordSize;
                if (misaligned != 0) {
                    structIndex += wordSize - misaligned;
                }
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
        public int absolutePosition() {
            return this.memberAbsolutePosition;
        }

        /**
         * Holds the bit offset of this member (if any).
         * The actual position of the bits data depends upon the endianess and
         * the word size.
         *
         * @return Integer representing the bit index
         */
        public final int bitIndex() {
            return memberBitIndex;
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

        // Returns the member int value.
        final int getWord(final int wordValue) {
            final int shift = (structByteOrder == ByteOrder.BIG_ENDIAN) ? (currWordSize << 3)
                    - memberBitIndex - memberBitLength
                    : memberBitIndex;
            int word = wordValue;
            word >>= shift;
            final int mask = 0xFFFFFFFF >>> (32 - memberBitLength);
            return word & mask;
        }

        // Returns the member long value.
        final long getWord(final long wordValue) {
            final int shift = (structByteOrder == ByteOrder.BIG_ENDIAN)
                    ? (currWordSize << 3) - memberBitIndex - memberBitLength
                    : memberBitIndex;
            long word = wordValue;
            word >>= shift;
            final long mask = 0xFFFFFFFFFFFFFFFFL >>> (64 - memberBitLength);
            return word & mask;
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

        // Sets the member int value.
        final int setWord(int value, final int word) {
            final int shift = (structByteOrder == ByteOrder.BIG_ENDIAN)
                    ? (currWordSize << 3) - memberBitIndex - memberBitLength
                    : memberBitIndex;
            int mask = 0xFFFFFFFF >>> (32 - memberBitLength);
            mask <<= shift;
            value <<= shift;
            return (word & ~mask) | (value & mask);
        }

        // Sets the member long value.
        final long setWord(long value, final long word) {
            final int shift = (structByteOrder == ByteOrder.BIG_ENDIAN)
                    ? (currWordSize << 3) - memberBitIndex - memberBitLength
                    : memberBitIndex;
            long mask = 0xFFFFFFFFFFFFFFFFL >>> (64 - memberBitLength);
            mask <<= shift;
            value <<= shift;
            return (word & ~mask) | (value & mask);
        }

        /**
         * Returns the outer {@link Struct struct} container.
         *
         * @return the outer struct.
         */
        public final Struct struct() {
            return Struct.this;
        }

        @Override
        public void updateAbsolutePosition() {
            this.memberAbsolutePosition = getRelativePosition() + memberOffset;
        }

    }

    /**
     * This class represents an arbitrary size (unsigned) bit field with
     * no word size constraint (they can straddle words boundaries).
     */
    public final class BitField extends AbstractMember {

        public BitField(int nbrOfBits) {
            super(nbrOfBits, 0);
        }

        public byte byteValue() {
            return (byte) longValue(getByteBuffer());
        }

        public byte byteValue(final ByteBuffer byteBuffer) {
            return (byte) longValue(byteBuffer);
        }

        public int intValue() {
            return (int) longValue(getByteBuffer());
        }

        public int intValue(final ByteBuffer byteBuffer) {
            return (int) longValue(byteBuffer);
        }

        public long longValue() {
            return longValue(getByteBuffer());
        }

        public long longValue(final ByteBuffer byteBuffer) {
            long signedValue = readBits(byteBuffer, memberBitIndex + (memberOffset << 3),
                    memberBitLength);
            return ~(-1L << memberBitLength) & signedValue;
        }

        public void set(final ByteBuffer byteBuffer, long value) {
            writeBits(byteBuffer, value, memberBitIndex + (memberOffset << 3), memberBitLength);
        }

        public void set(long value) {
            set(getByteBuffer(), value);
        }

        public short shortValue(final ByteBuffer byteBuffer) {
            return (short) longValue(byteBuffer);
        }

        public short shortValue() {
            return (short) longValue(getByteBuffer());
        }

        public String toString() {
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

        public Bool16(int nbrOfBits) {
            super(nbrOfBits, 2);
        }

        public boolean get() {
            return get(getByteBuffer());
        }

        public boolean get(final ByteBuffer byteBuffer) {
            return getShort(byteBuffer) != 0;
        }

        public void set(final short value) {
            set(getByteBuffer(), (value != 0));
        }

        public void set(final boolean value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final boolean value) {
            final short boolValue = (short) (value ? 1 : 0);
            setShort(byteBuffer, boolValue);
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

        public Bool32(int nbrOfBits) {
            super(nbrOfBits, 4);
        }

        public boolean get() {
            return get(getByteBuffer());
        }

        public boolean get(final ByteBuffer byteBuffer) {
            return getInt(byteBuffer) != 0;
        }

        public void set(final int value) {
            set(getByteBuffer(), (value != 0));
        }

        public void set(final boolean value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final boolean value) {
            final int boolValue = (value ? 1 : 0);
            setInt(byteBuffer, boolValue);
        }

        public String toString() {
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

        public Bool64(int nbrOfBits) {
            super(nbrOfBits, 8);
        }

        public boolean get() {
            return get(getByteBuffer());
        }

        public boolean get(final ByteBuffer byteBuffer) {
            return getLong(byteBuffer) != 0;
        }

        public void set(final long value) {
            set(getByteBuffer(), (value != 0));
        }

        public void set(final boolean value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final boolean value) {
            final long boolValue = (value ? 1L : 0L);
            setLong(byteBuffer, boolValue);
        }

        public String toString() {
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

        public Bool8(int nbrOfBits) {
            super(nbrOfBits, 1);
        }

        public boolean get() {
            return get(getByteBuffer());
        }

        public boolean get(final ByteBuffer byteBuffer) {
            return getByte(byteBuffer) != 0;
        }

        public void set(final byte value) {
            set(getByteBuffer(), (value != 0));
        }

        public void set(final boolean value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final boolean value) {
            final byte boolValue = (byte) (value ? 1 : 0);
            setByte(byteBuffer, boolValue);
        }

        public String toString() {
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

        public Enum16(final T[] values, final int nbrOfBits) {
            super(nbrOfBits, 2);
            _values = values;
        }

        public T get() {
            return get(getByteBuffer());
        }

        public T get(final ByteBuffer byteBuffer) {
            final short ordinal = getShort(byteBuffer);
            return _values[ordinal];
        }

        public void set(final T value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final T e) {
            final int value = e.ordinal();
            setShort(byteBuffer, (short) value);
        }

        public String toString() {
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

        public Enum32(final T[] values, final int nbrOfBits) {
            super(nbrOfBits, 4);
            _values = values;
        }

        public T get() {
            return get(getByteBuffer());
        }

        public T get(final ByteBuffer byteBuffer) {
            final int ordinal = getInt(byteBuffer);
            return _values[ordinal];
        }

        public void set(final T value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final T e) {
            final int value = e.ordinal();
            setInt(byteBuffer, value);
        }

        public String toString() {
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

        public Enum64(final T[] values, final int nbrOfBits) {
            super(nbrOfBits, 8);
            _values = values;
        }

        public T get() {
            return get(getByteBuffer());
        }

        public T get(final ByteBuffer byteBuffer) {
            final long ordinal = getLong(byteBuffer);
            return _values[(int) ordinal];
        }

        public void set(final T value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final T e) {
            final long value = e.ordinal();
            setLong(byteBuffer, value);
        }

        public String toString() {
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

        public Enum8(final T[] values, final int nbrOfBits) {
            super(nbrOfBits, 1);
            _values = values;
        }

        public T get() {
            return get(getByteBuffer());
        }

        public T get(final ByteBuffer byteBuffer) {
            final byte ordinal = getByte(byteBuffer);
            return _values[ordinal];
        }

        public void set(final T value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final T e) {
            final int value = e.ordinal();
            setByte(byteBuffer, (byte) value);
        }

        public String toString() {
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

        public float get() {
            return get(getByteBuffer());
        }

        public float get(final ByteBuffer byteBuffer) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            return byteBuffer.getFloat(index);
        }

        public void set(final float value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final float value) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            byteBuffer.putFloat(index, value);
        }

        public String toString() {
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

        public double get() {
            return get(getByteBuffer());
        }

        public double get(final ByteBuffer byteBuffer) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            return byteBuffer.getDouble(index);
        }

        public void set(final double value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final double value) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            byteBuffer.putDouble(index, value);
        }

        public String toString() {
            return String.valueOf(this.get());
        }
    }

    protected final class Member extends AbstractMember {

        Member(final int bitLength, final int wordSize) {
            super(bitLength, wordSize);
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
        protected ScalarMember(int bitLength, int wordSize) {
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
        protected ScalarMember(int bitLength) {
            this(bitLength, (bitLength / 8));
        }

        final byte getByte(final ByteBuffer byteBuffer) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            final int word = byteBuffer.get(index);
            return (byte) ((memberBitLength == memberMaxBitLength) ? word : getWord(word));
        }

        final char getChar(final ByteBuffer byteBuffer) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            final char word = byteBuffer.getChar(index);
            return (char) ((memberBitLength == memberMaxBitLength) ? word : getWord(word));
        }

        final int getInt(final ByteBuffer byteBuffer) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            final int word = byteBuffer.getInt(index);
            return (memberBitLength == memberMaxBitLength) ? word : getWord(word);
        }

        final long getLong(final ByteBuffer byteBuffer) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            final long word = byteBuffer.getLong(index);
            return (memberBitLength == memberMaxBitLength) ? word : getWord(word);
        }

        final short getShort(final ByteBuffer byteBuffer) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            final int word = byteBuffer.getShort(index);
            return (short) ((memberBitLength == memberMaxBitLength) ? word : getWord(word));
        }

        final void setByte(final ByteBuffer byteBuffer, final byte value) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            if (memberBitLength == memberMaxBitLength) {
                byteBuffer.put(index, value);
            } else {
                byteBuffer.put(index, (byte) setWord(value, byteBuffer.get(index)));
            }
        }

        final void setChar(final ByteBuffer byteBuffer, final char value) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            if (memberBitLength == memberMaxBitLength) {
                byteBuffer.putChar(index, value);
            } else {
                byteBuffer.putChar(index, (char) setWord(value, byteBuffer.getChar(index)));
            }
        }

        final void setInt(final ByteBuffer byteBuffer, final int value) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            if (memberBitLength == memberMaxBitLength) {
                byteBuffer.putInt(index, value);
            } else {
                byteBuffer.putInt(index, setWord(value, byteBuffer.getInt(index)));
            }
        }

        final void setLong(final ByteBuffer byteBuffer, final long value) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            if (memberBitLength == memberMaxBitLength) {
                byteBuffer.putLong(index, value);
            } else {
                byteBuffer.putLong(index,
                        setWord(value, byteBuffer.getLong(index)));
            }
        }

        final void setShort(final ByteBuffer byteBuffer, final short value) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            if (memberBitLength == memberMaxBitLength) {
                byteBuffer.putShort(index, value);
            } else {
                byteBuffer.putShort(index, (short) setWord(value, byteBuffer.getShort(index)));
            }
        }

    }

    /**
     * This class represents a 16 bits signed integer.
     */
    public final class Signed16 extends ScalarMember {

        public Signed16() {
            super(16);
        }

        public Signed16(final int nbrOfBits) {
            super(nbrOfBits, 2);
        }

        public short get() {
            return get(getByteBuffer());
        }

        public short get(final ByteBuffer byteBuffer) {
            return getShort(byteBuffer);
        }

        public void set(final short value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final short value) {
            setShort(byteBuffer, value);
        }

        public String toString() {
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

        public Signed32(final int nbrOfBits) {
            super(nbrOfBits, 4);
        }

        public int get() {
            return get(getByteBuffer());
        }

        public int get(final ByteBuffer byteBuffer) {
            return getInt(byteBuffer);
        }

        public void set(final int value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final int value) {
            setInt(byteBuffer, value);
        }

        public String toString() {
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

        public Signed64(final int nbrOfBits) {
            super(nbrOfBits, 8);
        }

        public long get() {
            return get(getByteBuffer());
        }

        public long get(final ByteBuffer byteBuffer) {
            return getLong(byteBuffer);
        }

        public void set(final long value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final long value) {
            final int index = getAbsolutePosition(byteBuffer) + memberOffset;
            if (memberBitLength == memberMaxBitLength) {
                byteBuffer.putLong(index, value);
            } else {
                byteBuffer.putLong(index,
                        setWord(value, byteBuffer.getLong(index)));
            }
        }

        public String toString() {
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

        public Signed8(final int nbrOfBits) {
            super(nbrOfBits, 1);
        }

        public byte get() {
            return get(getByteBuffer());
        }

        public byte get(final ByteBuffer byteBuffer) {
            return getByte(byteBuffer);
        }

        public void set(final byte value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final byte value) {
            setByte(byteBuffer, value);
        }

        public String toString() {
            return String.valueOf(this.get());
        }
    }

    /**
     * This class represents a UTF-8 character string, null terminated
     * (for C/C++ compatibility)
     */
    public final class UTF8String extends AbstractMember {

        private final int _length;
        private final UTF8ByteBufferReader _reader = new UTF8ByteBufferReader();
        private final UTF8ByteBufferWriter _writer = new UTF8ByteBufferWriter();

        public UTF8String(final int length) {
            super(length << 3, 1);
            _length = length; // Takes into account 0 terminator.
        }

        public String get() {
            return get(getByteBuffer());
        }

        public String get(final ByteBuffer byteBuffer) {
            synchronized (byteBuffer) {
                final StringBuilder tmp = new StringBuilder();
                try {
                    int index = getAbsolutePosition(byteBuffer) + memberOffset;
                    byteBuffer.position(index);
                    _reader.setInput(byteBuffer);
                    for (int i = 0; i < _length; i++) {
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
        }

        public void set(final String value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final String string) {
            synchronized (byteBuffer) {
                try {
                    final int index = getAbsolutePosition(byteBuffer) + memberOffset;
                    byteBuffer.position(index);
                    _writer.setOutput(byteBuffer);
                    if (string.length() < _length) {
                        _writer.write(string);
                        _writer.write(0); // Marks end of string.
                    } else if (string.length() > _length) { // Truncates.
                        _writer.write(string.substring(0, _length));
                    } else { // Exact same length.
                        _writer.write(string);
                    }
                } catch (IOException e) { // Should never happen.
                    throw new Error(e.getMessage());
                } finally {
                    _writer.reset();
                }
            }
        }

        public String toString() {
            return this.get();
        }
    }

    /**
     * This class represents a 16 bits signed integer.
     */
    public final class UTFChar16 extends ScalarMember {

        public UTFChar16() {
            super(16);
        }

        public UTFChar16(final int nbrOfBits) {
            super(nbrOfBits, 2);
        }

        public char get() {
            return get(getByteBuffer());
        }

        public char get(final ByteBuffer byteBuffer) {
            return getChar(byteBuffer);
        }

        public void set(final CharSequence single) {
            set(getByteBuffer(), single.charAt(0));
        }

        public void set(final ByteBuffer byteBuffer, final CharSequence single) {
            set(byteBuffer, single.charAt(0));
        }

        public void set(final char value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final char value) {
            setChar(byteBuffer, value);
        }

        public String toString() {
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

        public UTFChar8(final int nbrOfBits) {
            super(nbrOfBits, 1);
        }

        public char get() {
            return get(getByteBuffer());
        }

        public char get(final ByteBuffer byteBuffer) {
            return (char) (0xFF & getByte(byteBuffer));
        }

        public void set(final CharSequence single) {
            set(getByteBuffer(), single.charAt(0));
        }

        public void set(final ByteBuffer byteBuffer, final CharSequence single) {
            set(byteBuffer, single.charAt(0));
        }

        public void set(final char value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final char value) {
            setByte(byteBuffer, (byte) value);
        }

        public String toString() {
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

        public Unsigned16(final int nbrOfBits) {
            super(nbrOfBits, 2);
        }

        public int get() {
            return get(getByteBuffer());
        }

        public int get(final ByteBuffer byteBuffer) {
            return 0xFFFF & getShort(byteBuffer);
        }

        public void set(final int value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final int value) {
            setShort(byteBuffer, (short) value);
        }

        public String toString() {
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

        public Unsigned32(final int nbrOfBits) {
            super(nbrOfBits, 4);
        }

        public long get() {
            return get(getByteBuffer());
        }

        public long get(final ByteBuffer byteBuffer) {
            return 0xFFFFFFFFL & getInt(byteBuffer);
        }

        public void set(final long value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final long value) {
            setInt(byteBuffer, (int) value);
        }

        public String toString() {
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

        public Unsigned8(final int nbrOfBits) {
            super(nbrOfBits, 1);
        }

        public short get() {
            return get(getByteBuffer());
        }

        public short get(final ByteBuffer byteBuffer) {
            return (short) (0xFF & getByte(byteBuffer));
        }

        public void set(final short value) {
            set(getByteBuffer(), value);
        }

        public void set(final ByteBuffer byteBuffer, final short value) {
            setByte(byteBuffer, (byte) value);
        }

        public String toString() {
            return String.valueOf(this.get());
        }
    }
}
