package uk.org.fyodor.generators;

public class UrlGenerator implements Generator<String> {

    Generator<String> protocolGenerator = RDG.value("http://", "https://", "http://www.", "https://www.");
    Generator<String> domainGenerator = RDG.domain();
    Generator<String> suffixGenerator = RDG.domainSuffix();

    public String next() {
        return protocolGenerator.next() +
                domainGenerator.next() +
                "." +
                suffixGenerator.next();
    }
}
