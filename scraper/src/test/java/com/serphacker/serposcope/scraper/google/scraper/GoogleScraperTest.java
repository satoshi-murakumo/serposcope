/*
 * Serposcope - SEO rank checker https://serposcope.serphacker.com/
 *
 * Copyright (c) 2016 SERP Hacker
 * @author Pierre Nogues <support@serphacker.com>
 * @license https://opensource.org/licenses/MIT MIT License
 */
package com.serphacker.serposcope.scraper.google.scraper;

import static com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.serphacker.serposcope.scraper.google.GoogleCountryCode;
import com.serphacker.serposcope.scraper.google.GoogleDevice;
import com.serphacker.serposcope.scraper.google.GoogleScrapSearch;
import com.serphacker.serposcope.scraper.http.ScrapClient;

/**
 *
 * @author admin
 */
@RunWith(MockitoJUnitRunner.class)
public class GoogleScraperTest {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleScraperTest.class);

    public GoogleScraperTest() {
    }

    @Test
    public void testBuildUrl() {
        GoogleScraper scraper = new GoogleScraper(null, null);

        GoogleScrapSearch search = null;
        String url = null;

        search = new GoogleScrapSearch();
        search.setKeyword("keyword");
        assertEquals("https://www.google.com/search?q=keyword", scraper.buildRequestUrl(search, 0));

        search = new GoogleScrapSearch();
        search.setKeyword("keyword");
        assertEquals("https://www.google.com/search?q=keyword&start=10", scraper.buildRequestUrl(search, 1));

        search = new GoogleScrapSearch();
        search.setKeyword("keyword");
        search.setDatacenter("10.0.0.1");
        assertEquals("https://www.google.com/search?q=keyword", scraper.buildRequestUrl(search, 0));
    }

    @Test
    public void testParseSerpEmpty() throws IOException {
        ScrapClient http = mock(ScrapClient.class);
        when(http.getContentAsString()).thenReturn("");

        GoogleScraper scraper = new GoogleScraper(http, null);

        GoogleScrapSearch search = new GoogleScrapSearch();
        search.setDevice(GoogleDevice.DESKTOP);
        scraper.prepareParser(search);

        assertEquals(ERROR_NETWORK, scraper.parseSerp(new ArrayList<>()));
    }

    @Test
    public void testLastPage() throws IOException {
        String[] files = new String[]{
            "last-page-com-desktop",
            "last-page-com-smartphone",
            "last-page-fr-desktop",
            "last-page-fr-smartphone"
        };

        for (String file : files) {
            LOG.debug("checking " + file);
            String content = new String(ByteStreams.toByteArray(ClassLoader.class.getResourceAsStream("/serps/lastpage/" + file)));
            ScrapClient http = mock(ScrapClient.class);
            when(http.getContentAsString()).thenReturn(content);
            GoogleScraper scraper = new GoogleScraper(http, null);

            GoogleScrapSearch search = new GoogleScrapSearch();
            search.setDevice(GoogleDevice.DESKTOP);
            scraper.prepareParser(search);

            assertEquals(OK, scraper.parseSerp(new ArrayList<>()));
            assertFalse(scraper.hasNextPage());
        }
    }

    @Test
    @Ignore
    public void testParsing() throws Exception {

        File files = new File(ClassLoader.class.getResource("/serps/").toURI());
        for (File testFile : files.listFiles()) {

            if(testFile.isDirectory() || testFile.getName().endsWith(".res")){
                continue;
            }

            LOG.info("checking {}", testFile);

            String testContent = new String(Files.readAllBytes(testFile.toPath()));
            ScrapClient http = mock(ScrapClient.class);
            when(http.getContentAsString()).thenReturn(testContent);
            GoogleScraper scraper = new GoogleScraper(http, null);
            GoogleScrapSearch search = new GoogleScrapSearch();
            search.setDevice(GoogleDevice.DESKTOP);
            scraper.prepareParser(search);
            List<String> urls = new ArrayList<>();
            assertEquals(OK, scraper.parseSerp(urls));
            assertTrue(scraper.hasNextPage());

            File resFile = new File(testFile.toString() + ".res");
            assertTrue(resFile.exists());

            List<String> expectedUrls = Arrays.asList(new String(Files.readAllBytes(resFile.toPath())).split("\n"));
            assertEquals(expectedUrls, urls);
        }

    }

    @Test
    public void testParsingJaSmartPhone() throws Exception {

        File files = new File(ClassLoader.class.getResource("/serps_ja/smartphone/").toURI());
        for (File testFile : files.listFiles()) {

            if(testFile.isDirectory() || testFile.getName().endsWith(".res")){
                continue;
            }

            LOG.info("checking {}", testFile);

            String testContent = new String(Files.readAllBytes(testFile.toPath()));
            ScrapClient http = mock(ScrapClient.class);
            when(http.getContentAsString()).thenReturn(testContent);
            GoogleScraper scraper = new GoogleScraper(http, null);
            GoogleScrapSearch search = new GoogleScrapSearch();
            search.setDevice(GoogleDevice.SMARTPHONE);
            scraper.prepareParser(search);
            List<String> urls = new ArrayList<>();
            assertEquals(OK, scraper.parseSerp(urls));
            assertTrue(scraper.hasNextPage());

            File resFile = new File(testFile.toString() + ".res");
            assertTrue(resFile.exists());

            List<String> expectedUrls = Arrays.asList(new String(Files.readAllBytes(resFile.toPath())).split("\n"));
            assertEquals(expectedUrls, urls);
        }
    }

    @Test
    public void testParsingJaDesktop() throws Exception {

        File files = new File(ClassLoader.class.getResource("/serps_ja/desktop/").toURI());
        for (File testFile : files.listFiles()) {

            if(testFile.isDirectory() || testFile.getName().endsWith(".res")){
                continue;
            }

            LOG.info("checking {}", testFile);

            String testContent = new String(Files.readAllBytes(testFile.toPath()));
            ScrapClient http = mock(ScrapClient.class);
            when(http.getContentAsString()).thenReturn(testContent);
            GoogleScraper scraper = new GoogleScraper(http, null);
            GoogleScrapSearch search = new GoogleScrapSearch();
            search.setDevice(GoogleDevice.DESKTOP);
            scraper.prepareParser(search);
            List<String> urls = new ArrayList<>();
            assertEquals(OK, scraper.parseSerp(urls));
            assertTrue(scraper.hasNextPage());

            File resFile = new File(testFile.toString() + ".res");
            assertTrue(resFile.exists());

            List<String> expectedUrls = Arrays.asList(new String(Files.readAllBytes(resFile.toPath())).split("\n"));
            assertEquals(expectedUrls, urls);
        }
    }

    @Test
    public void testDownloadNetworkError() throws Exception {
        ScrapClient http = mock(ScrapClient.class);
        when(http.get(any(), any())).thenReturn(-1);

        GoogleScrapSearch search = new GoogleScrapSearch();
        search.setKeyword("suivi de position");
        search.setCountry(GoogleCountryCode.FR);

        GoogleScraper scraper = new GoogleScraper(http, null);
        assertEquals(ERROR_NETWORK, scraper.scrap(search).status);
    }

    @Test
    public void testParseNetworkError() throws Exception {
        ScrapClient http = mock(ScrapClient.class);
        when(http.get(any(), any())).thenReturn(200);
        when(http.getContentAsString()).thenReturn("");

        GoogleScrapSearch search = new GoogleScrapSearch();
        search.setKeyword("suivi de position");
        search.setCountry(GoogleCountryCode.FR);

        GoogleScraper scraper = new GoogleScraper(http, null);
        assertEquals(ERROR_NETWORK, scraper.scrap(search).status);
    }


    @Test
    public void testBuildUule() {
        GoogleScraper scraper = new GoogleScraper(null, null);
        assertEquals("w+CAIQICIpTW9udGV1eCxQcm92ZW5jZS1BbHBlcy1Db3RlIGQnQXp1cixGcmFuY2U",
            scraper.buildUule("Monteux,Provence-Alpes-Cote d'Azur,France").replaceAll("=+$", ""));
        assertEquals("w+CAIQICIGRnJhbmNl",
            scraper.buildUule("France").replaceAll("=+$", ""));
        assertEquals("w+CAIQICIlQ2VudHJlLVZpbGxlLENoYW1wYWduZS1BcmRlbm5lLEZyYW5jZQ",
            scraper.buildUule("Centre-Ville,Champagne-Ardenne,France").replaceAll("=+$", ""));
        assertEquals("w+CAIQICIfTGlsbGUsTm9yZC1QYXMtZGUtQ2FsYWlzLEZyYW5jZQ",
            scraper.buildUule("Lille,Nord-Pas-de-Calais,France").replaceAll("=+$", ""));
    }

    @Test
    public void extractResults(){
        GoogleScraper scraper = new GoogleScraper(null, null);
//        assertEquals(2490l, scraper.extractResultsNumber("Environ 2 490 résultats"));
//        assertEquals(25270000000l, scraper.extractResultsNumber("Page&nbsp;10 sur environ 25&nbsp;270&nbsp;000&nbsp;000&nbsp;résultats<nobr> (0,46&nbsp;secondes)&nbsp;</nobr>"));
//        assertEquals(25270000000l, scraper.extractResultsNumber("Page 10 of about 25,270,000,000 results<nobr> (0.42 seconds)&nbsp;</nobr>"));
//        assertEquals(25270000000l, scraper.extractResultsNumber("About 25,270,000,000 results<nobr> (0.28 seconds)&nbsp;</nobr>"));
//        assertEquals(225000l, scraper.extractResultsNumber("About 225,000 results<nobr> (0.87 seconds)&nbsp;</nobr>"));
//        assertEquals(225000l, scraper.extractResultsNumber("Page 5 of about 225,000 results (0.45 seconds) "));
    }

}
