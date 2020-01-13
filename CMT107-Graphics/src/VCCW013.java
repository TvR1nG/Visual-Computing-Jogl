import java.awt.DisplayMode;
import java.io.File;
import java.io.IOException;
import static com.jogamp.opengl.GL.GL_BGR;
import static com.jogamp.opengl.GL.GL_RGB;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_UNSIGNED_BYTE;
import static com.jogamp.opengl.GL3.*;

import java.awt.image.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
/**
 * In this class, the operation of applying a picture to a cube is implemented.
 * In order to see the texture style more intuitively, 
 * also added the gl.glrotatef() function to perform the automatic rotation action.
 * This implementation method refers to this book:
 * <<Foundations of 3D graphics programming: Using JOGL and Java3D>> J.Chen & Edward J. Wegman
 * @author Han
 *
 */
public class VCCW013 implements GLEventListener{
   public static DisplayMode dm, dm_old;
   private GLU glu = new GLU();
   private float x,y,z;
   private int texture;
 
   
   @Override
   public void display(GLAutoDrawable drawable) {
      final GL2 gl = drawable.getGL().getGL2();
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);   
      gl.glLoadIdentity();                       // Reset The View
    
      //Rotate automatically
      gl.glTranslatef(0f, 0f, -5.0f);
      gl.glRotatef(x ,1.0f, 1.0f, 1.0f); 
      gl.glRotatef(y, 0.0f, 1.0f, 0.0f);
      gl.glRotatef(z, 0.0f, 0.0f, 1.0f);
      gl.glBindTexture(GL2.GL_TEXTURE_2D, texture);
      gl.glBegin(GL2.GL_QUADS);//start to draw a cube with gl method
      
      // Front Face
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);//quad's bottom left
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);//bottom right
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);//quad's top right
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);//top left
      // Back Face
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);//bottom right
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);//top right
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);//top left
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);//bottom left
      
      // Top Face
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);//top left
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);//bottom left
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);//bottom right
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);//top right
      
      // Bottom Face
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);//bottom left
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);//bottom right
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);//top right
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);//top left
      
      // Right face
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);//bottom right
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);//top right
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);//top left
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);//bottom left
      
      // Left Face
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);//bottom left
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);//bottom right
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);//top right
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);//top left
      gl.glEnd();//The operation of drawing the cube is complete
      gl.glFlush();
      
      //change the speeds here  
      x+=0.05f;
      y+=0.05f;
      z+=0.05f;
   }
   @Override
   public void dispose(GLAutoDrawable drawable) {
      // method body
   }
   @Override
   public void init(GLAutoDrawable drawable) {
      final GL2 gl = drawable.getGL().getGL2();
      gl.glShadeModel(GL2.GL_SMOOTH);
      gl.glClearColor(0f, 0f, 0f, 0f);
      gl.glClearDepth(1.0f);
      gl.glEnable(GL2.GL_DEPTH_TEST);
      gl.glDepthFunc(GL2.GL_LEQUAL);
      gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
      gl.glEnable(GL2.GL_TEXTURE_2D);//creating a texture variable
      try
      {
         File dragon = new File("PnWelshDragon.jpg");
         Texture t = TextureIO.newTexture(dragon, true);
         texture= t.getTextureObject(gl);
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
   }
   @Override
   public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      final GL2 gl = drawable.getGL().getGL2();
      if(height <=0)
         height =1;
      final float h = (float) width / (float) height;
      gl.glViewport(0, 0, width, height);
      gl.glMatrixMode(GL2.GL_PROJECTION);
      gl.glLoadIdentity();
      glu.gluPerspective(45.0f, h, 1.0, 20.0);
      gl.glMatrixMode(GL2.GL_MODELVIEW);
      gl.glLoadIdentity();
   }
   public static void main(String[] args) {
	  //Getting the capabilities object of GL2 profile
      final GLProfile profile = GLProfile.get(GLProfile.GL2);
      GLCapabilities capabilities = new GLCapabilities(profile);
      
      // The canvas 
      final GLCanvas canvas = new GLCanvas(capabilities);
      VCCW013 t = new VCCW013();
      canvas.addGLEventListener(t);
      canvas.setSize(400, 400);
      
      //Create the frame by instantiating the Frame class Object of JSE AWT frame component.
      //Add canvas to it and make the frame visible.
      final JFrame frame = new JFrame (" VC 03");
      frame.getContentPane().add(canvas);
      frame.setSize(frame.getContentPane().getPreferredSize());
      frame.setVisible(true);
      final FPSAnimator animator = new FPSAnimator(canvas, 300,true );
      animator.start();
   }
}
