import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length <= 0) {
            System.out.println("Invalid number of arguments");
            return;
        }

        File input = new File(args[0]);
        Register register = new Register(input);
        System.out.println("Module: " + register.module);
        System.out.println("Ref Nos: " + register.refNos);
        System.out.println("Activity: " + register.activity);
        System.out.println("Group: " + register.group);
        System.out.println("Room: " + register.room);
        System.out.println("Date / Time: " + register.startTime + " until " + register.endTime);
        System.out.println("Tutors: " + Arrays.toString(register.tutors));
        for (RegisterEntry entry : register.entries) {
            System.out.printf("Name =  %s, Number = %s, Signature = %s, Comment = %s\n", entry.studentName, entry.studentNumber, entry.getSignatureString(), entry.comments);
        }
    }
}
