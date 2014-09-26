package uk.org.fyodor.random;

public interface RandomValues {

    long randomLong(long lower, long upper);

    int randomInteger(int upper);

    int randomInteger(int lower, int upper);

    boolean randomBoolean();

    double randomDouble(double lower, double upper);

    double randomDouble(double lower, double upper, int scale);
}
