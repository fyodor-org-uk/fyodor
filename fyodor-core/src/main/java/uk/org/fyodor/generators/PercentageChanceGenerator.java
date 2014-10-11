package uk.org.fyodor.generators;

public class PercentageChanceGenerator implements Generator<Boolean> {

    private final int chance;
    private final Generator<Integer> generator = RDG.integer(100);

    public PercentageChanceGenerator(int chance) {
        if (chance < 1 || chance > 99) {
            throw new IllegalArgumentException("chance must be between 1 and 99");
        }
        this.chance = chance;
    }

    @Override
    public Boolean next() {
        return generator.next() <= chance;
    }
}
