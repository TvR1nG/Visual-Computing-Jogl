import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_CULL_FACE;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL3.*;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.swing.JFrame;

import Basic.ShaderProg;
import Basic.Transform;
import Basic.Vec4;
import Objects.STeapot;
import Objects.SObject;
import Objects.SSphere;
import Objects.SCylinder;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
/**
 * In this class, lighting and rendering and the use of materials are implemented. 
 * The mouse and keyboard actions are used to operate the object. 
 * Detailed operation instructions will be given in the method definition
 * @author Han 20/11/19
 *
 */
public class VCCW011 extends JFrame{

	final GLCanvas canvas; //Define a canvas 
	final FPSAnimator animator=new FPSAnimator(60, true);
	final Renderer renderer = new Renderer();

	public VCCW011() {
        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities caps = new GLCapabilities(glp);
        canvas = new GLCanvas(caps);
        

        add(canvas, java.awt.BorderLayout.CENTER); // Put the canvas in the frame
		canvas.addGLEventListener(renderer); //Set the canvas to listen GLEvents
		canvas.addKeyListener(renderer);
		canvas.addMouseListener(renderer);
		canvas.addMouseMotionListener(renderer);
		
		animator.add(canvas);

		setTitle("CW 01");
		setSize(500,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		animator.start();
		canvas.requestFocus();
		}

	public static void main(String[] args) {
		new VCCW011();

	}
	
	class Renderer implements GLEventListener, KeyListener, MouseListener, MouseMotionListener{

		private Transform T = new Transform();

		//VAOs and VBOs parameters
		private int idPoint=0, numVAOs = 1;
		private int idBuffer=0, numVBOs = 1;
		private int idElement=0, numEBOs = 1;
		private int[] VAOs = new int[numVAOs];
		private int[] VBOs = new int[numVBOs];
		private int[] EBOs = new int[numEBOs];

		//Model parameters
		private int numElements;
		private int vPosition;
		private int vNormal;

		//Transformation parameters
		private int ModelView;
		private int Projection; 
		private int NormalTransform;
		private float scale = 1;
		private float tx = 0;	//translate x-axis
		private float ty = 0;	//translate y-axis
		private float rx = 0;	//rotate x-axis
		private float ry = 0;	//rotate y-axis
		
		private int xMouse = 0; 
		private int yMouse = 0;
		
		@Override
		public void display(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this 
			
			gl.glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

			gl.glPointSize(5);                                                                                                                                                                                                                                                                                                                                                                                                  
			gl.glLineWidth(5);                                                                                                                                                                                                                                                                                                                                                                                                  

			T.initialize();
			T.scale(0.3f, 0.3f, 0.3f);
			
			//Key control interaction
			T.scale(scale, scale, scale);
			T.rotateX(rx);
			T.rotateY(ry);
			T.translate(tx,ty,0);
			
			
			//Locate camera
			T.lookAt(0, 0, 0, 0, 0, -1, 0, 1, 0);  	//Default					
			
/**	        
			Send the model_view and normal transformation matrix to the shader.
			Here, the parameter "true" for transposition indicates that 
			a row-dominated matrix is transformed into a column-dominated matrix, 
			which is necessary when the position vector of the vertex is multiplied 
			with the model_view matrix in advance. 
			Note that the normal transformation matrix is the inverse transposed matrix 
			of the vertex transformation matrix
*/			
			
			gl.glUniformMatrix4fv( ModelView, 1, true, T.getTransformv(), 0 );			
			gl.glUniformMatrix4fv( NormalTransform, 1, true, T.getInvTransformTv(), 0 );			

		    gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); //default
		    gl.glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_INT, 0);	//for solid teapot
		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void init(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this 
			
			gl.glEnable(GL_PRIMITIVE_RESTART);
			gl.glPrimitiveRestartIndex(0xFFFF);

			gl.glEnable(GL_CULL_FACE); 
			
			SCylinder cylinder = new SCylinder(2);
			float [] vertexArray = cylinder.getVertices();
			float [] normalArray = cylinder.getNormals();
			int [] vertexIndexs =cylinder.getIndices();
			numElements = cylinder.getNumIndices();
			
			gl.glGenVertexArrays(numVAOs,VAOs,0);
			gl.glBindVertexArray(VAOs[idPoint]);

			FloatBuffer vertices = FloatBuffer.wrap(vertexArray);
			FloatBuffer normals = FloatBuffer.wrap(normalArray);
			
			gl.glGenBuffers(numVBOs, VBOs,0);
			gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[idBuffer]);

		    // Create an empty buffer with the size we need 
			// and a null pointer for the data values
			long vertexSize = vertexArray.length*(Float.SIZE/8);
			long normalSize = normalArray.length*(Float.SIZE/8);
			gl.glBufferData(GL_ARRAY_BUFFER, vertexSize +normalSize, 
					null, GL_STATIC_DRAW); // pay attention to *Float.SIZE/8
		    
			// Load the real data separately.  We put the colors right after the vertex coordinates,
		    // so, the offset for colors is the size of vertices in bytes
		    gl.glBufferSubData( GL_ARRAY_BUFFER, 0, vertexSize, vertices );
		    gl.glBufferSubData( GL_ARRAY_BUFFER, vertexSize, normalSize, normals );

			IntBuffer elements = IntBuffer.wrap(vertexIndexs);
			
			gl.glGenBuffers(numEBOs, EBOs,0);
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBOs[idElement]);


			long indexSize = vertexIndexs.length*(Integer.SIZE/8);
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexSize, 
					elements, GL_STATIC_DRAW); // pay attention to *Float.SIZE/8						
			
		    ShaderProg shaderproc = new ShaderProg(gl, "Gouraud.vert", "Gouraud.frag");
			int program = shaderproc.getProgram();
			gl.glUseProgram(program);
			
		   // Initialize the vertex position attribute in the vertex shader    
		    vPosition = gl.glGetAttribLocation( program, "vPosition" );
			gl.glEnableVertexAttribArray(vPosition);
			gl.glVertexAttribPointer(vPosition, 3, GL_FLOAT, false, 0, 0L);

		    // Initialize the vertex color attribute in the vertex shader.
		    // The offset is the same as in the glBufferSubData, i.e., vertexSize
			// It is the starting point of the color data
		    vNormal = gl.glGetAttribLocation( program, "vNormal" );
			gl.glEnableVertexAttribArray(vNormal);
		    gl.glVertexAttribPointer(vNormal, 3, GL_FLOAT, false, 0, vertexSize);

		    //Get connected with the ModelView matrix in the vertex shader
		    ModelView = gl.glGetUniformLocation(program, "ModelView");
		    NormalTransform = gl.glGetUniformLocation(program, "NormalTransform");
		    Projection = gl.glGetUniformLocation(program, "Projection");

		    // Initialize shader lighting parameters
		    float[] lightPosition = {10.0f, 10.0f, -10.0f, 0.0f};
		    Vec4 lightAmbient = new Vec4(1.0f, 1.5f, 1.0f, 1.0f);
		    Vec4 lightDiffuse = new Vec4(1.0f, 1.5f, 1.0f, 1.0f);
		    Vec4 lightSpecular = new Vec4(1.5f, 1.0f, 1.0f, 1.0f);

		    //Ruby material
		    Vec4 materialAmbient = new Vec4(0.1745f, 0.01175f, 0.01175f, 0.55f);
		    Vec4 materialDiffuse = new Vec4(0.61424f, 0.04136f, 0.04136f, 0.55f);
		    Vec4 materialSpecular = new Vec4(0.727811f, 0.626959f, 0.626959f, 0.55f);
		    float  materialShininess = 76.8f;
		    
		    
		    Vec4 ambientProduct = lightAmbient.times(materialAmbient);
		    float[] ambient = ambientProduct.getVector();
		    Vec4 diffuseProduct = lightDiffuse.times(materialDiffuse);
		    float[] diffuse = diffuseProduct.getVector();
		    Vec4 specularProduct = lightSpecular.times(materialSpecular);
		    float[] specular = specularProduct.getVector();

		    gl.glUniform4fv( gl.glGetUniformLocation(program, "AmbientProduct"),
				  1, ambient,0 );
		    gl.glUniform4fv( gl.glGetUniformLocation(program, "DiffuseProduct"),
				  1, diffuse, 0 );
		    gl.glUniform4fv( gl.glGetUniformLocation(program, "SpecularProduct"),
				  1, specular, 0 );
			
		    gl.glUniform4fv( gl.glGetUniformLocation(program, "LightPosition"),
				  1, lightPosition, 0 );

		    gl.glUniform1f( gl.glGetUniformLocation(program, "Shininess"),
				 materialShininess );
				 
		    // This is necessary. Otherwise, the The color on back face may display 
//		    gl.glDepthFunc(GL_LESS);
		    gl.glEnable(GL_DEPTH_TEST);		    
		}
		
		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int w,
				int h) {

			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this 
			
			gl.glViewport(x, y, w, h);

			T.initialize();

			//projection
			if(h<1){h=1;}
			if(w<1){w=1;}			
			float a = (float) w/ h;   //aspect 
			if (w < h) {
				T.ortho(-1, 1, -1/a, 1/a, -1, 1);
			}
			else{
				T.ortho(-1*a, 1*a, -1, 1, -1, 1);
			}
			
			// Convert right-hand to left-hand coordinate system
			T.reverseZ();
		    gl.glUniformMatrix4fv( Projection, 1, true, T.getTransformv(), 0 );
		}
		
/**         
 *  Elsewhere in the program, some parameters are defined 
 *  for conversion and the parameters are calculated in response to certain key events. 
 *  The key 'Q'&'E' or middle button is pressed:scale down the object
 *  rotate the object around X axis clockwise or anti-clockwise
 *  when the key 'S' or 'W' is pressed 
 *  rotate the object around Y axis clockwise or anti-clockwise
 *  when the key 'A' or 'D' is pressed   
 *  Left arrow key is pressed: move the object left 
 *  Right arrow key is pressed: move the object right 
 *  Up arrow key is pressed: move the object up 
 *  Down arrow key is pressed: move the object down 
	*/
		@Override
		public void keyPressed(KeyEvent k) {
			int keyEvent = k.getKeyCode(); 
			switch (keyEvent){
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			case KeyEvent.VK_Q:
				scale *= 1.1;
				break;			
			case KeyEvent.VK_E:
				scale *=0.9;
				break;
			case KeyEvent.VK_LEFT:
				tx -= 0.01;	
				break;
			case KeyEvent.VK_RIGHT:
				tx += 0.01;
				break;
			case KeyEvent.VK_UP:
				ty += 0.01;
				break;
			case KeyEvent.VK_DOWN:
				ty -= 0.01;
				break;
			case KeyEvent.VK_S:
			    rx -= 1;		//1 degree
				break;
			case KeyEvent.VK_W:
				rx += 1;
				break;
			case KeyEvent.VK_A:
				ry -= 1;
				break;
			case KeyEvent.VK_D:
				ry += 1;
				break;
			}								
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			
			//left button down, move the object
			if((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0){
				tx += (x-xMouse) * 0.01;
				ty -= (y-yMouse) * 0.01;
				xMouse = x;
				yMouse = y;
			}
			
			//right button down, rotate the object
			if((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0){
			    ry += (x-xMouse) * 1;
			    rx += (y-yMouse) * 1;
			    xMouse = x;
			    yMouse = y;
			}
			
			//middle button down, scale the object
			if((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0){
			    scale *= Math.pow(1.1, (y-yMouse) * 0.5);
			    xMouse = x;
			    yMouse = y;
			}			
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			xMouse = e.getX();		
			yMouse = e.getY();	
		}
		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void keyTyped(KeyEvent ke) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
}