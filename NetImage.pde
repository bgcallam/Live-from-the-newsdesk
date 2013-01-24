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
      if(!m2.find()) imageWidth=int(random(300));
      if(!m3.find()) imageWidth=int(random(200));
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
