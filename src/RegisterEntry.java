public class RegisterEntry {
    public String studentNumber;
    public String studentName;
    public boolean signature;
    public String comments;

    public String getSignatureString() {
        if (signature) {
            return "PRESENT";
        } else {
            return "ABSENT";
        }
    }

    public void setSignatureFromString(String string) {
        if (string.contains("PRESENT")) {
            signature = true;
        } else if (string.contains("ABSENT")) {
            signature = false;
        }
    }
}
