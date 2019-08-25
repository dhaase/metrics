package eu.dirk.haase.type;

import java.lang.reflect.Field;

public class PrintStruct {


    private static String indentSpace(int indent) {
        String indentStr = "";
        for (int i = 0; indent > i; ++i) {
            indentStr += "   ";
        }
        return indentStr;
    }

    public static void members(int indent, Struct struct) throws IllegalAccessException {
        int offset = struct.getAbsolutePosition(struct.getByteBuffer());
        String indentStr = indentSpace(indent);
        for (Field f : struct.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object value = f.get(struct);
            if (Struct.class.isAssignableFrom(f.getType())) {
                print(indent, (Struct) value);
            } else if (f.getType().isArray()) {
                Object[] array = (Object[]) value;
                Struct.AbstractMember member = (Struct.AbstractMember) array[0];
                System.out.println(indentStr + f.getName() + "[" + array.length + "]: " + (offset + member.memberOffset));
            } else {
                Struct.AbstractMember member = (Struct.AbstractMember) value;
                System.out.println(indentStr + f.getName() + ": " + (offset + member.memberOffset));
            }
        }
    }

    public static void print(int indent, Struct struct) throws IllegalAccessException {
        String indentStr = indentSpace(indent);
        System.out.println(indentStr + struct.getClass().getSimpleName() + " { " + struct.getAbsolutePosition(struct.getByteBuffer()));
        members(++indent, struct);
        System.out.println(indentStr + "} ");
    }


}
