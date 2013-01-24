import processing.core.*; import processing.opengl.*; import javax.media.opengl.*; import javax.media.opengl.glu.*; import damkjer.ocd.*; import java.util.*; import java.util.regex.*; import java.net.*; import java.io.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; public class ColorSpace extends PApplet {




/***************************************************
 * Benjamin Callam 5/13/08
 * 
 ***************** SETTINGS ************************/
int windowWidth = screen.width;       // dimensions of the window
int windowHeight =screen.height;
int imageWidth = windowWidth/2;       // dimesions of the camera capture
int imageHeight = windowHeight/2;
int cellsize = 40;      // size of the cells
int framerate = 30;     // frames per second
boolean renderOut = false;
boolean fogToggle = true;  // control with 'f' key
boolean blendToggle = false; // control with 'b' key
PFont font_1 = createFont("SFSquareHeadExtended", 10);
PFont font_2 = createFont("SFSquareHeadExtended", 24);
float[] fogColor = { 
  1.0f, 1.0f, 1.0f, 1.0f};
/***************************************************/
PImage a;
int numPixels;
Camera camera1;
Crawler newsCrawler;
Stage theStage;


// ***************************************
// Run in fullscreen NOTE: the name below has to be the same as the app
static public void main(String args[]) {
  PApplet.main(new String[] { 
    "--present", "ColorSpace"   }
  );
}

public void setup() {
  size(windowWidth, windowHeight, OPENGL);
  background(0);
  lights();
  noStroke();
  newsCrawler = new Crawler("cnn.com","alt");   // Initialize with the root domain
  theStage = new Stage();
  //newsCrawler = new Crawler("msnbc.msn.com","alt");

  //camera1 = new Camera(this,windowWidth/2,windowHeight/2,0);
  //numPixels = a.pixels.length;
  //plotColor();
  // noLoop();
}


public void draw() {
 /* lights();
  //directionalLight(250, 250, 250, -1, -1, -1);
  background(0);
  //plotColor();
  //camera1.feed();
  camera();
  ((PGraphicsOpenGL)g).gl.glClear(GL.GL_DEPTH_BUFFER_BIT); 
  theStage.drawStage();*/


  // Start crawling! (this should likely be its own thread.)
  if(!newsCrawler.queueEmpty() ) { //&& theStage.isOpen()) {
    NetImage toLoad =  newsCrawler.crawl();
    PImage b;
    String loading = toLoad.getUrl();
   textFont(font_1);
    if(loading.length()!=0){
      println("####  Loading: " + loading);
      b = loadImage(loading);
      float randX = random(windowWidth);
      float randY = random(windowHeight);
      image(b,randX,randY);
      text(toLoad.getTitle(),randX,randY);
     //theStage.addToStage(b);
    }
  }
}

public void keyPressed(){
  /* if(key == CODED) { 
   if (keyCode == RIGHT) { 
   camera1.arc(radians(2));
   } 
   else if (keyCode == LEFT) { 
   camera1.arc(radians(-2));
   } 
   if (keyCode == UP) { 
   camera1.boom(5);
   } 
   else if (keyCode == DOWN) { 
   camera1.boom(-5);
   }  
   }
   if (key =='='){
   camera1.zoom(radians(-2));
   }*/
  if(keyCode == ESC) {
    exit();
  }
}

/* Daniel Shiffman               */
/* Programming from A to Z       */
/* Spring 2006                   */
/* http://www.shiffman.net       */
/* daniel.shiffman@nyu.edu       */

// Simple example of a web crawler
// URL queue: linked list
// Sites already visited: hash table

// Needs to be updated to comply with ROBOTS.TXT!




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

class Stage {
  private float scaleFactor, yscaleFactor,xscaleFactor, thirdX, thirdY;
  private float q1,q2,q3,q4,qA;
  private int screenHeight, screenWidth;
  private LinkedList imageQueue;

  public Stage(){
    this.scaleFactor = 7;
    this.screenHeight = screen.height;
    this.screenWidth = screen.width;
    this.yscaleFactor = screen.height/scaleFactor;
    this.xscaleFactor = screen.width-screen.height-yscaleFactor;
    this.thirdX = xscaleFactor/3;
    this.thirdY = yscaleFactor/3;
    imageQueue= new LinkedList();
    rectMode(CORNER);
  }

  public void drawStage(){

    drawQueue();
    int fillColor = 200;
    fill(fillColor,fillColor,fillColor);
    // horizontal dashboard
    for(int i=0;i<scaleFactor-2;i++){
      rect(0.0f, i*yscaleFactor, xscaleFactor, yscaleFactor-thirdY*2);
    }
    rect(0.0f, (scaleFactor*yscaleFactor)-thirdY, xscaleFactor, yscaleFactor-thirdY*2);
    // sides of the dashboard
    rect(0.0f, 0.0f, xscaleFactor/scaleFactor, screenHeight);
    rect(xscaleFactor-(xscaleFactor/scaleFactor), 0.0f, xscaleFactor/scaleFactor, screenHeight);    
  }

  // argument tells whether to shift or not
  private void drawQueue(){
    for(int i=0;i<imageQueue.size();i++){
      if(i==4){
        image((PImage)imageQueue.get(i),(xscaleFactor/scaleFactor),i*((3*thirdY))+thirdY,yscaleFactor*3,yscaleFactor*3);
 
        //plotColor((PImage)imageQueue.get(i));
      } 
      else {
        image((PImage)imageQueue.get(i),(xscaleFactor/scaleFactor),i*((3*thirdY))+thirdY,(xscaleFactor-((xscaleFactor/scaleFactor)*2)),thirdY*2 );
      }
    }
  }

  public boolean isOpen(){
    if(imageQueue.size()<5) return true;
    else return false;
  }

  public boolean addToStage(PImage pImg){
    if(isOpen()){
      return imageQueue.add(pImg); 
    } 
    else return false;
  }

  public void plotColor(PImage a) {
    //println("pixels =" +numPixels);
    for (int i = 0; i < numPixels; i+=50) { // For each pixel in the video frame...

      // Fetch the current color in that location, and also the color
      // of the background in that spot
      int currColor = a.pixels[i];
      // Extract the red, green, and blue components of the current pixel\u2019s color
      int currR = (currColor >> 16) & 0xFF;
      int currG = (currColor >> 8) & 0xFF;
      int currB = currColor & 0xFF;
      fill(255);
      pushMatrix();
      translate(currR,currG,currB);
      sphere(20);
      popMatrix();
    }
  }
}

/*******************************************************************************
 *  Image SuperClass for images pulled from the net
 *******************************************************************************/
public class NetImage {
  int imgHeight, imgWidth, imgSize;
  private String imgTitle, imageUrl;
  public Pattern altURL, altHeight, altWidth, altTitle, fileType;
  private HashMap imagesViewed;

  public NetImage(){
    imgSize = 0;
    imagesViewed = new HashMap();
  }

  public void parseImage(String unParsed){

    //Matcher fT = fileType.matcher(unParsed);
    Matcher m1 = altURL.matcher(unParsed);
    Matcher m2 = altHeight.matcher(unParsed);
    Matcher m3 = altWidth.matcher(unParsed);
    Matcher m4 = altTitle.matcher(unParsed);

    // check for Url and file type
    if(m1.find()) {
      //println("URL: " + m1.find());
      if(!m2.find()) imageWidth=PApplet.parseInt(random(300));
      if(!m3.find()) imageWidth=PApplet.parseInt(random(200));
      m4.find();

      int hTemp = Integer.parseInt(m2.group(1));
      int wTemp = Integer.parseInt(m3.group(1));

      // See if this is the biggest image
      if((hTemp*wTemp)>(imgSize)) {
        String tempUrl = m1.group(1);
        if(!imagesViewed.containsKey(tempUrl)){
          imgHeight = hTemp;
          imgWidth = wTemp;
          imageUrl = tempUrl;
          imgTitle = m4.group(1);
          imgSize = hTemp*wTemp;
          imagesViewed.put(imageUrl,imageUrl);
          //println("### Title: " + imgTitle + " found at " + imageUrl + " with " + imgSize + " pixels");
        }
      }
    }
  }

  public String getUrl(){
    return imageUrl;
  }

  public String getTitle(){
    return imgTitle;
  }

  // Reset the largest image size for the next time
  public void reset(){
    imgSize = 0;
  }
}


/*******************************************************************************
 *   For images that are imbedded (somewhat) normally and have an alt tag and dimensions (CNN.com)
 *    ie. <img src="" width="" height="" alt"">
 *******************************************************************************/
public class AltImage extends NetImage {

  public  AltImage(){
    //imgSize = 0;
    altURL = Pattern.compile("src\\s*=\\s*\"(http[^\"\\s]*(jpg|jpeg))\"",    Pattern.CASE_INSENSITIVE);
    altHeight = Pattern.compile("height\\s*=\\s*\"(.*?)\"",    Pattern.CASE_INSENSITIVE);
    altWidth  = Pattern.compile("width\\s*=\\s*\"(.*?)\"",    Pattern.CASE_INSENSITIVE); 
    altTitle  = Pattern.compile("alt\\s*=\\s*\"(.*?)\"",    Pattern.CASE_INSENSITIVE); 
  }

} // End class AltImage

/*******************************************************************************
 *   For images that are imbedded (somewhat) normally and have an alt tag and dimensions (CNN.com)
 *    ie. <img src="" width="" height="" alt"">
 *******************************************************************************/
public class FoxImage extends NetImage {

  public  FoxImage(){
    imgSize = 0;
    altURL = Pattern.compile("src\\s*=\\s*\"(http[^\"\\s]*(jpg|jpeg))\"",    Pattern.CASE_INSENSITIVE);
    altHeight = Pattern.compile("height\\s*=\\s*\"(.*?)\"",    Pattern.CASE_INSENSITIVE);
    altWidth  = Pattern.compile("width\\s*=\\s*\"(.*?)\"",    Pattern.CASE_INSENSITIVE); 
    altTitle  = Pattern.compile("alt\\s*=\\s*\"(.*?)\"",    Pattern.CASE_INSENSITIVE); 
  }

} // End class FoxImage

/*******************************************************************************
 *   For images that are imbedded (somewhat) normally and have an alt tag and dimensions (CNN.com)
 *    ie. <img src="" width="" height="" alt"">
 *******************************************************************************/
public class MSNImage extends NetImage {

  public  MSNImage(){
    imgSize = 0;
    altURL = Pattern.compile("src\\s*=\\s*\"(http[^\"\\s]*(jpg|jpeg))\"",    Pattern.CASE_INSENSITIVE);
    altHeight = Pattern.compile("height\\s*=\\s*\"(.*?)\"",    Pattern.CASE_INSENSITIVE);
    altWidth  = Pattern.compile("width\\s*=\\s*\"(.*?)\"",    Pattern.CASE_INSENSITIVE); 
    altTitle  = Pattern.compile("alt\\s*=\\s*\"(.*?)\"",    Pattern.CASE_INSENSITIVE); 
  }

} // End class MSNImage

/* Daniel Shiffman               */
/* Programming from A to Z       */
/* Spring 2006                   */
/* http://www.shiffman.net       */
/* daniel.shiffman@nyu.edu       */

/* Class to read an input URL    */
/* and return a String           */

//package a2z;




public class PageParser
{
	// Stings to hold url as well as url's source content
	private String urlPath;
	private String content; 
	
	public PageParser(String name) throws IOException {
		urlPath = name;
		readContent();
	}
	
	
	public void readContent() throws IOException {
		// Create an empty StringBuffer
		StringBuffer stuff = new StringBuffer();
		try {
			// Call the openStream method (from below)
			InputStream stream = createInputStream(urlPath);
			//Create a BufferedReader from the InputStream
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
			// Read the stream line by line and append to StringBuffer
			String line;
			while (( line = reader.readLine()) != null) {
				stuff.append(line + "\n");
			}
			// Close the reader 
			reader.close();
		} catch (IOException e) {
			System.out.println("Ooops! Something went wrong in PageParser! " + e);
		}
		// Convert the StringBuffer to a String
		content = stuff.toString();
	}
		
	// A method to create an InputStream from a URL
	public  InputStream createInputStream(String urlpath) {
		InputStream stream = null;
		try {
			URL url = new URL(urlpath);
			stream = url.openStream();
			return stream;
		} catch (MalformedURLException e) {
			System.out.println("Something's wrong with the URL in PageParser:  "+ urlpath + " " + e);
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("In PageParser there's a problem downloading from:  "+ urlpath + " " + e);
			// e.printStackTrace();
		}
		return stream;
	}
	
	public String getContent() {
		return content;
	}
}




}