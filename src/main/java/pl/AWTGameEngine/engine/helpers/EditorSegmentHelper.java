package pl.AWTGameEngine.engine.helpers;

import pl.AWTGameEngine.exceptions.resources.EditorSegmentException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EditorSegmentHelper {

    public static InputStream patchStream(InputStream stream, boolean usingEditor) throws IOException {
        if(usingEditor) {
            return stream;
        }
        String s = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        stream.close();
        String[] split = s.split("\n");
        List<String> patched = new ArrayList<>();
        boolean editor = false;
        for(String line : split) {
            if (line.trim().startsWith("#EDITOR")) {
                if(editor) {
                    throw new EditorSegmentException();
                }
                editor = true;
                continue;
            } else if(line.trim().startsWith("#END")) {
                if(!editor) {
                    throw new EditorSegmentException();
                }
                editor = false;
                continue;
            }
            if(!editor) {
                patched.add(line);
            }
        }
        return new ByteArrayInputStream(String.join("\n", patched).getBytes(StandardCharsets.UTF_8));
    }

}
