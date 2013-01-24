import processing.opengl.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import damkjer.ocd.*;

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

void setup() {
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


void draw() {
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

void keyPressed(){
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
