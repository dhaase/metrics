/*
 * Javolution - Java(TM) Solution for Real-Time and Embedded Systems
 * Copyright (C) 2012 - Javolution (http://javolution.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package eu.dirk.haase.type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
 * <p> Applications can work with the raw {@link #structByteBuffer bytes}
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
    private static final int INITIAL_ABSOLUTE_POSITION = -1;
    private final List<PositionUpdatable> memberList;
    private final ByteOrder structByteOrder;
    private final List<StructMember> structMember;
    private final int structOffset;
    /**
     * Indicates if the index has to be reset for each new field (
     * <code>true</code> only for Union subclasses).
     */
    boolean structResetIndex;
    /**
     * Holds the index position during construction.
     * This is the index a the first unused byte available.
     */
    private int currStructIndex;
    private int structAbsolutePosition = INITIAL_ABSOLUTE_POSITION;
    /**
     * Holds the byte buffer backing the struct (top struct).
     */
    private ByteBuffer structByteBuffer;
    /**
     * Holds this struct's length.
     */
    private int structLength;

    /**
     * Default constructor.
     */
    protected Struct() {
        this(ByteOrder.nativeOrder(), null);
    }

    /**
     * Default constructor.
     */
    protected Struct(final AbstractMember afterMember) {
        this(ByteOrder.nativeOrder(), afterMember);
    }

    /**
     * Default constructor.
     */
    protected Struct(final ByteOrder byteOrder) {
        this(byteOrder, null);
    }

    /**
     * Default constructor.
     */
    protected Struct(final ByteOrder byteOrder, final AbstractMember afterMember) {
        this.structOffset = (afterMember == null ? 0 : afterMember.memberOffset + afterMember.memberLength);
        this.structResetIndex = isUnion();
        this.structByteOrder = byteOrder;
        this.memberList = new LinkedList<>();
        this.structMember = new LinkedList<>();
        registerMember(this);
    }

    @Override
    public final int absolutePosition() {
        return this.structAbsolutePosition;
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
            currStructIndex = 0;
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
            currStructIndex = 0;
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
            currStructIndex = 0;
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
            currStructIndex = 0;
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
            currStructIndex = 0;
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
            currStructIndex = 0;
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
    protected final Utf8String[] array(final Utf8String[] array, final int stringLength) {
        final boolean resetIndexSaved = structResetIndex;
        if (structResetIndex) {
            currStructIndex = 0;
            structResetIndex = false; // Ensures the array elements are sequential.
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = new Utf8String(stringLength);
        }
        structResetIndex = resetIndexSaved;
        return array;
    }

    public final ByteOrder byteOrder() {
        return structByteOrder;
    }

    private final void calcAbsolutePosition() {
        for (int i = 0; memberList.size() > i; ++i) {
            memberList.get(i).updateAbsolutePosition();
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        } else {
            final Struct struct = (Struct) o;
            return Objects.equals(memberList, struct.memberList);
        }
    }

    /**
     * Returns the absolute byte position of this struct within its associated
     * {@link #structByteBuffer byte buffer}.
     *
     * @return the relative position of this struct (can be an inner struct)
     * in the byte buffer.
     */
    public final int getStructAbsolutePosition() {
        return this.structAbsolutePosition;
    }

    public final void setStructAbsolutePosition(final int offset) {
        for (StructMember sm : this.structMember) {
            sm.innerStruct.setStructAbsolutePosition(offset);
        }
        this.structAbsolutePosition = offset;
        calcAbsolutePosition();
    }

    @Override
    public final int hashCode() {
        return Objects.hash(memberList);
    }

    /**
     * Sets the current byte buffer for this struct.
     * The specified byte buffer can be mapped to memory for direct memory
     * access or can wrap a shared byte array for I/O purpose
     * (e.g. <code>DatagramPacket</code>).
     * The capacity of the specified byte buffer should be at least the
     * {@link Struct#size() size} of this struct plus the offset position.
     *
     * @param byteBuffer       the new byte buffer.
     * @param absolutePosition the position of this struct in the specified byte buffer.
     * @return <code>this</code>
     * @throws IllegalArgumentException      if the specified byteBuffer has a
     *                                       different byte order than this struct.
     * @throws UnsupportedOperationException if this struct is an inner struct.
     * @see #structByteOrder
     */
    public final void initByteBuffer(final ByteBuffer byteBuffer, final int absolutePosition) {
        setByteBuffer(byteBuffer);
        setStructAbsolutePosition(absolutePosition);
    }

    /**
     * Defines the specified struct as inner of this struct.
     *
     * @param <S>         Type of the Inner Struct
     * @param innerStruct the inner struct.
     * @return the specified struct.
     * @throws IllegalArgumentException if the specified struct is already
     *                                  an inner struct.
     */
    protected final <S extends Struct> S inner(final S innerStruct) {
        this.structMember.add(new StructMember(this, innerStruct));
        return innerStruct;
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

    private void registerMember(final PositionUpdatable positionUpdatable) {
        this.memberList.add(positionUpdatable);
    }

    public final void setByteBuffer(final ByteBuffer byteBuffer) {
        for (StructMember sm : this.structMember) {
            sm.innerStruct.setByteBuffer(byteBuffer);
        }
        if (byteBuffer.order() != this.structByteOrder) {
            throw new IllegalArgumentException(
                    "The byte order of the specified byte buffer"
                            + " is different from this struct byte order");
        }
        this.structByteBuffer = byteBuffer;
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
    @Override
    public final String toString() {
        final String prefix = "Struct(" + System.identityHashCode(this) + ")";
        if ((this.structAbsolutePosition == Struct.INITIAL_ABSOLUTE_POSITION) || (this.structByteBuffer == null)) {
            return prefix;
        }
        final int size = size();
        final int start = getStructAbsolutePosition();
        final StringBuilder tmp = new StringBuilder(size * 6 + 20);
        tmp.append(prefix);
        tmp.append('{');
        for (int i = 0; i < size; i++) {
            int b = this.structByteBuffer.get(start + i) & 0xFF;
            tmp.append(HEX_CHAR[b >> 4]);
            tmp.append(HEX_CHAR[b & 0xF]);
            tmp.append(((i & 0xF) == 0xF) ? '\n' : ' ');
        }
        tmp.append('}');
        return tmp.toString();
    }

    @Override
    public final void updateAbsolutePosition() {
        this.structAbsolutePosition = getStructAbsolutePosition();
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
    public abstract class AbstractMember implements PositionUpdatable {

        /**
         * Holds the byte length of this member.
         */
        final int memberLength;
        /**
         * Holds the relative offset (in bytes) of this member within its struct.
         */
        final int memberOffset;

        int memberAbsolutePosition = Struct.INITIAL_ABSOLUTE_POSITION;

        /**
         * Base constructor for custom member types.
         * <p>
         * The word size can be zero, in which case the {@link #memberOffset}
         * of the member does not change.
         *
         * @param byteSize the size in bytes used when accessing
         *                 this member data or <code>0</code> if the data is accessed
         *                 at the bit level.
         */
        protected AbstractMember(final int byteSize) {
            registerMember();

            this.memberLength = byteSize;

            // Resets index if union.
            if (Struct.this.structResetIndex) {
                Struct.this.currStructIndex = 0;
            }

            // Sets member indices.
            memberOffset = Struct.this.currStructIndex + Struct.this.structOffset;

            // Update struct indices.
            Struct.this.currStructIndex += byteSize;
            Struct.this.structLength = Math.max(Struct.this.structLength, Struct.this.currStructIndex);
            // size and index may differ because of {@link Union}
        }

        @Override
        public final int absolutePosition() {
            return this.memberAbsolutePosition;
        }

        public final int length() {
            return memberLength;
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

        protected void registerMember() {
            Struct.this.registerMember(this);
        }

        @Override
        public void updateAbsolutePosition() {
            this.memberAbsolutePosition = Struct.this.getStructAbsolutePosition() + this.memberOffset;
        }

    }

    /**
     * This class represents an arbitrary size (unsigned) bit field with
     * no word size constraint (they can straddle words boundaries).
     */
    public final class BitField extends NonScalarMember {

        private final int memberBitLength;

        public BitField(final int nbrOfBits) {
            super(nbrOfBits / 8);
            this.memberBitLength = nbrOfBits;
            if ((nbrOfBits % 8) != 0) {
                throw new IllegalArgumentException("Number of bits (" +
                        nbrOfBits + ") must an integer multiple of 8.");
            }
        }

        public final byte[] get() {
            final byte[] bitFieldBytes = new byte[memberBitLength / 8];
            for (int i = 0; bitFieldBytes.length > i; ++i) {
                bitFieldBytes[i] = structByteBuffer.get(this.memberAbsolutePosition + i);
            }
            return bitFieldBytes;
        }

        public final void set(final byte[] value) {
            for (int i = 0; value.length > i; ++i) {
                structByteBuffer.put(this.memberAbsolutePosition + i, value[i]);
            }
        }

        public final String toBinaryString() {
            final StringBuffer sb = new StringBuffer(memberBitLength + length());

            for (int i = 0; (memberBitLength / 8) > i; ++i) {
                byte value = structByteBuffer.get(this.memberAbsolutePosition + i);
                for (int j = 0; j < 8; j++) {
                    sb.append(((value & 1) == 1) ? '1' : '0');
                    value >>= 1;
                }
                sb.append('-');
            }

            if (sb.charAt(sb.length() - 1) == '-') {
                sb.deleteCharAt(sb.length() - 1);
            }

            return sb.toString();
        }

        @Override
        public final String toString() {
            if ((memberAbsolutePosition == Struct.INITIAL_ABSOLUTE_POSITION) || (Struct.this.structByteBuffer == null)) {
                return "";
            }
            return toBinaryString();
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

        public void negate() {
            final short value = Struct.this.structByteBuffer.getShort(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putShort(this.memberAbsolutePosition, (byte) (value != 0 ? 0 : 1));
        }

        public final void set(final boolean value) {
            final short boolValue = (short) (value ? 1 : 0);
            setShort(boolValue);
        }

        public final void set(final short value) {
            set(value != 0);
        }

        @Override
        public final Object valueObj() {
            return get();
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

        public void negate() {
            final int value = Struct.this.structByteBuffer.getInt(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putInt(this.memberAbsolutePosition, (byte) (value != 0 ? 0 : 1));
        }

        public final void set(final boolean value) {
            final int boolValue = (value ? 1 : 0);
            setInt(boolValue);
        }

        public final void set(final int value) {
            set(value != 0);
        }

        @Override
        public final Object valueObj() {
            return get();
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

        public void negate() {
            final long value = Struct.this.structByteBuffer.getLong(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putLong(this.memberAbsolutePosition, (byte) (value != 0 ? 0L : 1L));
        }

        public final void set(final boolean value) {
            final long boolValue = (value ? 1L : 0L);
            setLong(boolValue);
        }

        public final void set(final long value) {
            set(value != 0);
        }

        @Override
        public final Object valueObj() {
            return get();
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

        public void negate() {
            final byte value = Struct.this.structByteBuffer.get(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.put(this.memberAbsolutePosition, (byte) (value != 0 ? 0 : 1));
        }

        public final void set(final byte value) {
            set(value != 0);
        }

        public final void set(final boolean value) {
            final byte boolValue = (byte) (value ? 1 : 0);
            setByte(boolValue);
        }

        @Override
        public final Object valueObj() {
            return get();
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


        @Override
        public final Object valueObj() {
            return get();
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


        @Override
        public final Object valueObj() {
            return get();
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


        @Override
        public final Object valueObj() {
            return get();
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


        @Override
        public final Object valueObj() {
            return get();
        }
    }

    /**
     * This class represents a 32 bits float (C/C++/Java <code>float</code>).
     */
    public final class Float32 extends ScalarMember {

        public Float32() {
            super(32);
        }

        public void add(float delta) {
            final float value = Struct.this.structByteBuffer.getFloat(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putFloat(this.memberAbsolutePosition, (value + delta));
        }

        public void decrement() {
            final float value = Struct.this.structByteBuffer.getFloat(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putFloat(this.memberAbsolutePosition, (value - 1f));
        }

        public final float get() {
            return structByteBuffer.getFloat(this.memberAbsolutePosition);
        }

        public void increment() {
            final float value = Struct.this.structByteBuffer.getFloat(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putFloat(this.memberAbsolutePosition, (value + 1f));
        }

        public final void set(final float value) {
            structByteBuffer.putFloat(this.memberAbsolutePosition, value);
        }

        @Override
        public final Object valueObj() {
            return get();
        }
    }

    /**
     * This class represents a 64 bits float (C/C++/Java <code>double</code>).
     */
    public final class Float64 extends ScalarMember {

        public Float64() {
            super(64);
        }

        public void add(double delta) {
            final double value = Struct.this.structByteBuffer.getDouble(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putDouble(this.memberAbsolutePosition, (value + delta));
        }

        public void decrement() {
            final double value = Struct.this.structByteBuffer.getDouble(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putDouble(this.memberAbsolutePosition, (value - 1d));
        }

        public final double get() {
            return structByteBuffer.getDouble(this.memberAbsolutePosition);
        }

        public void increment() {
            final double value = Struct.this.structByteBuffer.getDouble(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putDouble(this.memberAbsolutePosition, (value + 1d));
        }

        public final void set(final double value) {
            structByteBuffer.putDouble(this.memberAbsolutePosition, value);
        }

        @Override
        public final Object valueObj() {
            return get();
        }
    }

    public abstract class NonScalarMember extends AbstractMember {

        public NonScalarMember(int wordSize) {
            super(wordSize);
        }

    }

    public abstract class ScalarMember extends AbstractMember {

        /**
         * Base constructor for custom member types.
         * <p>
         * The word size can be zero, in which case the {@link #memberOffset}
         * of the member does not change.
         *
         * @param bitLength the number of bits or <code>0</code>
         *                  to force next member on next word boundary.
         */
        protected ScalarMember(final int bitLength) {
            super(bitLength / 8);
        }

        @Override
        public final boolean equals(Object thatObj) {
            if (this == thatObj) {
                return true;
            } else if (thatObj == null || getClass() != thatObj.getClass()) {
                return false;
            } else {
                final ScalarMember that = (ScalarMember) thatObj;
                return Objects.equals(this.valueObj(), that.valueObj());
            }
        }

        final byte getByte() {
            return Struct.this.structByteBuffer.get(this.memberAbsolutePosition);
        }

        final void setByte(final byte value) {
            Struct.this.structByteBuffer.put(this.memberAbsolutePosition, value);
        }

        final char getChar() {
            return Struct.this.structByteBuffer.getChar(this.memberAbsolutePosition);
        }

        final void setChar(final char value) {
            Struct.this.structByteBuffer.putChar(this.memberAbsolutePosition, value);
        }

        final int getInt() {
            return Struct.this.structByteBuffer.getInt(this.memberAbsolutePosition);
        }

        final void setInt(final int value) {
            Struct.this.structByteBuffer.putInt(this.memberAbsolutePosition, value);
        }

        final long getLong() {
            return Struct.this.structByteBuffer.getLong(this.memberAbsolutePosition);
        }

        final void setLong(final long value) {
            Struct.this.structByteBuffer.putLong(this.memberAbsolutePosition, value);
        }

        final short getShort() {
            return Struct.this.structByteBuffer.getShort(this.memberAbsolutePosition);
        }

        final void setShort(final short value) {
            Struct.this.structByteBuffer.putShort(this.memberAbsolutePosition, value);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(this.valueObj());
        }

        @Override
        public final String toString() {
            if ((memberAbsolutePosition == Struct.INITIAL_ABSOLUTE_POSITION) || (Struct.this.structByteBuffer == null)) {
                return "0";
            }
            final Object valueObj = this.valueObj();
            return (valueObj == null ? super.toString() : String.valueOf(valueObj));
        }

        public abstract Object valueObj();

    }

    /**
     * This class represents a 16 bits signed integer.
     */
    public final class Signed16 extends ScalarMember {

        public Signed16() {
            super(16);
        }

        public void add(short delta) {
            final short value = Struct.this.structByteBuffer.getShort(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putShort(this.memberAbsolutePosition, (short) (value + delta));
        }

        public void decrement() {
            final short value = Struct.this.structByteBuffer.getShort(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putShort(this.memberAbsolutePosition, (short) (value - 1));
        }

        public final short get() {
            return getShort();
        }

        public void increment() {
            final short value = Struct.this.structByteBuffer.getShort(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putShort(this.memberAbsolutePosition, (short) (value + 1));
        }

        public final void set(final short value) {
            setShort(value);
        }

        @Override
        public final Object valueObj() {
            return get();
        }
    }

    /**
     * This class represents a 32 bits signed integer.
     */
    public final class Signed32 extends ScalarMember {


        public Signed32() {
            super(32);
        }

        public void add(int delta) {
            final int value = Struct.this.structByteBuffer.getInt(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putInt(this.memberAbsolutePosition, (value + delta));
        }

        public void decrement() {
            final int value = Struct.this.structByteBuffer.getInt(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putInt(this.memberAbsolutePosition, (value - 1));
        }

        public final int get() {
            return getInt();
        }

        public void increment() {
            final int value = Struct.this.structByteBuffer.getInt(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putInt(this.memberAbsolutePosition, (value + 1));
        }

        public final void set(final int value) {
            setInt(value);
        }


        @Override
        public final Object valueObj() {
            return get();
        }
    }

    /**
     * This class represents a 64 bits signed integer.
     */
    public class Signed64 extends ScalarMember {

        public Signed64() {
            super(64);
        }

        public void add(long delta) {
            final long value = Struct.this.structByteBuffer.getLong(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putLong(this.memberAbsolutePosition, (value + delta));
        }

        public void decrement() {
            final long value = Struct.this.structByteBuffer.getLong(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putLong(this.memberAbsolutePosition, (value - 1L));
        }

        public final long get() {
            return getLong();
        }

        public void increment() {
            final long value = Struct.this.structByteBuffer.getLong(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.putLong(this.memberAbsolutePosition, (value + 1L));
        }

        public final void set(final long value) {
            setLong(value);
        }

        @Override
        public final Object valueObj() {
            return get();
        }
    }

    /**
     * This class represents a 8 bits signed integer.
     */
    public final class Signed8 extends ScalarMember {

        public Signed8() {
            super(8);
        }

        public void add(byte delta) {
            final byte value = Struct.this.structByteBuffer.get(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.put(this.memberAbsolutePosition, (byte) (value + delta));
        }

        public void decrement() {
            final byte value = Struct.this.structByteBuffer.get(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.put(this.memberAbsolutePosition, (byte) (value - 1));
        }

        public final byte get() {
            return getByte();
        }

        public void increment() {
            final byte value = Struct.this.structByteBuffer.get(this.memberAbsolutePosition);
            Struct.this.structByteBuffer.put(this.memberAbsolutePosition, (byte) (value + 1));
        }

        public final void set(final byte value) {
            setByte(value);
        }

        @Override
        public final Object valueObj() {
            return get();
        }
    }

    public final class StructMember extends AbstractMember {

        final Struct innerStruct;
        final Struct outerStruct;

        StructMember(final Struct outerStruct, final Struct innerStruct) {
            super(innerStruct.size());
            this.outerStruct = outerStruct;
            this.innerStruct = innerStruct;
            registerInnerMember();
        }

        private void registerInnerMember() {
            for (final PositionUpdatable pu : innerStruct.memberList) {
                outerStruct.registerMember(pu);
            }
        }

        @Override
        protected void registerMember() {
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


        @Override
        public final Object valueObj() {
            return get();
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


        @Override
        public final Object valueObj() {
            return get();
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


        @Override
        public final Object valueObj() {
            return get();
        }
    }

    /**
     * This class represents a UTF-8 character string, null terminated
     * (for C/C++ compatibility)
     */
    public final class Utf8String extends NonScalarMember {


        public Utf8String(final int length) {
            super(length + 1);
        }

        public final CharSequence get() {
            final StringBuilder sb = new StringBuilder(memberLength);
            final int maxIndex = this.memberAbsolutePosition + memberLength;
            for (int i = this.memberAbsolutePosition; maxIndex > i; ++i) {
                final byte charByte = structByteBuffer.get(i);
                if (charByte > 0) {
                    sb.append((char) charByte);
                } else {
                    break;
                }
            }
            return sb;
        }

        public final void set(final CharSequence string) {
            final int minLength = Math.min(this.memberLength - 1, string.length());
            for (int i = 0; minLength > i; ++i) {
                structByteBuffer.put(this.memberAbsolutePosition + i, (byte) string.charAt(i));
            }
            structByteBuffer.put(minLength + 1, (byte) 0);
        }

        @Override
        public final String toString() {
            if ((memberAbsolutePosition == Struct.INITIAL_ABSOLUTE_POSITION) || (Struct.this.structByteBuffer == null)) {
                return "";
            }
            return this.get().toString();
        }
    }

    /**
     * This class represents a 16 bits signed integer.
     */
    public final class UtfChar16 extends ScalarMember {

        public UtfChar16() {
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


        @Override
        public final Object valueObj() {
            return get();
        }
    }

    /**
     * This class represents a 8 bits unsigned integer.
     */
    public final class UtfChar8 extends ScalarMember {

        public UtfChar8() {
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


        @Override
        public final Object valueObj() {
            return get();
        }
    }
}
