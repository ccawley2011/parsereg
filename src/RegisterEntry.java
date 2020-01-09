class RegisterEntry {
    String studentNumber;
    String studentName;
    Signature signature;
    String comments;

    enum Signature {
        UNKNOWN,
        PRESENT,
        ABSENT,
        NOT_EXPECTED
    }

    String getSignatureString(boolean checkComment) {
        if (signature == Signature.NOT_EXPECTED || (checkComment && !comments.isEmpty())) {
            return "NOT EXPECTED";
        } else if (signature == Signature.PRESENT) {
            return "PRESENT";
        } else if (signature == Signature.ABSENT) {
            return "ABSENT";
        } else  {
            return "UNKNOWN";
        }
    }

    void setSignatureFromString(String string) {
        if (string.contains("PRESENT")) {
            signature = Signature.PRESENT;
        } else if (string.contains("ABSENT")) {
            signature = Signature.ABSENT;
        } else {
            signature = Signature.UNKNOWN;
        }
    }
}
