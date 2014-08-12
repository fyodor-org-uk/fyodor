package com.fyodor.generators;

public class StringGenerator implements Generator<String> {

    private Integer length;
    public static char[] allChars;
    public static char[] letterAndNumbers;
    public static char[] lettersOnly;
    char[] charSet;

    public enum CharSet {
        AllChars(allChars),
        LettersAndNumbers(letterAndNumbers),
        LettersOnly(lettersOnly);

        private final char[] charset;

        CharSet(char[] charset) {
            this.charset = charset;
        }

        char[] getCharset() {
            return charset;
        }
    }

    static {
        allChars = new char[94];
        letterAndNumbers = new char[62];
        lettersOnly = new char[52];
        int j = 0, k = 0, l = 0;
        for (int i = 33; i <= 126; i++) {
            allChars[j] = (char) i;
            j++;
            if (Character.isLetterOrDigit(i)) {
                letterAndNumbers[k] = (char) i;
                k++;
            }
            if (Character.isLetter(i)) {
                lettersOnly[l] = (char) i;
                l++;
            }
        }
    }

    public StringGenerator(Integer length) {
        this(length, CharSet.AllChars);
    }

    public StringGenerator(Integer length, CharSet charSet) {
        this.length = length;
        this.charSet = charSet.getCharset();
    }

    @Override
    public String next() {
        char[] ret = new char[length];
        for (int i = 0; i < length; i++) {
            ret[i] = charSet[RDG.random.nextInt(charSet.length)];
        }
        return String.valueOf(ret);
    }
}
