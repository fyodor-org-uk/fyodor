package uk.org.fyodor.generators.characters;

public enum CharacterSetFilter {

    AllExceptDoubleQuotes {
        @Override
        public CharacterFilter getFilter() {
            return NoDoubleQuotesFilter.getFilter();
        }
    },
    DomainName {
        @Override
        public CharacterFilter getFilter() {
            return DomainNameFilter.getFilter();
        }
    },
    EmailLocalPart {
        @Override
        public CharacterFilter getFilter() {
            return EmailLocalPartFilter.getFilter();
        }
    },
    LettersAndDigits {
        @Override
        public CharacterFilter getFilter() {
            return LettersAndDigitsFilter.getFilter();
        }
    },
    LettersOnly {
        @Override
        public CharacterFilter getFilter() {
            return LettersOnlyFilter.getFilter();
        }
    };

    public abstract CharacterFilter getFilter();
}
