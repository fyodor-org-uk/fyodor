package uk.org.fyodor.generators.characters;

import uk.org.fyodor.range.Range;

public enum CharacterSetRange {

    defaultLatinBasic {
        @Override
        Range<Integer> getRange() {
            return Range.closed(33, 126);
        }
    },
    latin1 {
        @Override
        Range<Integer> getRange() {
            return Range.closed(160, 255);
        }
    },
    latinExtendedA {
        @Override
        Range<Integer> getRange() {
            return Range.closed(256, 383);
        }
    },
    latinExtendedB {
        @Override
        Range<Integer> getRange() {
            return Range.closed(384, 591);
        }
    };

    abstract Range<Integer> getRange();
}
