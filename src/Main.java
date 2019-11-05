import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length <= 0) {
            System.out.println("Invalid number of arguments");
            return;
        }

        ArrayList<Register> registers = new ArrayList<Register>();
        HashMap<String, String> students = new HashMap<String, String>();
        HashMap<String, HashMap<Date, RegisterEntry>> attendance = new HashMap<String, HashMap<Date, RegisterEntry>>();
        for (int i = 0; i < args.length; i++) {
            File input = new File(args[i]);
            Register register = new Register(input);
            registers.add(register);

            for (RegisterEntry entry : register.entries) {
                if (!students.containsKey(entry.studentNumber)) {
                    students.put(entry.studentNumber, entry.studentName);
                    attendance.put(entry.studentNumber, new HashMap<Date, RegisterEntry>());
                }
                attendance.get(entry.studentNumber).put(register.startTime, entry);
            }
        }

        FileWriter output = new FileWriter("output.csv");
        output.write("Student ID,Student Name");
        for (Register register : registers) {
            output.write(",\"" + register.startTime + "\"");
        }
        output.write('\n');

        for (Map.Entry me : attendance.entrySet()) {
            output.write("\"" + me.getKey() + "\",\"" + students.get(me.getKey())+"\"");
            HashMap<Date, RegisterEntry> lectures = (HashMap<Date, RegisterEntry>) me.getValue();
            for (Register register : registers) {
                if (lectures.containsKey(register.startTime)) {
                    RegisterEntry registerEntry = lectures.get(register.startTime);
                    if (!registerEntry.comments.isEmpty()) {
                        output.write(",NOT EXPECTED");
                    } else {
                        output.write("," + lectures.get(register.startTime).getSignatureString());
                    }
                } else {
                    output.write(",UNKNOWN");
                }
            }
            output.write('\n');
        }
        output.close();
    }
}
