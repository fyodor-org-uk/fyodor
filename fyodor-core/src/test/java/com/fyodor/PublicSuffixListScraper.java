package com.fyodor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class PublicSuffixListScraper {

    public static void main(String[] args) throws IOException {
        PublicSuffixListScraper scraper = new PublicSuffixListScraper();
        scraper.getList();
    }

    public void getList() throws IOException {
        //resource is taken from https://publicsuffix.org/list/effective_tld_names.dat
        URL url = getClass().getResource("/effective_tld_names.dat.txt");
        if (url == null) {
            throw new FileNotFoundException("file not on CLASSPATH");
        }
        BufferedReader br =
                new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        int numberOfSuffixes = 0;//keep track to add a new line every now and then for readability
        for (String line = null; (line = br.readLine()) != null;) {
            if (line.length() <= 0 || line.startsWith("//") || line.startsWith("*")) {
                continue;
            }
            stringBuilder.append("\"").append(line).append("\",");
            numberOfSuffixes++;
            if (numberOfSuffixes > 10) {
                stringBuilder.append("\n");
                numberOfSuffixes = 0;
            }
        }
        System.out.println(stringBuilder.toString());
    }
}
