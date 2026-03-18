package org.game;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;

final class Utils {

    public static void copy(Object src, Object dst) throws Exception {
        Field[] fields = src.getClass().getFields();

        for(Field field : fields) {
            Class<?> cls = field.getType();
            int m = field.getModifiers();

            if(Modifier.isPublic(m) && !Modifier.isStatic(m)) {
                if(
                    int.class.isAssignableFrom(cls) ||
                    float.class.isAssignableFrom(cls) ||
                    double.class.isAssignableFrom(cls) ||
                    boolean.class.isAssignableFrom(cls) ||
                    Boolean.class.isAssignableFrom(cls) ||
                    Integer.class.isAssignableFrom(cls) ||
                    Float.class.isAssignableFrom(cls) ||
                    Double.class.isAssignableFrom(cls) ||
                    String.class.isAssignableFrom(cls) || 
                    cls.isEnum()
                ) {
                    field.set(dst, field.get(src));
                } else if(Vec2.class.isAssignableFrom(cls)) {
                    Vec2 v1 = (Vec2)field.get(src);
                    Vec2 v2 = (Vec2)field.get(dst);

                    v2.set(v1);
                } else if(Vec3.class.isAssignableFrom(cls)) {
                    Vec3 v1 = (Vec3)field.get(src);
                    Vec3 v2 = (Vec3)field.get(dst);

                    v2.set(v1);
                } else if(Vec4.class.isAssignableFrom(cls)) {
                    Vec4 v1 = (Vec4)field.get(src);
                    Vec4 v2 = (Vec4)field.get(dst);

                    v2.set(v1);
                }
            }
        }
    }

    public static String string(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getField(fieldName);
        int m = field.getModifiers();
        String s = null;

        if(Modifier.isPublic(m) && !Modifier.isStatic(m)) {
            obj = field.get(obj);
            if(obj != null) {
                s = string(obj);
            }
        }
        return s;
    }

    public static String string(Object obj) {
        Class<?> cls = obj.getClass();
        String s = null;

        if(obj instanceof String) {
            s = (String)obj;
        } else if(obj instanceof Boolean) {
            s = "" + (Boolean)obj;
        } else if(obj instanceof Integer) {
            s = "" + (Integer)obj;
        } else if(obj instanceof Float) {
            s = "" + (Float)obj;
        } else if(obj instanceof Double) {
            s = "" + (Double)obj;
        } else if(Vec2.class.isAssignableFrom(cls)) {
            Vec2 v = (Vec2)obj;

            s = v.toString();
        } else if(Vec3.class.isAssignableFrom(cls)) {
            Vec3 v = (Vec3)obj;

            s = v.toString();
        } else if(Vec4.class.isAssignableFrom(cls)) {
            Vec4 v = (Vec4)obj;

            s = v.toString();
        } else if(cls.isEnum()) {
            s = obj.toString();
        } else if(UIEnum.class.isAssignableFrom(cls)) {
            s = "" + ((UIEnum)obj).getValue();
        }
        return s;
    }
    
    public static void parse(Object obj, String fieldName, String text) throws Exception {
        Field field = obj.getClass().getField(fieldName);
        int m = field.getModifiers();

        if(Modifier.isPublic(m) && !Modifier.isStatic(m)) {
            Object o = field.get(obj);
            if(o != null) {
                o = parse(o, text);
                if(!Modifier.isFinal(m)) {
                    field.set(obj, o);
                }
            }
        }
    }

    public static Object parse(Object obj, String text) throws Exception {
        Class<?> cls = obj.getClass();

        if(Integer.class.isAssignableFrom(cls)) {
            obj = Integer.parseInt(text);
        } else if(Float.class.isAssignableFrom(cls)) {
            obj = Float.parseFloat(text);
        } else if(Double.class.isAssignableFrom(cls)) {
            obj = (float)(Double.parseDouble(text));
        } else if(Boolean.class.isAssignableFrom(cls)) {
            obj = Boolean.parseBoolean(text);
        } else if(String.class.isAssignableFrom(cls)) {
            obj = text;
        } else if(Vec2.class.isAssignableFrom(cls)) {
            Vec2.parse(text, (Vec2)obj);
        } else if(Vec3.class.isAssignableFrom(cls)) {
            Vec3.parse(text, (Vec3)obj);
        } else if(Vec4.class.isAssignableFrom(cls)) {
            Vec4.parse(text, (Vec4)obj);
        } else if(cls.isEnum()) {
            Object[] constants = cls.getEnumConstants();
            for(int i = 0; i != constants.length; i++) {
                String ename = constants[i].toString();
                if(ename.equals(text)) {
                    obj = constants[i];
                    break;
                }
            }
        } else if(UIEnum.class.isAssignableFrom(cls)) {
            try {
                ((UIEnum)obj).setValue(Integer.parseInt(text));
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        } else {
            obj = null;
        }
        return obj;
    }

    public static void appendFiles(File directory, String extension, Vector<File> paths) {
        File[] files = directory.listFiles();

        if(files != null) {
            for(File file : files) {
                if(file.isFile() && IO.getExtension(file).equals(extension)) {
                    paths.add(file);
                } 
            }
            for(File file : files) {
                if(file.isDirectory()) {
                    appendFiles(file, extension, paths);
                }
            }
        }
    }
}
