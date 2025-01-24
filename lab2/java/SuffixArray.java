
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class SuffixArray {
    String text;
    int[] index;

    Path textFile;
    Path indexFile;

    // Internal constants.
    static final String INDEX_SUFFIX = ".jix";
    static final Charset ENCODING = StandardCharsets.UTF_8;
    static final int GZIP_BUFFERSIZE = 16384;

    public void setText(String text) {
        this.text = text;
        this.textFile = null;
        this.indexFile = null;
        this.index = null;
    }

    public void generateRandomText(int size, String alphabet) {
        Random random = new Random();
        char[] text = new char[size];
        for (int i = 0; i < size; i++) {
            text[i] = alphabet.charAt(random.nextInt(alphabet.length()));
        }
        setText(new String(text));
    }

    public void loadText(String textFile) throws IOException {
        this.textFile = Path.of(textFile);
        this.indexFile = Path.of(textFile + INDEX_SUFFIX);
        if (textFile.endsWith(".gz")) {
            this.text = readGZippedTextfile();
        } else {
            this.text = Files.readString(this.textFile, ENCODING);
        }
        this.index = null;
    }

    private String readGZippedTextfile() throws IOException {
        try (
            InputStream compresesed = Files.newInputStream(this.textFile);
            InputStream uncompressed = new GZIPInputStream(compresesed);
            Reader reader = new InputStreamReader(uncompressed, ENCODING);
            Writer writer = new StringWriter();
        ) {
            char[] buffer = new char[GZIP_BUFFERSIZE];
            int length;
            while ((length = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, length);
            }
            return writer.toString();
        }
    }

    public void loadIndex() throws IOException, ClassNotFoundException {
        try (ObjectInputStream stream = new ObjectInputStream(
                Files.newInputStream(this.indexFile)
            ))
        {
            this.index = (int[]) stream.readObject();
        }
    }

    public int size() {
        return this.text.length();
    }

    public int compareSuffixes(int suffix1, int suffix2) {
        if (suffix1 == suffix2) {
            return 0;
        }
        int end = text.length();
        while (suffix1 < end && suffix2 < end) {
            char ch1 = text.charAt(suffix1);
            char ch2 = text.charAt(suffix2);
            if (ch1 != ch2) {
                return ch1 < ch2 ? -1 : 1;
            }
            suffix1++;
            suffix2++;
        }
        return suffix1 > suffix2 ? -1 : 1;
    }

    public int compareSuffixToValue(int suffix, String value) {
        int end = text.length();
        for (int i = 0; i < value.length(); i++) {
            if (suffix + i >= end) {
                return -1;
            }
            char ch1 = text.charAt(suffix + i);
            char ch2 = value.charAt(i);
            if (ch1 != ch2) {
                return ch1 < ch2 ? -1 : 1;
            }
        }
        return 0;
    }

    public void print(String header) {
        this.print(header, new int[]{0, size()}, "  ");
    }

    public void print(String header, int[] breakpoints, String indicators) {
        this.print(header, breakpoints, indicators, 3, 40);
    }

    public void print(String header, int[] breakpoints, String indicators, int context, int maxSuffix) {
        int digits = Math.max(3, String.valueOf(size()).length());

        System.out.println("--- " + header + " " + "-".repeat(75-header.length()));
        System.out.format("%" + (digits+3) + "s%" + (digits+6) + "s      suffix%n", "index", "textpos");
        String dotdotdot = String.format("%" + (digits+2) + "s%" + (digits+6) + "s", "...", "...");
        int endRange = 0;
        for (int k : breakpoints) {
            int startRange = k - context;
            if (endRange < startRange - 1) {
                System.out.println(dotdotdot);
            } else {
                startRange = endRange;
            }
            endRange = k + context;
            for (int i = startRange; i < endRange; i++) {
                if (0 <= i && i < size()) {
                    char ind = indicators.charAt(0);
                    for (int bp = 0; bp < breakpoints.length; bp++) {
                        if (i >= breakpoints[bp]) 
                            ind = indicators.charAt(bp+1);
                    }
                    int suffixPos = this.index[i];
                    String suffixString = (
                        suffixPos + maxSuffix <= size()
                        ? this.text.substring(suffixPos, suffixPos+maxSuffix) + "..."
                        : this.text.substring(suffixPos)
                    );
                    suffixString = suffixString.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\n");
                    System.out.format(
                        "%c %" + digits + "d  --> %" + digits + "d  -->  %s%n",
                        ind, i, suffixPos, suffixString
                    );
                }
            }
        }
        if (endRange < size()) {
            System.out.println(dotdotdot);
        }
        System.out.println("-".repeat(80));
        System.out.println();
    }
}

