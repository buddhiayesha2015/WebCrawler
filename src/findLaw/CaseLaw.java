/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package findLaw;

import controller.CaseController;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Anchor;
import models.AppellateInformation;
import models.Case;
import models.Domain;
import models.FootNotes;
import models.Judge;
import models.WebPage;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pagecollector.PageCollector;

/**
 *
 * @author ASUS-PC
 */
public class CaseLaw {

    private WebPage webPage;
    private List<String> caseList;
    private String detailedCaseLawUrl;

    public CaseLaw(WebPage webPage) throws Exception {

        this.webPage = webPage;

        caseList = new ArrayList<String>();
        detailedCaseLawUrl = "";

        createCaseList(webPage);
        setDetailedCaseLawUrl();

    }

    private void setDetailedCaseLawUrl() throws Exception {

        boolean canProcess = true;
        
        for (String url : caseList) {
            Domain domain = new Domain(url);
            Anchor anchor = new Anchor(domain, url);
            WebPage webPage = new WebPage(anchor);
            webPage.getDocumentFromWeb();

            detailedCaseLawUrl = getDetailedCaseLawUrl(webPage);

            System.out.println(url);
            System.out.println(detailedCaseLawUrl);
            System.out.println("---");
            
            if(detailedCaseLawUrl.equals("http://caselaw.findlaw.com/us-supreme-court/10-779.html"))
                canProcess = true;

            if(canProcess) {
                informationExtraction(url, detailedCaseLawUrl);
            }
        }

    }

    private String getDetailedCaseLawUrl(WebPage webPage) {

        String buttonText = webPage.getDocument().getElementsByClass("btn_read").toString();

        Document document = Jsoup.parse(buttonText);
        Elements options = document.select("a[href]");

        String link = "";

        for (Element element : options) {

            link = (element.attr("href"));

        }
        return link;

    }

    private void createCaseList(WebPage webPage) {
        String caseLawTable = webPage.getDocument().getElementById("srpcaselaw").toString();

        Document document = Jsoup.parse(caseLawTable);
        Elements options = document.select("a[href]");

        for (Element element : options) {

            caseList.add(element.attr("href"));

        }
    }

    private void informationExtraction(String summaryURL, String detailedInformationURL) throws Exception {
        try {
            Case consumerLawCase = new Case();
            ArrayList<AppellateInformation> appellateInformation = new ArrayList<AppellateInformation>();
            ArrayList<FootNotes> footNotes = new ArrayList<FootNotes>();
            ArrayList<Judge> judges = new ArrayList<Judge>();

            String url ="http://caselaw.findlaw.com";
            Domain domain = new Domain(url);

            Anchor summaryAnchor = new Anchor(domain, summaryURL);
            WebPage summaryWebPage = new WebPage(summaryAnchor);
            summaryWebPage.getDocumentFromWeb();
            Document summaryDocument = summaryWebPage.getDocument();

            //get <p> tags in document
            Elements paragraphs = summaryDocument.select("p");

            //iterate through each <p> element.
            for (Element p : paragraphs) {

                if (!p.text().equals(" ")) {
                    consumerLawCase.setSummary(p.text().replace("'", "''")); //extract summary and set it to Case object
                }

            }

            //get <h3> tags in document
            Elements h3s = summaryDocument.select("h3");

            //iterate through each <h3> elements.
            for (Element h3 : h3s) {

                //extract judges information and push them to judges array list
                if (h3.text().equals("Judges")) {
                    if (!h3.nextElementSibling().text().equals("")) { //check whether li tags exists
                        String judgeName = h3.nextElementSibling().select("li").text().replace("'", "''"); // extract judge
                        Judge judge = new Judge();
                        judge.setName(judgeName);
                        judges.add(judge);
                    }

                } else if (h3.text().equals("Appellate Information")) {

                    if (!h3.nextElementSibling().text().equals("")) { //check whether li tags exists
                        //extract li tags for appellate information
                        Elements appellateElements = h3.nextElementSibling().select("li");

                        //iterate through li tags
                        for (Element li : appellateElements) {

                            switch (li.text().split(" ")[0]) {
                                case "Decided":
                                    Date decidedDate = splitAppellatesDates(li.text());
                                    java.sql.Date decidedSqlDate = null;
                                    if(decidedDate != null) {
                                        decidedSqlDate = new java.sql.Date(decidedDate.getTime());
                                        //Adding appellate Inforation Object to Appellate Information Array List
                                        AppellateInformation aI = new AppellateInformation("Decided", decidedSqlDate);
                                        appellateInformation.add(aI);
                                    }
                                    break;

                                case "Submitted":
                                    Date submittedDate = splitAppellatesDates(li.text());
                                    java.sql.Date submittedSqlDate = null;
                                    if(submittedDate != null) {
                                        submittedSqlDate = new java.sql.Date(submittedDate.getTime());
                                        AppellateInformation aI = new AppellateInformation("Submitted", submittedSqlDate);
                                        appellateInformation.add(aI);
                                    }
                                    break;

                                case "Argued":
                                    Date arguedDate = splitAppellatesDates(li.text());
                                    java.sql.Date arguedSqlDate = null;
                                    if(arguedDate != null) {
                                        arguedSqlDate = new java.sql.Date(arguedDate.getTime());
                                        AppellateInformation aI = new AppellateInformation("Argued", arguedSqlDate);
                                        appellateInformation.add(aI);
                                    }
                                    break;

                                case "Published":
                                    Date publishedDate = splitAppellatesDates(li.text());
                                    java.sql.Date publishedSqlDate = null;
                                    if(publishedDate != null) {
                                        publishedSqlDate = new java.sql.Date(publishedDate.getTime());
                                        AppellateInformation aI = new AppellateInformation("Published", publishedSqlDate);
                                        appellateInformation.add(aI);
                                    }
                                    break;

                            }

                        }

                    }

                } else if (h3.text().equals("Court")) {
                    if (!h3.nextElementSibling().text().equals("")) { //check whether li tags exists
                        String court = h3.nextElementSibling().child(0).text();
                        consumerLawCase.setCourt(court.replace("'", "''")); //adding court information to case
                    }

                } else if (h3.text().equals("Counsel")) {
                    if (!h3.nextElementSibling().text().equals("")) { //check whether li tags exists
                        String counsel = h3.nextElementSibling().child(0).text();
                        consumerLawCase.setCounsel(counsel.replace("'", "''")); //adding counsel information to case
                    }

                }
            }

            //----------------------------------------end extracting HTML document from summary page------------------------------
            //----------------------------------------start extracting HTML document from Read page-------------------------------
            Anchor readAnchor = new Anchor(domain, detailedInformationURL);
            WebPage readWebPage = new WebPage(readAnchor);
            readWebPage.getDocumentFromWeb();
            Document readDocument = readWebPage.getDocument();

            if(readDocument != null){
                //get <h3> tags in document
                Elements readDocementH3s = readDocument.select("h3");

                //iterate through h3 tags
                int index = 0;
                for (Element e : readDocementH3s) {
                    String extractedText = e.text();

                    //extract argued data and decided date. ex: Argued: March 30, 2011    Decided: June 24, 2011
                    if (extractedText.startsWith("Argued")) {
                        String[] rawDates = extractedText.split("    ");

                        //parse argued date as Date type
                        DateFormat df = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                        if(!rawDates[0].split(":")[1].trim().equals("")) {
                            Date arguedDate = (Date) df.parse(rawDates[0].split(":")[1].trim());
                            java.sql.Date arguedSqlDate = new java.sql.Date(arguedDate.getTime());
                            consumerLawCase.setArgued_date(arguedSqlDate);
                        }

                        //check whether there is Decided date
                        if (rawDates.length > 1) {
                            //parse decided date as Date type
                            if(!rawDates[1].split(":")[1].trim().equals("")) {
                                Date decidedDate = (Date) df.parse(rawDates[1].split(":")[1].trim());
                                java.sql.Date decidedSqlDate = new java.sql.Date(decidedDate.getTime());
                                consumerLawCase.setDecided_date(decidedSqlDate);
                            }
                        }

                    } else if (extractedText.startsWith("No.")) {
                        //extracting court Number. ex: No. 90-2324
                        String caseNo = extractedText.substring(4);
                        consumerLawCase.setCase_no(caseNo.replace("'", "''"));

                    } else if (index == 0 && extractedText.contains(" v. ")) {
                        //add parties to case
                        String[] partiesDetails = extractedText.split(" v. ");
                        consumerLawCase.setParty_1(partiesDetails[0].replace("'", "''"));

                        if (partiesDetails.length > 1) {
                            consumerLawCase.setParty_2(partiesDetails[1].replace("'", "''"));
                        }
                    }

                    index++;
                }

                //get case information
                Elements readDocementContent = readDocument.select(".caselawcontent .searchable-content").first().children();;
                String content = "";
                boolean isContent = false;

                for (Element e : readDocementContent) {

                    //extract content
                    if (isContent) {
                        content += e.text();
                    } else {
                        //check for FootNotes and if foot note, it will be added to array list
                        if (!e.text().startsWith("Footnote") && !e.text().startsWith("FOOTNOTES") && !e.text().equals("")) {
                            FootNotes fn = new FootNotes();
                            fn.setContent(e.text().replace("'", "''"));
                            footNotes.add(fn);
                        }
                    }

                    //Check whether text is within content Area.
                    if (e.text().equals("FOOTNOTES")) {
                        isContent = false;
                    } else if (e.text().contains("Argued: ")) {
                        isContent = true;
                    }

                }

                consumerLawCase.setContent(content.replace("'", "''"));

                CaseController.addCase(consumerLawCase, footNotes, appellateInformation, judges);
            }
        } catch (HttpStatusException ex) {
            
        } catch (IllegalArgumentException ex) {
            
        } catch(Exception ex) {
            
        }
        
    }
    
    //Split and formating Appellates Dates
    private static Date splitAppellatesDates(String records) {

        String[] dateRecord = records.split(" ");
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

        if (dateRecord.length > 1) {
            try {
                Date result =  (Date) df.parse(dateRecord[1]);
                return result;
            } catch (ParseException ex) {
                Logger.getLogger(PageCollector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

}
