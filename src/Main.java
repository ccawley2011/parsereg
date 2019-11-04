import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            System.out.println("Invalid number of arguments");
        }

        File input = new File(args[0]);
        Register register = new Register(input);
        for (RegisterEntry entry : register.entries) {
            System.out.printf("Name =  %s, Number = %s, Signature = %s, Comment = %s\n", entry.studentName, entry.studentNumber, entry.getSignatureString(), entry.comments);
        }
    }
}
