import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EVisionRegister extends Register {
    private String refNos;
    private String activity;
    private int group;
    private String room;
    private Date startTime, endTime;
    private String[] tutors;

    String getID() {
        return startTime.toString();
    }

    private void loadTopTable(Element table) throws ParseException {
        Elements rows = table.getElementsByTag("tr");
        for (Element row : rows) {
            Elements columns = row.getElementsByTag("td");
            if (columns.size() >= 2) {
                switch (columns.get(0).text()) {
                    case "Module":
                        module = columns.get(1).text();
                        break;
                    case "Ref Nos":
                        refNos = columns.get(1).text();
                        break;
                    case "Activity":
                        activity = columns.get(1).text();
                        break;
                    case "Group":
                        group = Integer.parseInt(columns.get(1).text());
                        break;
                    case "Room":
                        room = columns.get(1).text();
                        break;
                    case "Date / Time":
                        startTime = new SimpleDateFormat("E dd/MMM/yyyy HH:mm").parse(columns.get(1).text());
                        endTime = new SimpleDateFormat("E dd/MMM/yyyy HH:mm - HH:mm").parse(columns.get(1).text());
                        break;
                    case "Tutor":
                        tutors = columns.get(1).html().split("<br>");
                        break;
                    case "Register":
                        break;
                    default:
                        System.out.println("WARNING: Unrecognised property: "+columns.get(0).text());
                }
            }
        }
    }

    private void loadMainTable(Element table) {
        Elements rows = table.getElementsByTag("tr");
        for (Element row : rows) {
            Elements columns = row.getElementsByTag("td");
            if (columns.size() >= 5) {
                entries.add(new RegisterEntry(columns.get(1).text(), columns.get(2).text(), columns.get(3).text(), columns.get(4).html().replaceAll("<([^<]*)>", "")));
            }

            if (columns.size() >= 10) {
                entries.add(new RegisterEntry(columns.get(6).text(), columns.get(7).text(), columns.get(8).text(), columns.get(9).html().replaceAll("<([^<]*)>", "")));
            }
        }
    }

    EVisionRegister(File file) throws IOException, ParseException {
        Document doc = Jsoup.parse(file, "UTF-8");
        Elements tables = doc.getElementsByTag("table");
        if (tables.size() != 2) {
            System.out.println("Unexpected number of tables in the document");
        }

        loadTopTable(tables.first());
        loadMainTable(tables.last());
    }
}
