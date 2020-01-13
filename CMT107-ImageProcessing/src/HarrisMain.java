import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *In this class, the corner corner detection of image processing is implemented,
 * which uses the overall framework of lab6, and changes the processing () function.
 * In order to make the detection more accurate,
 * I added a class that converts the image into a gray matrix convertimagetograyscale (),
 * and isolate the class wirteimgae () that produces the final result.
 * tips:
 * Harris detection algorithm in the HarrisCornerDetection.java
 * Process: 1.Run the HarrisMain.java
            2.The program will display the initial picture: checkerboard
            3.Press 'P'
            4.Close the display picture window
            5.The root directory will generate an 'HarrisImage.jpg'
            6.The HarrisImage.jpg is the final result.
 * @author Han
 */
public class HarrisMain extends Component implements KeyListener {

	private BufferedImage img, outimg;
	int width, height;

	public HarrisMain() {
		loadImage();
		addKeyListener(this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	public void paint(Graphics g) {
		g.drawImage(outimg, 0, 0, null);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Image Processing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		HarrisMain img = new HarrisMain();
		frame.add("Center", img);
		frame.pack();
		img.requestFocusInWindow();
		frame.setVisible(true);
	}

	public void processing() {
		HarisCornerDetection cornerDetection = new HarisCornerDetection();
		cornerDetection.Detection(img);
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);

		if (ke.getKeyChar() == 'p' || ke.getKeyChar() == 'P') {// Image Processing
			processing();
		}
	}

	private void loadImage() {
		try {
			img = ImageIO.read(new File("Chess_Board.png"));
			width = img.getWidth();
			height = img.getHeight();
			outimg = img;
		} catch (IOException e) {
			System.out.println("Image could not be read");
			System.exit(1);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}