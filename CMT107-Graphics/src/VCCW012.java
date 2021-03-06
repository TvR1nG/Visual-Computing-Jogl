import static com.jogamp.opengl.GL3.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.swing.JFrame;

import Basic.ShaderProg;
import Basic.Transform;
import Basic.Vec4;
import Objects.SCylinder;
import Objects.SObject;
import Objects.SSphere;
import Objects.STeapot;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
/**
 * Three different materials are implemented in this class, 
 * of which a cylinder is introduced as a new object.
 * @author Han 23/11/19
 *
 */
public class VCCW012 extends JFrame{

	final GLCanvas canvas; //Define a canvas 
	final FPSAnimator animator=new FPSAnimator(60, true);
	final Renderer renderer = new Renderer();

	public VCCW012() {
        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities caps = new GLCapabilities(glp);
        canvas = new GLCanvas(caps);

		add(canvas, java.awt.BorderLayout.CENTER); // Put the canvas in the frame
		canvas.addGLEventListener(renderer); //Set the canvas to listen GLEvents
		
		animator.add(canvas);

		setTitle("CW 02");
		setSize(500,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		animator.start();
		canvas.requestFocus();
		}

	public static void main(String[] args) {
		new VCCW012();

	}

	class Renderer implements GLEventListener {

		private Transform T = new Transform(); //model_view transform

		//VAOs and VBOs parameters
		private int idPoint=0, numVAOs = 3;
		private int idBuffer=0, numVBOs = 3;
		private int idElement=0, numEBOs = 3;
		private int[] VAOs = new int[numVAOs];
		private int[] VBOs = new int[numVBOs];
		private int[] EBOs = new int[numEBOs];

		//Model parameters
		private int[] numElements = new int[numEBOs];
		private long vertexSize; 
		private long normalSize; 
		private int vPosition;
		private int vNormal;

		//Transformation parameters
		private int ModelView;
		private int NormalTransform;
		private int Projection; 

		//Lighting parameter
		private int AmbientProduct;
		private int DiffuseProduct;
		private int SpecularProduct;			
		private int Shininess;

		private float[] ambient1; 
	    private float[] diffuse1;
	    private float[] specular1;
	    private float  materialShininess1;
	    
		private float[] ambient2; 
	    private float[] diffuse2;
	    private float[] specular2;
	    private float  materialShininess2;
	    
	    private float[] ambient3;
	    private float[] diffuse3;
	    private float[] specular3;
	    private float materialShininess3;

		@Override
		public void display(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this 
			
			gl.glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

			//sphere's transformation.
			T.initialize();
			T.scale(0.2f, 0.2f, 0.2f);
			T.rotateX(-90);
			T.translate(0.6f, 0f, 0);				
/*			
			Send model_view and normal transformation matrices to shader. 
			Here parameter 'true' for transpose means to convert the row-major  
			matrix to column major one, which is required when vertices'
			location vectors are pre-multiplied by the model_view matrix.
			Note that the normal transformation matrix is the inverse-transpose
			matrix of the vertex transformation matrix
*/
			gl.glUniformMatrix4fv( ModelView, 1, true, T.getTransformv(), 0 );			
			gl.glUniformMatrix4fv( NormalTransform, 1, true, T.getInvTransformTv(), 0 );			
			
			//send other uniform variables to shader
			gl.glUniform4fv( AmbientProduct, 1, ambient1,0 );
		    gl.glUniform4fv( DiffuseProduct, 1, diffuse1, 0 );
		    gl.glUniform4fv( SpecularProduct, 1, specular1, 0 );			
		    gl.glUniform1f( Shininess, materialShininess1);

			idPoint=0;
			idBuffer=0;
			idElement=0;
			bindObject(gl);
		    gl.glDrawElements(GL_TRIANGLES, numElements[idElement], GL_UNSIGNED_INT, 0);	
		   
		    //cylinder's transformation.
		    T.initialize();
		    T.scale(0.2f, 0.2f, 0.2f);
		    T.rotateX(-90);
		    gl.glUniformMatrix4fv( ModelView, 1, true, T.getTransformv(), 0 );			
			gl.glUniformMatrix4fv( NormalTransform, 1, true, T.getInvTransformTv(), 0 );	
		   
			gl.glUniform4fv( AmbientProduct, 1, ambient2,0 );
		    gl.glUniform4fv( DiffuseProduct, 1, diffuse2, 0 );
		    gl.glUniform4fv( SpecularProduct, 1, specular2, 0 );			
		    gl.glUniform1f( Shininess, materialShininess2);
		    
			idPoint=1;
			idBuffer=1;
			idElement=1;
			bindObject(gl);
		    gl.glDrawElements(GL_TRIANGLES, numElements[idElement], GL_UNSIGNED_INT, 0);
		    
		    
		    //teapot's transformation
		    T.initialize();
		    T.scale(0.1f, 0.1f, 0.1f);
		    T.rotateX(-90);
		    T.translate(-0.6f, 0, 0.5f);
		    gl.glUniformMatrix4fv( ModelView, 1, true, T.getTransformv(), 0 );			
			gl.glUniformMatrix4fv( NormalTransform, 1, true, T.getInvTransformTv(), 0 );	
		   
			gl.glUniform4fv( AmbientProduct, 1, ambient3,0 );
		    gl.glUniform4fv( DiffuseProduct, 1, diffuse3, 0 );
		    gl.glUniform4fv( SpecularProduct, 1, specular3, 0 );			
		    gl.glUniform1f( Shininess, materialShininess3);
		    
			idPoint=2;
			idBuffer=2;
			idElement=2;
			bindObject(gl);
		    gl.glDrawElements(GL_TRIANGLES, numElements[idElement], GL_UNSIGNED_INT, 0);

		}

		
		@Override
		public void init(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this 

			System.out.print("GL_Version: " + gl.glGetString(GL_VERSION));
			
			gl.glEnable(GL_CULL_FACE); 

			//compile and use the shader program
			ShaderProg shaderproc = new ShaderProg(gl, "Gouraud.vert", "Gouraud.frag");
			int program = shaderproc.getProgram();
			gl.glUseProgram(program);

			// Initialize the vertex position and normal attribute in the vertex shader    
		    vPosition = gl.glGetAttribLocation( program, "vPosition" );
		    vNormal = gl.glGetAttribLocation( program, "vNormal" );

		    // Get connected with the ModelView, NormalTransform, and Projection matrices
		    // in the vertex shader
		    ModelView = gl.glGetUniformLocation(program, "ModelView");
		    NormalTransform = gl.glGetUniformLocation(program, "NormalTransform");
		    Projection = gl.glGetUniformLocation(program, "Projection");

		    // Get connected with uniform variables AmbientProduct, DiffuseProduct, 
		    // SpecularProduct, and Shininess in the vertex shader		    
		    AmbientProduct = gl.glGetUniformLocation(program, "AmbientProduct");
		    DiffuseProduct = gl.glGetUniformLocation(program, "DiffuseProduct");
		    SpecularProduct = gl.glGetUniformLocation(program, "SpecularProduct");			
		    Shininess = gl.glGetUniformLocation(program, "Shininess");
			
		    // Generate VAOs, VBOs, and EBOs
		    gl.glGenVertexArrays(numVAOs,VAOs,0);
			gl.glGenBuffers(numVBOs, VBOs,0);
			gl.glGenBuffers(numEBOs, EBOs,0);

		    // Initialize shader lighting parameters
		    float[] lightPosition = {10.0f, 15.0f, 20.0f, 1.0f};
		   
		    Vec4 lightAmbient = new Vec4(0.7f, 0.7f, 0.7f, 1.0f);
		    Vec4 lightDiffuse = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
		    Vec4 lightSpecular = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);

		    gl.glUniform4fv( gl.glGetUniformLocation(program, "LightPosition"),
				  1, lightPosition, 0 );
		    
		    
			//create the first object: a sphere
		    SObject sphere = new SSphere(1,40,40);
			idPoint=0;
			idBuffer=0;
			idElement=0;
			createObject(gl, sphere);

			// set Sphere material
		    Vec4 materialAmbient1 = new Vec4(0.24725f, 0.1995f, 0.0745f, 1.0f);	
		    Vec4 materialDiffuse1 = new Vec4(0.75164f, 0.60648f, 0.22648f, 1.0f);
		    Vec4 materialSpecular1 = new Vec4(0.628281f, 0.555802f, 0.366065f, 1.0f);
		    materialShininess1 = 51.2f;

		    Vec4 ambientProduct = lightAmbient.times(materialAmbient1);
		    ambient1 = ambientProduct.getVector();
		    Vec4 diffuseProduct = lightDiffuse.times(materialDiffuse1);
		    diffuse1 = diffuseProduct.getVector();
		    Vec4 specularProduct = lightSpecular.times(materialSpecular1);
		    specular1 = specularProduct.getVector();

		    //create the second object: a cylinder
		    SObject cylinder = new SCylinder(1,40,40);
			idPoint=1;
			idBuffer=1;
			idElement=1;
			createObject(gl, cylinder);

			 //set cylinder light
			Vec4 lightAmbient2 = new Vec4(1.5f, 1.5f, 1.5f, 1.0f);	
		    Vec4 lightDiffuse2 = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
		    Vec4 lightSpecular2 = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
		    
		   
		     //set cylinder material
			Vec4 materialAmbient2 = new Vec4(0.25f, 0.25f, 0.25f, 1.0f);	
		    Vec4 materialDiffuse2 = new Vec4(0.4f, 0.4f, 0.4f, 1.0f);
		    Vec4 materialSpecular2 = new Vec4(0.774597f, 0.774597f, 0.774597f, 1.0f);
		    materialShininess2 = 76.8f;
		   
		    Vec4 ambientProduct2 = lightAmbient2.times(materialAmbient2);
		    ambient2 = ambientProduct2.getVector();
		    Vec4 diffuseProduct2 = lightDiffuse2.times(materialDiffuse2);
		    diffuse2 = diffuseProduct2.getVector();
		    Vec4 specularProduct2 = lightSpecular2.times(materialSpecular2);
		    specular2 = specularProduct2.getVector();
		    
		    
		   //create the third object: a teapot
		    SObject teapot = new STeapot();
		    idPoint=2;
			idBuffer=2;
			idElement=2;
			createObject(gl, teapot);
		    
		
			Vec4 lightAmbient3 = new Vec4(1.0f, 1.0f, 1.0f, 0.5f);	
			Vec4 lightDiffuse3 = new Vec4(1.0f, 0.7f, 0.7f, 0.7f);
			Vec4 lightSpecular3 = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
		    
			
		   // Set teapot material		    
			Vec4 materialAmbient3 = new Vec4(0.135f, 0.2225f, 0.1575f, 0.95f);	
			Vec4 materialDiffuse3 = new Vec4(0.54f, 0.89f, 0.63f, 0.95f);
			Vec4 materialSpecular3 = new Vec4(0.316228f, 0.316228f, 0.316228f, 0.95f);
			materialShininess3 = 12.8f;
			
			Vec4 ambientProduct3 = lightAmbient3.times(materialAmbient3);
			ambient3 = ambientProduct3.getVector();
		    Vec4 diffuseProduct3 = lightDiffuse3.times(materialDiffuse3);
			diffuse3 = diffuseProduct3.getVector();
		    Vec4 specularProduct3 = lightSpecular3.times(materialSpecular3);
		    specular3 = specularProduct3.getVector();
		
			
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
		
		@Override
		public void dispose(GLAutoDrawable drawable) {
			// TODO Auto-generated method stub
			
		}
		
		public void createObject(GL3 gl, SObject obj) {
			float [] vertexArray = obj.getVertices();
			float [] normalArray = obj.getNormals();
			int [] vertexIndexs =obj.getIndices();
			numElements[idElement] = obj.getNumIndices();

			bindObject(gl);
			
			FloatBuffer vertices = FloatBuffer.wrap(vertexArray);
			FloatBuffer normals = FloatBuffer.wrap(normalArray);
			
		    // Create an empty buffer with the size we need 
			// and a null pointer for the data values
			vertexSize = vertexArray.length*(Float.SIZE/8);
			normalSize = normalArray.length*(Float.SIZE/8);
			gl.glBufferData(GL_ARRAY_BUFFER, vertexSize +normalSize, 
					null, GL_STATIC_DRAW); // pay attention to *Float.SIZE/8
		    
			// Load the real data separately.  We put the colors right after the vertex coordinates,
		    // so, the offset for colors is the size of vertices in bytes
		    gl.glBufferSubData( GL_ARRAY_BUFFER, 0, vertexSize, vertices );
		    gl.glBufferSubData( GL_ARRAY_BUFFER, vertexSize, normalSize, normals );

			IntBuffer elements = IntBuffer.wrap(vertexIndexs);			

			long indexSize = vertexIndexs.length*(Integer.SIZE/8);
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexSize, 
					elements, GL_STATIC_DRAW); // pay attention to *Float.SIZE/8						
			gl.glEnableVertexAttribArray(vPosition);
			gl.glVertexAttribPointer(vPosition, 3, GL_FLOAT, false, 0, 0L);
			gl.glEnableVertexAttribArray(vNormal);
		    gl.glVertexAttribPointer(vNormal, 3, GL_FLOAT, false, 0, vertexSize);
		}

		public void bindObject(GL3 gl){
			gl.glBindVertexArray(VAOs[idPoint]);
			gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[idBuffer]);
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBOs[idElement]);			
		};
	}
}