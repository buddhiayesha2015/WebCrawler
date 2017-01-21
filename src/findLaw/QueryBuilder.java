/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package findLaw;

import models.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;
import models.Anchor;
import models.Domain;

/**
 *
 * @author ASUS-PC
 */
public class QueryBuilder {

    private WebPage webpage;

    private List<String> courtArray;
    private List<String> topicArray;
    private List<String> queryArray;

    public List<String> getQueryArray() {
        return queryArray;
    }

    public QueryBuilder(WebPage webpage) throws Exception {
        this.webpage = webpage;

        courtArray = new ArrayList<String>();
        topicArray = new ArrayList<String>();
        queryArray = new ArrayList<String>();

        createCourtList(webpage);
        createTopicList(webpage);
        createQueryList();

        String url = "http://caselaw.findlaw.com/summary/search?court=us-supreme-court&topic=cs_1";
        Domain domain = new Domain(url);
        Anchor anchor = new Anchor(domain, url);
        WebPage webPage = new WebPage(anchor);
        webPage.getDocumentFromWeb();

        Pagination queryBuilder = new Pagination(webPage);

    }

    private void createCourtList(WebPage webpage) {
        String courtSelect = webpage.getDocument().getElementById("court").toString();
        Document document = Jsoup.parse(courtSelect);
        Elements options = document.select("select > option");

        for (Element element : options) {
            if (!element.attr("value").isEmpty()) {
                courtArray.add(element.attr("value"));
            }

        }
    }

    private void createTopicList(WebPage webpage) {
        String topicSelect = webpage.getDocument().getElementById("topic").toString();
        Document document = Jsoup.parse(topicSelect);
        Elements options = document.select("select > option");

        for (Element element : options) {
            if (!element.attr("value").isEmpty()) {
                topicArray.add(element.attr("value"));
            }

        }
    }

    private void createQueryList() {
        String domainUrl = "http://caselaw.findlaw.com/summary/search";

        for (String court : courtArray) {
            for (String topic : topicArray) {

                String query = domainUrl + "?" + "court="
                        + court + "&" + "topic="
                        + topic;
                queryArray.add(query);

            }
        }
    }

    public void getPageNumbers(String url) {

    }

}
