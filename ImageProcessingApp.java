package ml;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import wibd.ml.adapter.TextExtracter;
import wibd.ml.util.AWSClient;

public class ImageProcessingApp {

	public static void main(String[] args) {
		AWSClient awsClient = new AWSClient();
		List<String> objList = awsClient.getObjectslistFromFolder("wibd-ls1", "1891(10401-10600)");
		String url = "https://s3-us-east-2.amazonaws.com/wibd-ls1/";
		TextExtracter extracter = new TextExtracter();
		for(String str : objList) {
			System.out.println("file name is "+str);
			String URL = (url+str).replaceAll(" ", "+");
			  try {
				BufferedImage bufimg = ImageIO.read(new URL(URL));
				extracter.getImgText(bufimg, str);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		extracter.close();
		
	}
}
