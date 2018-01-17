package com.serphacker.serposcope.scraper.google.scraper;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status;

public class GoogleSmartPhoneSerpParser implements GoogleSerpParser {

    private Document lastSerpHtml;

    /* (非 Javadoc)
     * @see com.serphacker.serposcope.scraper.google.scraper.GoogleSerpParser#parseSerp(java.lang.String, java.util.List)
     */
    @Override
    public Status parseSerp(String html, List<String> urls) {
        if (html == null || html.isEmpty()) {
            return Status.ERROR_NETWORK;
        }

        lastSerpHtml = Jsoup.parse(html);
        if (lastSerpHtml == null) {
            return Status.ERROR_NETWORK;
        }

        Elements aElts = lastSerpHtml.select("a._Olt, a._wSg, g-inner-card a:not(._Qwu)");
        for (Element aElt : aElts) {

            String link = extractLink(aElt);
            if (link != null) {
                urls.add(link);
            }
        }

        return Status.OK;
    }

    /* (非 Javadoc)
     * @see com.serphacker.serposcope.scraper.google.scraper.GoogleSerpParser#parseResultsNumberOnFirstPage()
     */
    @Override
    public long parseResultsNumberOnFirstPage() {
        if (lastSerpHtml == null) {
            return 0;
        }

        Element resultstStatsDiv = lastSerpHtml.getElementById("resultStats");
        if (resultstStatsDiv == null) {
            return 0;
        }

        return extractResultsNumber(resultstStatsDiv.html());
    }

    /* (非 Javadoc)
     * @see com.serphacker.serposcope.scraper.google.scraper.GoogleSerpParser#hasNextPage()
     */
    @Override
    public boolean hasNextPage() {
        if (lastSerpHtml == null) {
            return false;
        }

        return lastSerpHtml.getElementById("pnnext") != null;
    }

    protected String extractLink(Element element) {
        if (element == null) {
            return null;
        }

        String amp = element.attr("data-amp");
        if (amp != null && !amp.isEmpty()) {
            return amp;
        }

        String attr = element.attr("href");
        if (attr == null) {
            return null;
        }

        if ((attr.startsWith("http://www.google") || attr.startsWith("https://www.google"))) {
            if (attr.contains("/aclk?")) {
                return null;
            }
        }

        if (attr.startsWith("http://") || attr.startsWith("https://")) {
            return attr;
        }

        if (attr.startsWith("/url?")) {
            try {
                List<NameValuePair> parse = URLEncodedUtils.parse(attr.substring(5), Charset.forName("utf-8"));
                Map<String, String> map = parse.stream()
                        .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
                return map.get("q");
            } catch (Exception ex) {
                return null;
            }
        }

        return null;
    }

    protected long extractResultsNumber(String html) {
        if (html == null || html.isEmpty()) {
            return 0;
        }
        html = html.replaceAll("\\(.+\\)", "");
        html = html.replaceAll("[^0-9]+", "");
        if (!html.isEmpty()) {
            return Long.parseLong(html);
        }
        return 0;
    }
}
