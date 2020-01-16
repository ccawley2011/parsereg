import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class UEALabModule extends Module {
    private String[] header;

    static String[] parse(String row) {
        String[] token = row.split(",");

        boolean quoteDelimited = false;

        int n = 0;

        for (int i = 0; i < token.length; i++) {
            if (token[i].charAt(0) == '\"') {
                String noQuote = token[i].substring(1);
                if (noQuote.contains("\"")) {
                    // non-quote-delimited field
                    token[n++] = noQuote.substring(0, noQuote.lastIndexOf('\"'));
                } else {
                    // start of a quote-delimited field
                    quoteDelimited = true;
                    token[n] = noQuote;
                }
            } else if (quoteDelimited) {
                // middle or end of a quote-delimited field
                token[n] = token[n] + ',' + token[i];

                if (token[i].endsWith("\"")) {
                    token[n] = token[n].substring(0, token[n].lastIndexOf('\"'));
                    quoteDelimited = false;
                    n++;
                }
            } else {
                // non-quote-delimited field
                token[n++] = token[i];
            }
        }

        String[] fields = new String[n];

        for (int i = 0; i < n; i++) {
            fields[i] = token[i];
        }

        return fields;
    }

    private void loadHeader(String _header) {
        header = parse(_header);

        for (int i = 5; i < header.length; i++) {
            String id = header[i];
            registers.add(new Register(id));
        }
    }

    private void loadRow(String row) {
        String[] tokens = parse(row);

        String studentID = tokens[0];
        String studentName = tokens[1];

        System.out.println(studentID + ' ' + studentName);

        for (int i = 5; i < tokens.length; i++) {
            String s = tokens[i];
            addRegisterEntry(header[i], new RegisterEntry(studentID, studentName, s, ""));
        }
    }

    UEALabModule(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        loadHeader(reader.readLine());

        String row;
        while ((row = reader.readLine()) != null) {
            loadRow(row);
        }
    }
}
