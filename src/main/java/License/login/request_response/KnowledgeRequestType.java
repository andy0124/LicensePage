package License.login.request_response;

import java.util.Arrays;

public enum KnowledgeRequestType {
    Project("project"), Category("category"), Channel("channel"),
    DialogTask("dialogTask"), Synomym("synonym"),
    TriggeringSentence("triggeringSentence"), TriggeringPattern("triggeringPattern"),
    TestSentence("testSentence"),ValidationSentence("validationSentence"),
    User("user"),
    test("test"),
    Undefined("UNDEFINED");

    private String value;


    KnowledgeRequestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] strValues() {
        KnowledgeRequestType[] arr = KnowledgeRequestType.values();
        String[] strArray = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            strArray[i] = arr[i].value;
        }
        return strArray;
    }

    public static KnowledgeRequestType fromText(String text) {
        return Arrays.stream(values())
                .filter(bl -> bl.value.equalsIgnoreCase(text))
                .findFirst()
                .orElse(Undefined);
    }
}
