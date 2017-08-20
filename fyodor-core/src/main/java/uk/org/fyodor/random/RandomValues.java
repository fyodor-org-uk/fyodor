package uk.org.fyodor.random;

public interface RandomValues {

    boolean randomBoolean();

    int randomInteger(int lower, int upper);

    long randomLong(long lower, long upper);

    double randomDouble(double lower, double upper);

    double randomDouble(double lower, double upper, int scale);

    byte randomByte(byte lower, byte upper);

    byte[] randomBytes(int length);

    short randomShort(short lower, short upper);
}
