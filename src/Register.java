import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Register {
    public ArrayList<RegisterEntry> entries;

    private void loadMainTable(Element table) {
        Elements rows = table.getElementsByTag("tr");
        for (Element row : rows) {
            Elements columns = row.getElementsByTag("td");
            if (columns.size() >= 5) {
                RegisterEntry entry = new RegisterEntry();
                entry.studentNumber = columns.get(1).text();
                entry.studentName = columns.get(2).text();
                entry.setSignatureFromString(columns.get(3).text());
                entry.comments = columns.get(4).text();
                entries.add(entry);
            }

            if (columns.size() >= 10) {
                RegisterEntry entry = new RegisterEntry();
                entry.studentNumber = columns.get(6).text();
                entry.studentName = columns.get(7).text();
                entry.setSignatureFromString(columns.get(8).text());
                entry.comments = columns.get(9).text();
                entries.add(entry);
            }
        }
    }

    public Register(File file) throws IOException {
        Document doc = Jsoup.parse(file, "UTF-8");
        Elements tables = doc.getElementsByTag("table");
        if (tables.size() != 2) {
            System.out.println("Unexpected number of tables in the document");
        }

        entries = new ArrayList<>();
        loadMainTable(tables.last());
    }
}
