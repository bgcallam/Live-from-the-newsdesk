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
      rect(0.0, i*yscaleFactor, xscaleFactor, yscaleFactor-thirdY*2);
    }
    rect(0.0, (scaleFactor*yscaleFactor)-thirdY, xscaleFactor, yscaleFactor-thirdY*2);
    // sides of the dashboard
    rect(0.0, 0.0, xscaleFactor/scaleFactor, screenHeight);
    rect(xscaleFactor-(xscaleFactor/scaleFactor), 0.0, xscaleFactor/scaleFactor, screenHeight);    
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
      color currColor = a.pixels[i];
      // Extract the red, green, and blue components of the current pixelÕs color
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
