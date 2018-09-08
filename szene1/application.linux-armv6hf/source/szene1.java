import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import oscP5.*; 
import netP5.*; 
import damkjer.ocd.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class szene1 extends PApplet {





float eyeX=0;
float eyeY=0;
float eyeZ=300;
float centerX=0;
float centerY=0;
float centerZ=0;
//float lightX=200;
//float lightY=120;
//float lightZ=140;
float upX;
float upY;
float upZ;
float rollen;
float neigen;
float schwenken;
float scale;
float transX;
float transY;
float transZ;
float rot;
float gruen;
float blau;
float alpha;
float rotierenX;
float rotierenY;
float rotierenZ;

Icosahedron ico1;

OscP5 oscP5;
NetAddress myRemoteLocation;

Camera camera1;
float cameraZ;

public void setup() {
  exec("sudo", "python", "/home/pi/receive_stepper_und_servo9_wlan0.py");
  oscP5 = new OscP5(this,12000);
  noCursor();
  
  ico1 = new Icosahedron(75);
  cameraZ = (height/2.0f) / tan(PI*60.0f/360.0f);
  camera1 = new Camera(this, eyeX, eyeY, eyeZ,
                             centerX, centerY, centerZ,
                             0, -1, 0,
                             0.112851804f, cameraZ/10, cameraZ*10);
  //0.0424744392 - 100cm
  //0.0847961745 - 200cm
  //0.112851804 - 75cm
  //0.168390157 - 50cm
}

public void draw() {

  camera1.jump(eyeX, eyeY, eyeZ);
  camera1.aim(centerX, centerY, centerZ);
  tilt();
  pan();
  roll();
  camera1.feed();
  
  background(0);
  lights();
  //pointLight(255, 102, 126, lightX, lightY, lightZ);
  
  pushMatrix();
  translate(transX, transY, transZ);
  scale(scale);
  rotateX(rotierenX);
  rotateY(rotierenY);
  rotateZ(rotierenZ);
  noStroke();
  fill(rot,gruen,blau,alpha);
  ico1.create();
  popMatrix();
  
  stroke(255);
  noFill();
  
  
  //translate(0,0,-50);
  scale(scale*0.5f);
  for(int i=-1000; i<1000; i+=2){
   line(i,-1000,i,1000);
  }
  for(int w=-1000; w<1000; w+=2){
   line(-1000,w,1000,w);
  }
  
  /*fill(100, 0, 0);
  noStroke();
  box(2100);*/
}

public void roll(){
  float[] attitude = camera1.attitude();
  if(floor(degrees(attitude[2])-rollen) > 0){
    for(int i = 0; i < abs(floor(degrees(attitude[2])-rollen));i ++){
      camera1.roll(radians(-1));
    }
  }
  if(floor(degrees(attitude[2])-rollen) < 0){
    for(int i = 0; i < abs(rollen-floor(degrees(attitude[2])));i ++){
      camera1.roll(radians(1));
    }
  }
}

public void tilt(){
  float[] attitude = camera1.attitude();
  if(floor(degrees(attitude[1])-neigen) > 0){
    for(int i = 0; i < abs(floor(degrees(attitude[1])-neigen));i ++){
      camera1.tilt(radians(-1));
    }
  }
  if(floor(degrees(attitude[1])-neigen) < 0){
    for(int i = 0; i < abs(neigen-floor(degrees(attitude[1])));i ++){
      camera1.tilt(radians(1));
    }
  }
}

public void pan(){
  float[] attitude = camera1.attitude();
  if(floor(degrees(attitude[0])-schwenken) > 0){
    for(int i = 0; i < abs(floor(degrees(attitude[0])-schwenken));i ++){
      camera1.pan(radians(-1));
    }
  }
  if(floor(degrees(attitude[0])-schwenken) < 0){
    for(int i = 0; i < abs(schwenken-floor(degrees(attitude[0])));i ++){
      camera1.pan(radians(1));
    }
  }
}



public void oscEvent(OscMessage theOscMessage) {
  if(theOscMessage.checkAddrPattern("/eyeX")==true)
  {
    eyeX = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/eyeY")==true)
  {
    eyeY = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/eyeZ")==true)
  {
    eyeZ = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/centerX")==true)
  {
    centerX = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/centerY")==true)
  {
    centerY = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/centerZ")==true)
  {
    centerZ = theOscMessage.get(0).floatValue();
  }
  /*if(theOscMessage.checkAddrPattern("/lightX")==true)
  {
    lightX = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/lightY")==true)
  {
    lightY = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/lightZ")==true)
  {
    lightZ = theOscMessage.get(0).floatValue();
  }*/
  if(theOscMessage.checkAddrPattern("/upX")==true)
  {
    upX = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/upY")==true)
  {
    upY = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/upZ")==true)
  {
    upZ = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/rollen")==true)
  {
    rollen = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/neigen")==true)
  {
    neigen = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/schwenken")==true)
  {
    schwenken = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/scale")==true)
  {
    scale = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/transX")==true)
  {
    transX = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/transY")==true)
  {
    transY = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/transZ")==true)
  {
    transZ = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/rot")==true)
  {
    rot = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/gruen")==true)
  {
    gruen = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/blau")==true)
  {
    blau = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/alpha")==true)
  {
    alpha = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/rotierenX")==true)
  {
    rotierenX = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/rotierenY")==true)
  {
    rotierenY = theOscMessage.get(0).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/rotierenZ")==true)
  {
    rotierenZ = theOscMessage.get(0).floatValue();
  }
  //theOscMessage.print();
}

class Dimension3D{
   float w, h, d;
   
   Dimension3D(float w, float h, float d){
     this.w=w;
     this.h=h;
     this.d=d;
  }
}




abstract class Shape3D{
  float x, y, z;
  float w, h, d;

  Shape3D(){
  }

  Shape3D(float x, float y, float z){
    this.x = x;
    this.y = y;
    this.z = z;
  }

  Shape3D(PVector p){
    x = p.x;
    y = p.y;
    z = p.z;
  }


  Shape3D(Dimension3D dim){
    w = dim.w;
    h = dim.h;
    d = dim.d;
  }

  Shape3D(float x, float y, float z, float w, float h, float d){
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
    this.h = h;
    this.d = d;
  }

  Shape3D(float x, float y, float z, Dimension3D dim){
    this.x = x;
    this.y = y;
    this.z = z;
    w = dim.w;
    h = dim.h;
    d = dim.d;
  }

  Shape3D(PVector p, Dimension3D dim){
    x = p.x;
    y = p.y;
    z = p.z;
    w = dim.w;
    h = dim.h;
    d = dim.d;
  }

  public void setLoc(PVector p){
    x=p.x;
    y=p.y;
    z=p.z;
  }

  public void setLoc(float x, float y, float z){
    this.x=x;
    this.y=y;
    this.z=z;
  }


  // override if you need these
  public void rotX(float theta){
  }

  public void rotY(float theta){
  }

  public void rotZ(float theta){
  }


  // must be implemented in subclasses
  public abstract void init();
  public abstract void create();
}




class Icosahedron extends Shape3D{

  // icosahedron
  PVector topPoint;
  PVector[] topPent = new PVector[5];
  PVector bottomPoint;
  PVector[] bottomPent = new PVector[5];
  float angle = 0, radius = 150;
  float triDist;
  float triHt;
  float a, b, c;

  // constructor
  Icosahedron(float radius){
    this.radius = radius;
    init();
  }

  Icosahedron(PVector v, float radius){
    super(v);
    this.radius = radius;
    init();
  }

  // calculate geometry
  public void init(){
    c = dist(cos(0)*radius, sin(0)*radius, cos(radians(72))*radius,  sin(radians(72))*radius);
    b = radius;
    a = (float)(Math.sqrt(((c*c)-(b*b))));

    triHt = (float)(Math.sqrt((c*c)-((c/2)*(c/2))));

    for (int i=0; i<topPent.length; i++){
      topPent[i] = new PVector(cos(angle)*radius, sin(angle)*radius, triHt/2.0f);
      angle+=radians(72);
    }
    topPoint = new PVector(0, 0, triHt/2.0f+a);
    angle = 72.0f/2.0f;
    for (int i=0; i<topPent.length; i++){
      bottomPent[i] = new PVector(cos(angle)*radius, sin(angle)*radius, -triHt/2.0f);
      angle+=radians(72);
    }
    bottomPoint = new PVector(0, 0, -(triHt/2.0f+a));
  }

  // draws icosahedron 
  public void create(){
    for (int i=0; i<topPent.length; i++){
      // icosahedron top
      beginShape();
      if (i<topPent.length-1){
        vertex(x+topPent[i].x, y+topPent[i].y, z+topPent[i].z);
        vertex(x+topPoint.x, y+topPoint.y, z+topPoint.z);
        vertex(x+topPent[i+1].x, y+topPent[i+1].y, z+topPent[i+1].z);
      } 
      else {
        vertex(x+topPent[i].x, y+topPent[i].y, z+topPent[i].z);
        vertex(x+topPoint.x, y+topPoint.y, z+topPoint.z);
        vertex(x+topPent[0].x, y+topPent[0].y, z+topPent[0].z);
      }
      endShape(CLOSE);

      // icosahedron bottom
      beginShape();
      if (i<bottomPent.length-1){
        vertex(x+bottomPent[i].x, y+bottomPent[i].y, z+bottomPent[i].z);
        vertex(x+bottomPoint.x, y+bottomPoint.y, z+bottomPoint.z);
        vertex(x+bottomPent[i+1].x, y+bottomPent[i+1].y, z+bottomPent[i+1].z);
      } 
      else {
        vertex(x+bottomPent[i].x, y+bottomPent[i].y, z+bottomPent[i].z);
        vertex(x+bottomPoint.x, y+bottomPoint.y, z+bottomPoint.z);
        vertex(x+bottomPent[0].x, y+bottomPent[0].y, z+bottomPent[0].z);
      }
      endShape(CLOSE);
    }

    // icosahedron body
    for (int i=0; i<topPent.length; i++){
      if (i<topPent.length-2){
        beginShape();
        vertex(x+topPent[i].x, y+topPent[i].y, z+topPent[i].z);
        vertex(x+bottomPent[i+1].x, y+bottomPent[i+1].y, z+bottomPent[i+1].z);
        vertex(x+bottomPent[i+2].x, y+bottomPent[i+2].y, z+bottomPent[i+2].z);
        endShape(CLOSE);

        beginShape();
        vertex(x+bottomPent[i+2].x, y+bottomPent[i+2].y, z+bottomPent[i+2].z);
        vertex(x+topPent[i].x, y+topPent[i].y, z+topPent[i].z);
        vertex(x+topPent[i+1].x, y+topPent[i+1].y, z+topPent[i+1].z);
        endShape(CLOSE);
      } 
      else if (i==topPent.length-2){
        beginShape();
        vertex(x+topPent[i].x, y+topPent[i].y, z+topPent[i].z);
        vertex(x+bottomPent[i+1].x, y+bottomPent[i+1].y, z+bottomPent[i+1].z);
        vertex(x+bottomPent[0].x, y+bottomPent[0].y, z+bottomPent[0].z);
        endShape(CLOSE);

        beginShape();
        vertex(x+bottomPent[0].x, y+bottomPent[0].y, z+bottomPent[0].z);
        vertex(x+topPent[i].x, y+topPent[i].y, z+topPent[i].z);
        vertex(x+topPent[i+1].x, y+topPent[i+1].y, z+topPent[i+1].z);
        endShape(CLOSE);
      }
      else if (i==topPent.length-1){
        beginShape();
        vertex(x+topPent[i].x, y+topPent[i].y, z+topPent[i].z);
        vertex(x+bottomPent[0].x, y+bottomPent[0].y, z+bottomPent[0].z);
        vertex(x+bottomPent[1].x, y+bottomPent[1].y, z+bottomPent[1].z);
        endShape(CLOSE);

        beginShape();
        vertex(x+bottomPent[1].x, y+bottomPent[1].y, z+bottomPent[1].z);
        vertex(x+topPent[i].x, y+topPent[i].y, z+topPent[i].z);
        vertex(x+topPent[0].x, y+topPent[0].y, z+topPent[0].z);
        endShape(CLOSE);
      }
    }
  }

  // overrided methods fom Shape3D
  public void rotZ(float theta){
    float tx=0, ty=0, tz=0;
    // top point
    tx = cos(theta)*topPoint.x+sin(theta)*topPoint.y;
    ty = sin(theta)*topPoint.x-cos(theta)*topPoint.y;
    topPoint.x = tx;
    topPoint.y = ty;

    // bottom point
    tx = cos(theta)*bottomPoint.x+sin(theta)*bottomPoint.y;
    ty = sin(theta)*bottomPoint.x-cos(theta)*bottomPoint.y;
    bottomPoint.x = tx;
    bottomPoint.y = ty;

    // top and bottom pentagons
    for (int i=0; i<topPent.length; i++){
      tx = cos(theta)*topPent[i].x+sin(theta)*topPent[i].y;
      ty = sin(theta)*topPent[i].x-cos(theta)*topPent[i].y;
      topPent[i].x = tx;
      topPent[i].y = ty;

      tx = cos(theta)*bottomPent[i].x+sin(theta)*bottomPent[i].y;
      ty = sin(theta)*bottomPent[i].x-cos(theta)*bottomPent[i].y;
      bottomPent[i].x = tx;
      bottomPent[i].y = ty;
    }
  }

  public void rotX(float theta){
  }

  public void rotY(float theta){
  }


}
  public void settings() {  fullScreen(P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "szene1" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
