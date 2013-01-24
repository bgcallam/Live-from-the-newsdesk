/* Daniel Shiffman               */
/* Programming from A to Z       */
/* Spring 2006                   */
/* http://www.shiffman.net       */
/* daniel.shiffman@nyu.edu       */

// Simple example of a web crawler
// URL queue: linked list
// Sites already visited: hash table

// Needs to be updated to comply with ROBOTS.TXT!

import java.util.*;
import java.util.regex.*;

public class Crawler {

  private LinkedList urlsToVisit;	// A queue of URLs to visit
  private HashMap urlsVisited;		// A table of already visited URLs
  private Pattern href, imgRegex;	// A Pattern to match an href tag

  private String rootRegex, rootSite;

  private NetImage netImg;
  private CrawlUrl crawled;

  public Crawler(String rtSite, String type) {
    urlsToVisit = new LinkedList();
    urlsVisited = new HashMap();
    
    this.crawled = new CrawlUrl();
    /*href = Pattern.compile(	"href	# match href \n" + 
     "\\s*=\\s*\"			# 0 or more spaces, =, 0 ore more spaces, quote \n" + 
     "(http[^\"\\s]*)	# capture the URL itself, http followed by no spaces and no quotes \n" +
     "\"							# ending with a quote \n",    Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);*/
    href = Pattern.compile( "href\\s*=\\s*\"([^\\s]+(?<!rss|png|css|mov|jpg|gif|pdf|xml|css|;|/))\"",    Pattern.CASE_INSENSITIVE);    // Grabs whats between the quotes for href
    imgRegex = Pattern.compile("<img(.*?)>", Pattern.CASE_INSENSITIVE);                // Grabs everything in the img tag

    // CNN.com
    //if(type=="alt"){ 
    this.netImg = new AltImage();
    //}

    this.rootSite = "http://www." + rtSite;
    this.rootRegex = ".*?" + rtSite.replaceAll("[\\.]","\\\\.") + ".*";
    //println("Root regex: " + rootRegex);
    addUrl(rootSite);
    
  } // END CONSTRUCTOR

  public void addUrl(String urlpath) {
    // Add it to both the LinkedList and the HashMap
    urlsToVisit.add(urlpath);
    urlsVisited.put(urlpath,urlpath);
  }

  // A method to determine if the queue is empty or not
  public boolean queueEmpty() {
    return urlsToVisit.isEmpty();
  }

  // Crawl one URL and return the image info
  public NetImage crawl() {
    String urlpath = (String) urlsToVisit.removeFirst();      // Get a URL in the queue
    read(urlpath);             // Read that Url
    //println("The path to read: " + urlpath);
    netImg.reset();            // OK to reset here b/c the variable doesn't matter 
    crawled.reset();
    return netImg;
  }

  /*--------  A method to read a URL and look for other URLs  ----------/
   /-------------------------------------------------------------------*/
  private void read(String urlpath) {
    //println(urlsVisited.size() + " " + urlsToVisit.size() + " " + urlpath);
    try {
      // Grab the URL content from out Parser
      PageParser urlr = new PageParser(urlpath);
      String pageContent = urlr.getContent();

      // Match the URL pattern to the content
      Matcher m = href.matcher(pageContent);
      //println(pageContent);
      // While there are URLs
      while (m.find()) {
        // Grab the captured part of the regex (the URLPath itself)
        String newurl = m.group(1);
        crawled.processUrl(newurl, rootSite );
        newurl = crawled.getUrl();
        // If it hasn't already been visited or if its an external link (or if it matches the ignore pattern)
        if (!urlsVisited.containsKey(newurl) && newurl.matches(rootRegex)) {
          //println("crawling... " + newurl);
          addUrl(newurl);
          //println("Added...");
        }
      }

      Matcher mI = imgRegex.matcher(pageContent);
      // While there are Images
      while (mI.find()){
        String newImage =  mI.group(1);
        //println("Image grab: " + newImage);
        netImg.parseImage(newImage);
      }
    } 
    catch (Exception e) {
      System.out.println("Problem reading from " + crawled.getUrl() + " " + e);
      //e.printStackTrace();
    }
  }
} // End Crawler class


/************************************************************
 * Class to manage urls and url selection
 *   - this will only take one URL from the page to follow
 ***********************************************************/
class CrawlUrl {
  private String currentUrl, tempUrl;
  private Pattern appendUrls;
  //private String ignoreHref;		                // To be used as a regex for ignoring media files (JPG,MOV, etc.) in the url

  public CrawlUrl() {
    this.appendUrls = Pattern.compile("\\w?^([^http].*)\\w?",    Pattern.CASE_INSENSITIVE);   // this will only grab urls that don't have "http://" already
    this.currentUrl = "";
  }

  public void processUrl(String unParsed, String rtUrl){
    Matcher q = appendUrls.matcher(unParsed);
    if(q.find()){
      String appender = q.group(1);
      tempUrl = rtUrl + appender;
    } 
    else {
      tempUrl = unParsed;
    }
    currentUrl = tempUrl;
    // Randomize this a bit to decide if we want to keep it
   /*  float r = random(5);
     int rx = round(r);
     if(currentUrl==""){
     currentUrl = tempUrl;
     }
     else if(rx<2){ 
     currentUrl = tempUrl;
     }*/
  }

  public String getUrl(){
    return currentUrl; 
  }

  public void reset(){
    currentUrl = "";
  }
} // End CrawlUrl class
