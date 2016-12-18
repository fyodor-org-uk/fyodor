package uk.org.fyodor.generators;

import uk.org.fyodor.generators.characters.CharacterFilter;
import uk.org.fyodor.generators.characters.CharacterSetFilter;

public class PostcodeGenerator implements Generator<String> {

    private static Generator<String> letter = RDG.string(1, CharacterSetFilter.LettersOnly);
    private static Generator<String> letterExceptIOrZ = RDG.string(1, new LetterExceptIOrZFilter());
    private static Generator<Integer> number = RDG.integer(9);
    private Generator<String> inwardCode = new InwardCodeGenerator();
    private Generator<Boolean> girobankChanceGenerator = RDG.percentageChanceOf(1);
    private Generator<OutwardCodeOptions> outwardCodeOptionsGenerator = RDG.value(OutwardCodeOptions.values());

    public String next() {
        return girobankChanceGenerator.next() ? "GIR 0AA" :
                outwardCodeOptionsGenerator.next().getGenerator().next() +
                        " " +
                        inwardCode.next();
    }

    private enum OutwardCodeOptions {
        twoLettersOneNumberOneLetter {
            @Override
            OutwardCodeOption getGenerator() {
                return new TwoLettersOneNumberAndOneLetterOption();
            }
        },
        oneLetterOneNumberOneLetter {
            @Override
            OutwardCodeOption getGenerator() {
                return new OneLetterOneNumberOneLetterOption();
            }
        },
        twoLettersOneOrTwoNumbers {
            @Override
            OutwardCodeOption getGenerator() {
                return new TwoLettersAndOneOrTwoNumbersOption();
            }
        },
        oneLetterOneOrTwoNumbers {
            @Override
            OutwardCodeOption getGenerator() {
                return new OneLetterAndOneOrTwoNumbersOption();
            }
        };

        abstract OutwardCodeOption getGenerator();
    }

    private interface OutwardCodeOption extends Generator<String> {
    }

    private static class InwardCodeGenerator implements Generator<String> {
        @Override
        public String next() {
            return number.next() + letter.next() + letter.next();
        }
    }

    private static class OneLetterOneNumberOneLetterOption implements OutwardCodeOption {
        @Override
        public String next() {
            return letter.next() + number.next() + letter.next();
        }
    }

    private static class TwoLettersAndOneOrTwoNumbersOption implements OutwardCodeOption {
        @Override
        public String next() {
            return letter.next() +
                    letterExceptIOrZ.next() +
                    number.next() +
                    (RDG.bool().next() ? number.next() : "");
        }
    }

    private static class TwoLettersOneNumberAndOneLetterOption implements OutwardCodeOption {

        @Override
        public String next() {
            return letter.next() +
                    letterExceptIOrZ.next() +
                    number.next() +
                    letter.next();
        }
    }

    private static class OneLetterAndOneOrTwoNumbersOption implements OutwardCodeOption {
        @Override
        public String next() {
            return letter.next() + number.next() + (RDG.bool().next() ? number.next() : "");
        }
    }

    private static class LetterExceptIOrZFilter implements CharacterFilter {
        @Override
        public boolean includeCharacter(char c) {
            return CharacterSetFilter.LettersOnly.getFilter().includeCharacter(c) &&
                    !"I".equalsIgnoreCase(String.valueOf(c)) &&
                    !"Z".equalsIgnoreCase(String.valueOf(c));
        }
    }
}
