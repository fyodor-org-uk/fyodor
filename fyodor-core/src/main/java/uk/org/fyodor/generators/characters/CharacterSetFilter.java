package uk.org.fyodor.generators.characters;

public enum CharacterSetFilter {

    AllExceptDoubleQuotes {
        @Override
        CharacterFilter getFilter() {
            return NoDoubleQuotesFilter.getFilter();
        }
    },
    DomainName {
        @Override
        CharacterFilter getFilter() {
            return DomainNameFilter.getFilter();
        }
    },
    EmailLocalPart {
        @Override
        CharacterFilter getFilter() {
            return EmailLocalPartFilter.getFilter();
        }
    },
    LettersAndDigits {
        @Override
        CharacterFilter getFilter() {
            return LettersAndDigitsFilter.getFilter();
        }
    },
    LettersOnly {
        @Override
        CharacterFilter getFilter() {
            return LettersOnlyFilter.getFilter();
        }
    };

    abstract CharacterFilter getFilter();
}
