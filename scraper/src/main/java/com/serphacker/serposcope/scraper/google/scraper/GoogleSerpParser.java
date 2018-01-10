package com.serphacker.serposcope.scraper.google.scraper;

import java.util.List;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status;

public interface GoogleSerpParser {

    Status parseSerp(String html, List<String> urls);

    long parseResultsNumberOnFirstPage();

    boolean hasNextPage();

}