import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Component;

/**
Input: Image
Output: Harris corner image
Process: 1. Takes a colored image 
		 2. Gets the grayscale matrix of that image by calling ConvertImageToMatrix class
		 3. Calculates xConvolution and yConvolution
		 4. Calculates magnitude from xConvolution and yConvolution and output final image
 @author Han
*/



public class HarisCornerDetection extends Component {
	BufferedImage img, finalImg;
	int[][] imageMatrix;
	int height =0, width =0;
	private WriteImage out;
	
	public HarisCornerDetection(){
		out = WriteImage.getInstance();
	} 
	
	public void Detection(BufferedImage img){
		this.height = img.getHeight();
		this.width = img.getWidth();
		this.img = img;
		imageMatrix = new ConvertImageToGrayScaleMatrix().imageToMatrix(img);
		int[][] xMatrix = xConvolution();
		int[][] yMatrix = yConvolution();

		finalImage(xMatrix, yMatrix);

		computeDerivative(xMatrix, yMatrix);
	}

	public int[][] xConvolution(){
		int[][] xMatrix = new int[width][height];

		BufferedImage image1 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		int x = 0;

		int [][] xMask = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

		for(int i = 1; i < height - 1; i++)
		{
			for(int j = 1; j < width - 1; j++)
			{
				x = xMask[0][0] * imageMatrix[j-1][i-1] + xMask[0][1] * imageMatrix[j][i-1]+ xMask[0][2] * imageMatrix[j+1][i-1]
					+ xMask[1][0] * imageMatrix[j-1][i]+ xMask[1][1] * imageMatrix[j][i]+ xMask[1][2] * imageMatrix[j+1][i]
					+ xMask[2][0] * imageMatrix[j-1][i+1]+ xMask[2][1] * imageMatrix[j][i+1]+ xMask[2][2] * imageMatrix[j+1][i+1];

				x = xyConvolutionValueChecker(x);
				xMatrix[j][i] = x;

				Color c = new Color(x, x ,x);

				image1.setRGB(j, i, c.getRGB());
			}
		}

		return xMatrix;
	}

	public int[][] yConvolution(){
		int[][] yMatrix = new int[width][height];

		BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		int y = 0;

		int [][] yMask = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};

		for(int i = 1; i < height - 1; i++)
		{
			for(int j = 1; j < width - 1; j++)
			{
				y = yMask[0][0] * imageMatrix[j-1][i-1] + yMask[0][1] * imageMatrix[j][i-1]+ yMask[0][2] * imageMatrix[j+1][i-1]
					+ yMask[1][0] * imageMatrix[j-1][i] + yMask[1][1] * imageMatrix[j][i]+ yMask[1][2] * imageMatrix[j+1][i]
					+ yMask[2][0] * imageMatrix[j-1][i+1] + yMask[2][1] * imageMatrix[j][i+1]+ yMask[2][2] * imageMatrix[j+1][i+1];

				y = xyConvolutionValueChecker(y);
				yMatrix[j][i] = y;

				Color c = new Color(y, y ,y);
				image2.setRGB(j, i, c.getRGB());
			}
		}

		return yMatrix;
	}
	
	public void computeDerivative(int[][] xMatrix, int[][] yMatrix){
		int [][] xyMatrix = new int[width][height];
		int [][] xxMatrix = new int[width][height];
		int [][] yyMatrix = new int[width][height];
		int [][] gxyMatrix = new int[width][height];
		int [][] gxxMatrix = new int[width][height];
		int [][] gyyMatrix = new int[width][height];
		
		for(int i = 1; i < height-2; i++)
		{
			for(int j = 1; j < width-2; j++)
			{
				//System.out.print(yMatrix[j][i]+"  ");
				xxMatrix[j][i] = xMatrix[j][i] * xMatrix[j][i];
				yyMatrix[j][i] = yMatrix[j][i] * yMatrix[j][i];
				xyMatrix[j][i] = xMatrix[j][i] * yMatrix[j][i];
			}
			//System.out.println();
		}
		
		int[][] gMat = {{1,4,7,4,1},
					   {4,16,26,16,4},
					   {7,26,41,26,7},
					   {4,16,26,16,4},
					   {1,4,7,4,1}};
		
		for(int i = 2; i < height-4; i++)
		{
			for(int j = 2; j < width-4; j++)
			{
				int m = i-2, temp1=0, temp2=0, temp3=0;
				int n = j-2;
				
				for(int k = 0; k < 5; k++){
					for(int l = 0; l < 5; l++){
						temp1 += gMat[k][l]* xxMatrix[m][n];
						temp2 += gMat[k][l]* yyMatrix[m][n];
						temp3 += gMat[k][l]* xyMatrix[m][n];
					}
				}
				
				temp1 = temp1 / 273;
				temp2 = temp2 / 273;
				temp3 = temp3 / 273;
				
				gxxMatrix[j][i] = pixelValueChecker(temp1);
				gyyMatrix[j][i] = pixelValueChecker(temp2);
				gxyMatrix[j][i] = pixelValueChecker(temp3);
			}
		}
		
		int H, trM, R;
		
		BufferedImage HarrisImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		Color w =new Color(240, 255, 239);
		Color b =new Color(255,0,0);
		
		for(int i = 2; i < height-2; i++)
		{
			for(int j = 2; j < width-2; j++)
			{
				H = (gxxMatrix[j][i] * gyyMatrix[j][i]) - (gxyMatrix[j][i] * gxyMatrix[j][i]);
				trM = gxxMatrix[j][i] + gyyMatrix[j][i];
				R = (int) (H - (.4 * (trM * trM)));
				
				if (R < -100000 || R > 100000)
				{
					HarrisImage.setRGB(j, i, w.getRGB());
				}
				else{
					HarrisImage.setRGB(j, i, b.getRGB());
				}
			}
		}
		repaint();
		out.Write(HarrisImage, "", "HarrisImage.jpg");
	}


	private int pixelValueChecker(int value){
		if(value > 255)
			value = 255;
		else if(value < 0)
			value = 0;
		
		return value;
	}
	
	private int xyConvolutionValueChecker(int value){
		if(value > 50)
		{
			value = 255;
		}
		else if(value < -50)
		{
			value = 255;
		}
		else
		{
			value = 0;
		}
		
		return value;
	}
	
	public void finalImage(int[][] xMatrix, int[][] yMatrix){
		int M, Mx, My;
		finalImg = new BufferedImage( width, height, BufferedImage.TYPE_BYTE_GRAY);
		
		for(int i = 1; i < height-1; i++)
		{
			for(int j = 1; j < width-1; j++)
			{
				Mx = xMatrix[j][i];
				My = yMatrix[j][i];
				M = (int) Math.sqrt(Mx*Mx + My*My);
				
				if(M > 0){
					M = 255;
				}
				else if(M < 0){
					M = 0;
				}
				
				Color c = new Color(M, M, M);
				
				finalImg.setRGB(j, i, c.getRGB());
			}
		}

	}
}
