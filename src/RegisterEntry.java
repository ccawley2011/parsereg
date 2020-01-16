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

    RegisterEntry(String _studentNumber, String _studentName, Signature _signature, String _comments) {
        studentNumber = _studentNumber;
        studentName = _studentName;
        signature = _signature;
        comments = _comments;
    }

    RegisterEntry(String _studentNumber, String _studentName, String _signature, String _comments) {
        studentNumber = _studentNumber;
        studentName = _studentName;
        setSignatureFromString(_signature);
        comments = _comments;
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
        if (string.contains("PRESENT") || string.equals("1")) {
            signature = Signature.PRESENT;
        } else if (string.contains("ABSENT") || string.equals("0")) {
            signature = Signature.ABSENT;
        } else {
            signature = Signature.UNKNOWN;
        }
    }
}
