package org.game;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

public class Log {
    
    public static int level = 2;
    
    static boolean paused = false;

    public static void put(int level, Object value) {
        if(level > Log.level || paused) {
            return;
        }

        UIGameEditor editor = UIGameEditor.getInstance();

        if(editor != null) {
            JTextArea area = editor.getConsoleArea();

            if(value == null) { 
                area.append("null\n");
            } else if(value instanceof Exception) {
                try {
                    ByteArrayOutputStream out = null;
        
                    try {
                        PrintStream stream = null;

                        out = new ByteArrayOutputStream(1000);
                        try {
                            ((Exception)value).printStackTrace(stream = new PrintStream(out));

                            area.append(new String(out.toByteArray()) + "\n");
                        } finally {
                            if(stream != null) {
                                stream.close();
                            }
                        }
                    } finally {
                        if(out != null) {
                            out.close();
                        }
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                area.append(value + "\n");
            }
            area.setCaretPosition(area.getDocument().getLength());
        } 
        if(value == null) {
            System.out.println("null");
        } else if(value instanceof Exception) {
            ((Exception)value).printStackTrace();
        } else {
            System.out.println(value);
        }
    }
}
